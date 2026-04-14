package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 借阅订单实体类
 * 记录借阅订单信息，包含押金状态和支付状态
 */
@Data
@TableName("borrow_order")
public class BorrowOrder {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orderNo;
    
    private Long borrowerId;
    
    @TableField(exist = false)
    private String borrowerName;
    
    @TableField(exist = false)
    private String borrowerPhone;
    
    private Long bookId;
    
    @TableField(exist = false)
    private String bookTitle;
    
    @TableField(exist = false)
    private String bookIsbn;
    
    private LocalDate borrowDate;
    
    private LocalDate dueDate;
    
    private LocalDate returnDate;
    
    private BigDecimal depositAmount;
    
    private Integer depositStatus;
    
    private Integer payStatus;
    
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
