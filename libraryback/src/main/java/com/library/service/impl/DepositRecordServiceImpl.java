package com.library.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.BusinessException;
import com.library.common.PageResult;
import com.library.entity.Borrower;
import com.library.entity.DepositRecord;
import com.library.mapper.BorrowerMapper;
import com.library.mapper.DepositRecordMapper;
import com.library.service.BorrowerService;
import com.library.service.DepositRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
public class DepositRecordServiceImpl extends ServiceImpl<DepositRecordMapper, DepositRecord> implements DepositRecordService {

    @Autowired
    private BorrowerService borrowerService;
    
    @Autowired
    private BorrowerMapper borrowerMapper;

    @Override
    public PageResult<DepositRecord> getDepositPage(Integer page, Integer size, Long borrowerId, Integer type, Integer status) {
        Page<DepositRecord> pageParam = new Page<>(page, size);
        Page<DepositRecord> depositPage = baseMapper.selectDepositPage(pageParam, borrowerId, type, status);
        return new PageResult<>(depositPage.getTotal(), depositPage.getRecords(),
                                depositPage.getCurrent(), depositPage.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addDepositRecord(Long borrowerId, BigDecimal amount, Integer type, String remark, Long operatorId, String operatorName) {
        Borrower borrower = borrowerMapper.selectById(borrowerId);
        if (borrower == null) {
            throw new BusinessException("借阅人员不存在");
        }
        
        DepositRecord record = new DepositRecord();
        record.setBorrowerId(borrowerId);
        record.setBorrowerName(borrower.getName());
        record.setAmount(amount);
        record.setType(type);
        record.setStatus(1);
        record.setRemark(remark);
        record.setOperatorId(operatorId);
        record.setOperatorName(operatorName);
        
        boolean success = this.save(record);
        if (success) {
            borrowerService.updateDeposit(borrowerId, amount, type);
            log.info("新增押金记录成功: borrowerId={}, type={}, amount={}", borrowerId, type, amount);
        }
        return success;
    }

    @Override
    public BigDecimal getTotalDeposit(Long borrowerId) {
        return baseMapper.selectTotalIncome(borrowerId);
    }

    @Override
    public BigDecimal getTotalRefund(Long borrowerId) {
        return baseMapper.selectTotalExpense(borrowerId);
    }
}
