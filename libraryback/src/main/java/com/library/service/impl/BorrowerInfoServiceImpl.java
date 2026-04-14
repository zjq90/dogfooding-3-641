package com.library.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.BusinessException;
import com.library.common.PageResult;
import com.library.entity.BorrowerInfo;
import com.library.entity.DepositRecord;
import com.library.mapper.BorrowerInfoMapper;
import com.library.service.BorrowerInfoService;
import com.library.service.DepositRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 借阅人员信息服务实现类
 */
@Slf4j
@Service
public class BorrowerInfoServiceImpl extends ServiceImpl<BorrowerInfoMapper, BorrowerInfo> implements BorrowerInfoService {
    
    @Autowired
    private DepositRecordService depositRecordService;
    
    @Override
    public PageResult<BorrowerInfo> getBorrowerPage(Integer page, Integer size, String name, String phone, Integer status) {
        Page<BorrowerInfo> pageParam = new Page<>(page, size);
        Page<BorrowerInfo> borrowerPage = baseMapper.selectBorrowerPage(pageParam, name, phone, status);
        return new PageResult<>(borrowerPage.getTotal(), borrowerPage.getRecords(),
                                borrowerPage.getCurrent(), borrowerPage.getSize());
    }
    
    @Override
    public BorrowerInfo getBorrowerById(Long id) {
        return this.getById(id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addBorrower(BorrowerInfo borrower) {
        BorrowerInfo exist = baseMapper.selectByPhone(borrower.getPhone());
        if (exist != null) {
            throw new BusinessException("该手机号已存在");
        }
        
        borrower.setDepositAmount(BigDecimal.ZERO);
        boolean success = this.save(borrower);
        
        if (success) {
            log.info("新增借阅人员成功: {}", borrower.getName());
        }
        
        return success;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBorrower(BorrowerInfo borrower) {
        BorrowerInfo exist = baseMapper.selectByPhone(borrower.getPhone());
        if (exist != null && !exist.getId().equals(borrower.getId())) {
            throw new BusinessException("该手机号已被其他用户使用");
        }
        
        boolean success = this.updateById(borrower);
        
        if (success) {
            log.info("更新借阅人员成功: {}", borrower.getId());
        }
        
        return success;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBorrower(Long id) {
        BorrowerInfo borrower = this.getById(id);
        if (borrower == null) {
            throw new BusinessException("借阅人员不存在");
        }
        
        if (borrower.getDepositAmount() != null && borrower.getDepositAmount().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException("该用户还有押金余额，请先退还押金");
        }
        
        boolean success = this.removeById(id);
        
        if (success) {
            log.info("删除借阅人员成功: {}", id);
        }
        
        return success;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addDeposit(Long borrowerId, BigDecimal amount, String remark) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("缴纳金额必须大于0");
        }
        
        BorrowerInfo borrower = this.getById(borrowerId);
        if (borrower == null) {
            throw new BusinessException("借阅人员不存在");
        }
        
        BigDecimal newBalance = borrower.getDepositAmount().add(amount);
        
        DepositRecord record = new DepositRecord();
        record.setBorrowerId(borrowerId);
        record.setAmount(amount);
        record.setType(1);
        record.setBalance(newBalance);
        record.setOrderNo(generateOrderNo());
        record.setRemark(remark);
        
        boolean recordSuccess = depositRecordService.save(record);
        boolean updateSuccess = baseMapper.updateDepositAmount(borrowerId, amount) > 0;
        
        if (recordSuccess && updateSuccess) {
            log.info("缴纳押金成功: 用户{}, 金额{}", borrowerId, amount);
            return true;
        }
        
        throw new BusinessException("缴纳押金失败");
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean refundDeposit(Long borrowerId, BigDecimal amount, String remark) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("退还金额必须大于0");
        }
        
        BorrowerInfo borrower = this.getById(borrowerId);
        if (borrower == null) {
            throw new BusinessException("借阅人员不存在");
        }
        
        if (borrower.getDepositAmount().compareTo(amount) < 0) {
            throw new BusinessException("退还金额不能超过押金余额");
        }
        
        BigDecimal newBalance = borrower.getDepositAmount().subtract(amount);
        
        DepositRecord record = new DepositRecord();
        record.setBorrowerId(borrowerId);
        record.setAmount(amount.negate());
        record.setType(2);
        record.setBalance(newBalance);
        record.setOrderNo(generateOrderNo());
        record.setRemark(remark);
        
        boolean recordSuccess = depositRecordService.save(record);
        boolean updateSuccess = baseMapper.updateDepositAmount(borrowerId, amount.negate()) > 0;
        
        if (recordSuccess && updateSuccess) {
            log.info("退还押金成功: 用户{}, 金额{}", borrowerId, amount);
            return true;
        }
        
        throw new BusinessException("退还押金失败");
    }
    
    private String generateOrderNo() {
        return "D" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) 
               + String.format("%04d", (int)(Math.random() * 10000));
    }
}
