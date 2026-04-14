package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.common.PageResult;
import com.library.entity.BorrowOrder;

import java.util.List;
import java.util.Map;

/**
 * 借阅订单服务接口
 */
public interface BorrowOrderService extends IService<BorrowOrder> {
    
    PageResult<BorrowOrder> getOrderPage(Integer page, Integer size, String orderNo, Long borrowerId, 
                                         Long bookId, Integer status, Integer depositStatus, Integer payStatus);
    
    BorrowOrder getOrderById(Long id);
    
    boolean createOrder(BorrowOrder order);
    
    boolean returnOrder(Long id);
    
    Map<String, Object> getOrderStats();
    
    List<Map<String, Object>> getMonthlyStats();
}
