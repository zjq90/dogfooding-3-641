package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.common.PageResult;
import com.library.entity.DepositRecord;

import java.math.BigDecimal;

public interface DepositRecordService extends IService<DepositRecord> {
    
    PageResult<DepositRecord> getDepositPage(Integer page, Integer size, Long borrowerId, Integer type, Integer status);
    
    boolean addDepositRecord(Long borrowerId, BigDecimal amount, Integer type, String remark, Long operatorId, String operatorName);
    
    BigDecimal getTotalDeposit(Long borrowerId);
    
    BigDecimal getTotalRefund(Long borrowerId);
}
