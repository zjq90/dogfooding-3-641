package com.library.controller;

import com.library.common.PageResult;
import com.library.common.Result;
import com.library.entity.DepositRecord;
import com.library.entity.User;
import com.library.mapper.UserMapper;
import com.library.service.DepositRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/deposit")
public class DepositRecordController {

    @Autowired
    private DepositRecordService depositRecordService;
    
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/page")
    public Result<PageResult<DepositRecord>> getDepositPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long borrowerId,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        
        PageResult<DepositRecord> result = depositRecordService.getDepositPage(page, size, borrowerId, type, status);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<DepositRecord> getDepositById(@PathVariable Long id) {
        DepositRecord record = depositRecordService.getById(id);
        if (record != null) {
            return Result.success(record);
        }
        return Result.error("记录不存在");
    }

    @PostMapping
    public Result<Void> addDepositRecord(
            @RequestParam Long borrowerId,
            @RequestParam BigDecimal amount,
            @RequestParam Integer type,
            @RequestParam(required = false) String remark,
            @RequestAttribute Long userId) {
        
        log.info("新增押金记录: borrowerId={}, amount={}, type={}", borrowerId, amount, type);
        
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("金额必须大于0");
        }
        
        User operator = userMapper.selectById(userId);
        String operatorName = operator != null ? operator.getRealName() : "系统";
        
        boolean success = depositRecordService.addDepositRecord(borrowerId, amount, type, remark, userId, operatorName);
        if (success) {
            log.info("押金记录添加成功");
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }

    @GetMapping("/total-deposit/{borrowerId}")
    public Result<BigDecimal> getTotalDeposit(@PathVariable Long borrowerId) {
        BigDecimal total = depositRecordService.getTotalDeposit(borrowerId);
        return Result.success(total);
    }

    @GetMapping("/total-refund/{borrowerId}")
    public Result<BigDecimal> getTotalRefund(@PathVariable Long borrowerId) {
        BigDecimal total = depositRecordService.getTotalRefund(borrowerId);
        return Result.success(total);
    }
}
