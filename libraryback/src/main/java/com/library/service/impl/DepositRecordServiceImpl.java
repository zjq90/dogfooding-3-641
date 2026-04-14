package com.library.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.PageResult;
import com.library.entity.DepositRecord;
import com.library.mapper.DepositRecordMapper;
import com.library.service.DepositRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 押金明细服务实现类
 */
@Slf4j
@Service
public class DepositRecordServiceImpl extends ServiceImpl<DepositRecordMapper, DepositRecord> implements DepositRecordService {
    
    @Override
    public PageResult<DepositRecord> getDepositPage(Integer page, Integer size, Long borrowerId, Integer type) {
        Page<DepositRecord> pageParam = new Page<>(page, size);
        Page<DepositRecord> recordPage = baseMapper.selectDepositPage(pageParam, borrowerId, type);
        return new PageResult<>(recordPage.getTotal(), recordPage.getRecords(),
                                recordPage.getCurrent(), recordPage.getSize());
    }
    
    @Override
    public boolean addDepositRecord(DepositRecord record) {
        boolean success = this.save(record);
        if (success) {
            log.info("新增押金记录成功: 用户{}", record.getBorrowerId());
        }
        return success;
    }
}
