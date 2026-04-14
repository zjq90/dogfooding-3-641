package com.library.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.BusinessException;
import com.library.common.PageResult;
import com.library.entity.BookInfo;
import com.library.entity.BorrowOrder;
import com.library.entity.Borrower;
import com.library.mapper.BookInfoMapper;
import com.library.mapper.BorrowOrderMapper;
import com.library.mapper.BorrowerMapper;
import com.library.service.BorrowOrderService;
import com.library.service.BorrowerService;
import com.library.service.DepositRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class BorrowOrderServiceImpl extends ServiceImpl<BorrowOrderMapper, BorrowOrder> implements BorrowOrderService {

    @Autowired
    private BorrowerMapper borrowerMapper;
    
    @Autowired
    private BookInfoMapper bookInfoMapper;
    
    @Autowired
    private BorrowerService borrowerService;
    
    @Autowired
    private DepositRecordService depositRecordService;

    @Override
    public PageResult<BorrowOrder> getOrderPage(Integer page, Integer size, String orderNo, Long borrowerId, 
                                                 Long bookId, Integer status, Integer depositStatus, Integer paymentStatus) {
        Page<BorrowOrder> pageParam = new Page<>(page, size);
        Page<BorrowOrder> orderPage = baseMapper.selectOrderPage(pageParam, orderNo, borrowerId, bookId, status, depositStatus, paymentStatus);
        return new PageResult<>(orderPage.getTotal(), orderPage.getRecords(),
                                orderPage.getCurrent(), orderPage.getSize());
    }

    @Override
    public BorrowOrder getOrderDetail(Long id) {
        return baseMapper.selectOrderDetail(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createOrder(Long borrowerId, Long bookId, Integer borrowDays, BigDecimal depositAmount) {
        Borrower borrower = borrowerMapper.selectById(borrowerId);
        if (borrower == null) {
            throw new BusinessException("借阅人员不存在");
        }
        if (borrower.getStatus() != 1) {
            throw new BusinessException("借阅人员已被禁用");
        }
        
        BookInfo book = bookInfoMapper.selectById(bookId);
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        if (book.getAvailableQuantity() <= 0) {
            throw new BusinessException("图书库存不足");
        }
        
        Integer borrowingCount = getBorrowingCount(borrowerId);
        if (borrowingCount >= 5) {
            throw new BusinessException("借阅人员已达到最大借阅数量限制(5本)");
        }
        
        BigDecimal actualDeposit = depositAmount != null ? depositAmount : BigDecimal.ZERO;
        if (actualDeposit.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal currentDeposit = borrower.getDeposit() != null ? borrower.getDeposit() : BigDecimal.ZERO;
            if (currentDeposit.compareTo(actualDeposit) < 0) {
                throw new BusinessException("借阅人押金余额不足，当前余额: " + currentDeposit + "，需要: " + actualDeposit);
            }
        }
        
        book.setAvailableQuantity(book.getAvailableQuantity() - 1);
        if (book.getAvailableQuantity() == 0) {
            book.setStatus(2);
        }
        bookInfoMapper.updateById(book);
        
        BorrowOrder order = new BorrowOrder();
        order.setOrderNo(generateOrderNo());
        order.setBorrowerId(borrowerId);
        order.setBorrowerName(borrower.getName());
        order.setBorrowerPhone(borrower.getPhone());
        order.setBookId(bookId);
        order.setBookTitle(book.getTitle());
        order.setBookIsbn(book.getIsbn());
        order.setBorrowDate(LocalDate.now());
        order.setDueDate(LocalDate.now().plusDays(borrowDays));
        order.setDepositAmount(actualDeposit);
        order.setDepositStatus(0);
        order.setPaymentStatus(0);
        order.setStatus(0);
        
        boolean success = this.save(order);
        if (success) {
            if (actualDeposit.compareTo(BigDecimal.ZERO) > 0) {
                depositRecordService.addDepositRecord(borrowerId, actualDeposit, 2, 
                    "借阅订单扣款，订单号: " + order.getOrderNo(), null, "系统");
            }
            log.info("创建借阅订单成功: orderNo={}, borrowerId={}, bookId={}, depositAmount={}", 
                order.getOrderNo(), borrowerId, bookId, actualDeposit);
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean returnBook(Long orderId) {
        BorrowOrder order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态不正确，无法归还");
        }
        
        BookInfo book = bookInfoMapper.selectById(order.getBookId());
        if (book != null) {
            book.setAvailableQuantity(book.getAvailableQuantity() + 1);
            if (book.getStatus() == 2) {
                book.setStatus(1);
            }
            bookInfoMapper.updateById(book);
        }
        
        order.setReturnDate(LocalDate.now());
        if (LocalDate.now().isAfter(order.getDueDate())) {
            order.setStatus(2);
            log.warn("图书归还逾期，订单号: {}, 应还日期: {}", order.getOrderNo(), order.getDueDate());
        } else {
            order.setStatus(1);
        }
        
        boolean success = this.updateById(order);
        if (success) {
            log.info("归还图书成功，订单号: {}", order.getOrderNo());
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean refundDeposit(Long orderId) {
        BorrowOrder order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getDepositStatus() == 1) {
            throw new BusinessException("押金已退还");
        }
        
        BigDecimal depositAmount = order.getDepositAmount();
        if (depositAmount != null && depositAmount.compareTo(BigDecimal.ZERO) > 0) {
            depositRecordService.addDepositRecord(order.getBorrowerId(), depositAmount, 1, 
                "借阅订单退还押金，订单号: " + order.getOrderNo(), null, "系统");
        }
        
        order.setDepositStatus(1);
        order.setDepositReturnTime(LocalDateTime.now());
        
        boolean success = this.updateById(order);
        if (success) {
            log.info("退还押金成功，订单号: {}, 退还金额: {}", order.getOrderNo(), depositAmount);
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePaymentStatus(Long orderId, Integer paymentStatus) {
        BorrowOrder order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        order.setPaymentStatus(paymentStatus);
        if (paymentStatus == 1) {
            order.setPaymentTime(LocalDateTime.now());
        }
        
        boolean success = this.updateById(order);
        if (success) {
            log.info("更新支付状态成功，订单号: {}, 支付状态: {}", order.getOrderNo(), paymentStatus);
        }
        return success;
    }

    @Override
    public Integer getBorrowingCount(Long borrowerId) {
        return baseMapper.selectBorrowingCount(borrowerId);
    }

    @Override
    public Integer getOverdueCount() {
        return baseMapper.selectOverdueCount(LocalDate.now());
    }
    
    private String generateOrderNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "BO" + dateStr + random;
    }
}
