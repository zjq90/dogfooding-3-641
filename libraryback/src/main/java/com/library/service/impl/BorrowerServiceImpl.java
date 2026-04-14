package com.library.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.BusinessException;
import com.library.common.PageResult;
import com.library.entity.Borrower;
import com.library.mapper.BorrowerMapper;
import com.library.service.BorrowerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
public class BorrowerServiceImpl extends ServiceImpl<BorrowerMapper, Borrower> implements BorrowerService {

    @Override
    public PageResult<Borrower> getBorrowerPage(Integer page, Integer size, String name, String phone, Integer status) {
        Page<Borrower> pageParam = new Page<>(page, size);
        Page<Borrower> borrowerPage = baseMapper.selectBorrowerPage(pageParam, name, phone, status);
        return new PageResult<>(borrowerPage.getTotal(), borrowerPage.getRecords(),
                                borrowerPage.getCurrent(), borrowerPage.getSize());
    }

    @Override
    public Borrower getByPhone(String phone) {
        return baseMapper.selectByPhone(phone);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addBorrower(Borrower borrower) {
        Borrower existByPhone = baseMapper.selectByPhone(borrower.getPhone());
        if (existByPhone != null) {
            throw new BusinessException("手机号已存在");
        }
        
        if (borrower.getIdCard() != null && !borrower.getIdCard().isEmpty()) {
            Borrower existByIdCard = baseMapper.selectByIdCard(borrower.getIdCard());
            if (existByIdCard != null) {
                throw new BusinessException("身份证号已存在");
            }
        }
        
        if (borrower.getDeposit() == null) {
            borrower.setDeposit(BigDecimal.ZERO);
        }
        if (borrower.getStatus() == null) {
            borrower.setStatus(1);
        }
        if (borrower.getGender() == null) {
            borrower.setGender(1);
        }
        
        boolean success = this.save(borrower);
        if (success) {
            log.info("新增借阅人员成功: {}", borrower.getName());
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBorrower(Borrower borrower) {
        Borrower existBorrower = this.getById(borrower.getId());
        if (existBorrower == null) {
            throw new BusinessException("借阅人员不存在");
        }
        
        if (borrower.getPhone() != null && !borrower.getPhone().equals(existBorrower.getPhone())) {
            Borrower existByPhone = baseMapper.selectByPhone(borrower.getPhone());
            if (existByPhone != null) {
                throw new BusinessException("手机号已存在");
            }
        }
        
        if (borrower.getIdCard() != null && !borrower.getIdCard().equals(existBorrower.getIdCard())) {
            Borrower existByIdCard = baseMapper.selectByIdCard(borrower.getIdCard());
            if (existByIdCard != null) {
                throw new BusinessException("身份证号已存在");
            }
        }
        
        boolean success = this.updateById(borrower);
        if (success) {
            log.info("更新借阅人员成功: {}", borrower.getId());
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDeposit(Long borrowerId, BigDecimal amount, Integer type) {
        Borrower borrower = this.getById(borrowerId);
        if (borrower == null) {
            throw new BusinessException("借阅人员不存在");
        }
        
        BigDecimal currentDeposit = borrower.getDeposit() != null ? borrower.getDeposit() : BigDecimal.ZERO;
        BigDecimal newDeposit;
        
        if (type == 1) {
            newDeposit = currentDeposit.add(amount);
        } else {
            if (currentDeposit.compareTo(amount) < 0) {
                throw new BusinessException("押金余额不足");
            }
            newDeposit = currentDeposit.subtract(amount);
        }
        
        borrower.setDeposit(newDeposit);
        boolean success = this.updateById(borrower);
        if (success) {
            log.info("更新借阅人员押金成功: borrowerId={}, type={}, amount={}", borrowerId, type, amount);
        }
        return success;
    }
}
