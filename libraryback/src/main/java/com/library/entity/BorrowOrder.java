package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("borrow_order")
public class BorrowOrder {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orderNo;
    
    private Long borrowerId;
    
    private String borrowerName;
    
    private String borrowerPhone;
    
    private Long bookId;
    
    private String bookTitle;
    
    private String bookIsbn;
    
    private LocalDate borrowDate;
    
    private LocalDate dueDate;
    
    private LocalDate returnDate;
    
    private BigDecimal depositAmount;
    
    private Integer depositStatus;
    
    private LocalDateTime depositReturnTime;
    
    private Integer paymentStatus;
    
    private LocalDateTime paymentTime;
    
    private Integer status;
    
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    @TableField(select = false)
    private Integer deleted;
}
