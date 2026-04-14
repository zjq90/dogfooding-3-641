package com.library.controller;

import com.library.common.PageResult;
import com.library.common.Result;
import com.library.entity.BorrowOrder;
import com.library.service.BorrowOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 借阅订单控制器
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class BorrowOrderController {
    
    @Autowired
    private BorrowOrderService borrowOrderService;
    
    /**
     * 分页查询借阅订单
     */
    @GetMapping("/page")
    public Result<PageResult<BorrowOrder>> getOrderPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Long borrowerId,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer depositStatus,
            @RequestParam(required = false) Integer payStatus) {
        
        PageResult<BorrowOrder> result = borrowOrderService.getOrderPage(page, size, orderNo, borrowerId, 
                                                                          bookId, status, depositStatus, payStatus);
        return Result.success(result);
    }
    
    /**
     * 根据ID获取订单详情
     */
    @GetMapping("/{id}")
    public Result<BorrowOrder> getOrderById(@PathVariable Long id) {
        BorrowOrder order = borrowOrderService.getOrderById(id);
        if (order != null) {
            return Result.success(order);
        }
        return Result.error("订单不存在");
    }
    
    /**
     * 创建借阅订单
     */
    @PostMapping
    public Result<Void> createOrder(@RequestBody BorrowOrder order) {
        log.info("创建借阅订单: 用户{}, 图书{}", order.getBorrowerId(), order.getBookId());
        
        boolean success = borrowOrderService.createOrder(order);
        if (success) {
            return Result.success("创建成功");
        }
        return Result.error("创建失败");
    }
    
    /**
     * 归还订单
     */
    @PutMapping("/{id}/return")
    public Result<Void> returnOrder(@PathVariable Long id) {
        log.info("归还订单: {}", id);
        
        boolean success = borrowOrderService.returnOrder(id);
        if (success) {
            return Result.success("归还成功");
        }
        return Result.error("归还失败");
    }
    
    /**
     * 获取订单统计
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getOrderStats() {
        Map<String, Object> stats = borrowOrderService.getOrderStats();
        return Result.success(stats);
    }
    
    /**
     * 获取月度订单统计
     */
    @GetMapping("/stats/monthly")
    public Result<List<Map<String, Object>>> getMonthlyStats() {
        List<Map<String, Object>> stats = borrowOrderService.getMonthlyStats();
        return Result.success(stats);
    }
}
