package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.common.PageResult;
import com.library.entity.DepositRecord;

/**
 * 押金明细服务接口
 */
public interface DepositRecordService extends IService<DepositRecord> {
    
    PageResult<DepositRecord> getDepositPage(Integer page, Integer size, Long borrowerId, Integer type);
    
    boolean addDepositRecord(DepositRecord record);
}
