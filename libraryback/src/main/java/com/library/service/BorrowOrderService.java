package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.common.PageResult;
import com.library.entity.BorrowOrder;

public interface BorrowOrderService extends IService<BorrowOrder> {
    
    PageResult<BorrowOrder> getOrderPage(Integer page, Integer size, String orderNo, Long borrowerId, 
                                          Long bookId, Integer status, Integer depositStatus, Integer paymentStatus);
    
    BorrowOrder getOrderDetail(Long id);
    
    boolean createOrder(Long borrowerId, Long bookId, Integer borrowDays, java.math.BigDecimal depositAmount);
    
    boolean returnBook(Long orderId);
    
    boolean refundDeposit(Long orderId);
    
    boolean updatePaymentStatus(Long orderId, Integer paymentStatus);
    
    Integer getBorrowingCount(Long borrowerId);
    
    Integer getOverdueCount();
}
