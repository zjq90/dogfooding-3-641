package com.library.controller;

import com.library.common.PageResult;
import com.library.common.Result;
import com.library.entity.BorrowOrder;
import com.library.service.BorrowOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/borrow-order")
public class BorrowOrderController {

    @Autowired
    private BorrowOrderService borrowOrderService;

    @GetMapping("/page")
    public Result<PageResult<BorrowOrder>> getOrderPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Long borrowerId,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer depositStatus,
            @RequestParam(required = false) Integer paymentStatus) {
        
        PageResult<BorrowOrder> result = borrowOrderService.getOrderPage(page, size, orderNo, borrowerId, bookId, status, depositStatus, paymentStatus);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<BorrowOrder> getOrderById(@PathVariable Long id) {
        BorrowOrder order = borrowOrderService.getOrderDetail(id);
        if (order != null) {
            return Result.success(order);
        }
        return Result.error("订单不存在");
    }

    @PostMapping
    public Result<Void> createOrder(
            @RequestParam Long borrowerId,
            @RequestParam Long bookId,
            @RequestParam(defaultValue = "30") Integer borrowDays,
            @RequestParam(required = false) BigDecimal depositAmount) {
        
        log.info("创建借阅订单: borrowerId={}, bookId={}, borrowDays={}", borrowerId, bookId, borrowDays);
        
        boolean success = borrowOrderService.createOrder(borrowerId, bookId, borrowDays, depositAmount);
        if (success) {
            log.info("借阅订单创建成功");
            return Result.success("创建成功");
        }
        return Result.error("创建失败");
    }

    @PutMapping("/{id}/return")
    public Result<Void> returnBook(@PathVariable Long id) {
        log.info("归还图书，订单ID: {}", id);
        
        boolean success = borrowOrderService.returnBook(id);
        if (success) {
            log.info("归还成功，订单ID: {}", id);
            return Result.success("归还成功");
        }
        return Result.error("归还失败");
    }

    @PutMapping("/{id}/refund-deposit")
    public Result<Void> refundDeposit(@PathVariable Long id) {
        log.info("退还押金，订单ID: {}", id);
        
        boolean success = borrowOrderService.refundDeposit(id);
        if (success) {
            log.info("押金退还成功，订单ID: {}", id);
            return Result.success("退还成功");
        }
        return Result.error("退还失败");
    }

    @PutMapping("/{id}/payment-status")
    public Result<Void> updatePaymentStatus(@PathVariable Long id, @RequestParam Integer paymentStatus) {
        log.info("更新支付状态，订单ID: {}, 支付状态: {}", id, paymentStatus);
        
        boolean success = borrowOrderService.updatePaymentStatus(id, paymentStatus);
        if (success) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    @GetMapping("/count")
    public Result<Map<String, Object>> getOrderCount() {
        Map<String, Object> result = new HashMap<>();
        result.put("overdueCount", borrowOrderService.getOverdueCount());
        return Result.success(result);
    }
}
