package com.library.controller;

import com.library.common.PageResult;
import com.library.common.Result;
import com.library.entity.DepositRecord;
import com.library.service.DepositRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 押金明细控制器
 */
@Slf4j
@RestController
@RequestMapping("/deposit")
public class DepositRecordController {
    
    @Autowired
    private DepositRecordService depositRecordService;
    
    /**
     * 分页查询押金明细
     */
    @GetMapping("/page")
    public Result<PageResult<DepositRecord>> getDepositPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long borrowerId,
            @RequestParam(required = false) Integer type) {
        
        PageResult<DepositRecord> result = depositRecordService.getDepositPage(page, size, borrowerId, type);
        return Result.success(result);
    }
    
    /**
     * 根据ID获取押金明细详情
     */
    @GetMapping("/{id}")
    public Result<DepositRecord> getDepositById(@PathVariable Long id) {
        DepositRecord record = depositRecordService.getById(id);
        if (record != null) {
            return Result.success(record);
        }
        return Result.error("记录不存在");
    }
}
