package com.library.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.BusinessException;
import com.library.common.PageResult;
import com.library.entity.BookInfo;
import com.library.entity.BorrowOrder;
import com.library.entity.BorrowerInfo;
import com.library.mapper.BorrowOrderMapper;
import com.library.service.BookInfoService;
import com.library.service.BorrowOrderService;
import com.library.service.BorrowerInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 借阅订单服务实现类
 */
@Slf4j
@Service
public class BorrowOrderServiceImpl extends ServiceImpl<BorrowOrderMapper, BorrowOrder> implements BorrowOrderService {
    
    @Autowired
    private BookInfoService bookInfoService;
    
    @Autowired
    private BorrowerInfoService borrowerInfoService;
    
    @Override
    public PageResult<BorrowOrder> getOrderPage(Integer page, Integer size, String orderNo, Long borrowerId, 
                                                 Long bookId, Integer status, Integer depositStatus, Integer payStatus) {
        Page<BorrowOrder> pageParam = new Page<>(page, size);
        Page<BorrowOrder> orderPage = baseMapper.selectOrderPage(pageParam, orderNo, borrowerId, bookId, 
                                                                  status, depositStatus, payStatus);
        return new PageResult<>(orderPage.getTotal(), orderPage.getRecords(),
                                orderPage.getCurrent(), orderPage.getSize());
    }
    
    @Override
    public BorrowOrder getOrderById(Long id) {
        return baseMapper.selectById(id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createOrder(BorrowOrder order) {
        BorrowerInfo borrower = borrowerInfoService.getById(order.getBorrowerId());
        if (borrower == null) {
            throw new BusinessException("借阅人员不存在");
        }
        
        BookInfo book = bookInfoService.getById(order.getBookId());
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        
        if (book.getAvailableQuantity() <= 0) {
            throw new BusinessException("图书库存不足");
        }
        
        if (order.getDepositAmount() != null && order.getDepositAmount().compareTo(BigDecimal.ZERO) > 0) {
            if (borrower.getDepositAmount().compareTo(order.getDepositAmount()) < 0) {
                throw new BusinessException("押金余额不足，请先充值");
            }
        }
        
        order.setOrderNo(generateOrderNo());
        order.setBorrowDate(LocalDate.now());
        order.setStatus(0);
        order.setDepositStatus(order.getDepositAmount() != null && order.getDepositAmount().compareTo(BigDecimal.ZERO) > 0 ? 1 : 0);
        order.setPayStatus(1);
        
        boolean borrowSuccess = bookInfoService.borrowBook(order.getBookId(), null);
        if (!borrowSuccess) {
            throw new BusinessException("借阅失败，图书库存更新失败");
        }
        
        boolean saveSuccess = this.save(order);
        
        if (saveSuccess) {
            log.info("创建借阅订单成功: {}", order.getOrderNo());
        }
        
        return saveSuccess;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean returnOrder(Long id) {
        BorrowOrder order = this.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        if (order.getStatus() == 1) {
            throw new BusinessException("该订单已归还");
        }
        
        boolean returnSuccess = bookInfoService.returnBook(order.getBookId(), null);
        if (!returnSuccess) {
            throw new BusinessException("归还失败，图书库存更新失败");
        }
        
        order.setReturnDate(LocalDate.now());
        order.setStatus(1);
        
        if (LocalDate.now().isAfter(order.getDueDate())) {
            order.setStatus(2);
            log.warn("订单归还逾期: {}", order.getOrderNo());
        }
        
        if (order.getDepositAmount() != null && order.getDepositAmount().compareTo(BigDecimal.ZERO) > 0) {
            order.setDepositStatus(2);
        }
        
        boolean updateSuccess = this.updateById(order);
        
        if (updateSuccess) {
            log.info("订单归还成功: {}", order.getOrderNo());
        }
        
        return updateSuccess;
    }
    
    @Override
    public Map<String, Object> getOrderStats() {
        return baseMapper.selectOrderStats();
    }
    
    @Override
    public List<Map<String, Object>> getMonthlyStats() {
        return baseMapper.selectMonthlyStats();
    }
    
    private String generateOrderNo() {
        return "B" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) 
               + String.format("%04d", (int)(Math.random() * 10000));
    }
}
