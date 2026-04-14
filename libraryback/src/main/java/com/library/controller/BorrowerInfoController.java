package com.library.controller;

import com.library.common.PageResult;
import com.library.common.Result;
import com.library.entity.BorrowerInfo;
import com.library.service.BorrowerInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 借阅人员信息控制器
 */
@Slf4j
@RestController
@RequestMapping("/borrower")
public class BorrowerInfoController {
    
    @Autowired
    private BorrowerInfoService borrowerInfoService;
    
    /**
     * 分页查询借阅人员
     */
    @GetMapping("/page")
    public Result<PageResult<BorrowerInfo>> getBorrowerPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer status) {
        
        PageResult<BorrowerInfo> result = borrowerInfoService.getBorrowerPage(page, size, name, phone, status);
        return Result.success(result);
    }
    
    /**
     * 根据ID获取借阅人员详情
     */
    @GetMapping("/{id}")
    public Result<BorrowerInfo> getBorrowerById(@PathVariable Long id) {
        BorrowerInfo borrower = borrowerInfoService.getBorrowerById(id);
        if (borrower != null) {
            return Result.success(borrower);
        }
        return Result.error("借阅人员不存在");
    }
    
    /**
     * 新增借阅人员
     */
    @PostMapping
    public Result<Void> addBorrower(@RequestBody BorrowerInfo borrower) {
        log.info("新增借阅人员: {}", borrower.getName());
        
        boolean success = borrowerInfoService.addBorrower(borrower);
        if (success) {
            return Result.success("新增成功");
        }
        return Result.error("新增失败");
    }
    
    /**
     * 更新借阅人员
     */
    @PutMapping("/{id}")
    public Result<Void> updateBorrower(@PathVariable Long id, @RequestBody BorrowerInfo borrower) {
        log.info("更新借阅人员: {}", id);
        
        borrower.setId(id);
        boolean success = borrowerInfoService.updateBorrower(borrower);
        if (success) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }
    
    /**
     * 删除借阅人员
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteBorrower(@PathVariable Long id) {
        log.info("删除借阅人员: {}", id);
        
        boolean success = borrowerInfoService.deleteBorrower(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
    
    /**
     * 缴纳押金
     */
    @PostMapping("/{id}/deposit")
    public Result<Void> addDeposit(
            @PathVariable Long id,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String remark) {
        
        log.info("缴纳押金: 用户{}, 金额{}", id, amount);
        
        boolean success = borrowerInfoService.addDeposit(id, amount, remark);
        if (success) {
            return Result.success("缴纳成功");
        }
        return Result.error("缴纳失败");
    }
    
    /**
     * 退还押金
     */
    @PostMapping("/{id}/refund")
    public Result<Void> refundDeposit(
            @PathVariable Long id,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String remark) {
        
        log.info("退还押金: 用户{}, 金额{}", id, amount);
        
        boolean success = borrowerInfoService.refundDeposit(id, amount, remark);
        if (success) {
            return Result.success("退还成功");
        }
        return Result.error("退还失败");
    }
    
    /**
     * 获取所有借阅人员（用于下拉选择）
     */
    @GetMapping("/list")
    public Result<List<BorrowerInfo>> getBorrowerList() {
        List<BorrowerInfo> list = borrowerInfoService.list();
        return Result.success(list);
    }
}
