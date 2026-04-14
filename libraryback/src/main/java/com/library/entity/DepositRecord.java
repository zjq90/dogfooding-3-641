package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 押金明细实体类
 * 记录借阅人员押金的缴纳和退还记录
 */
@Data
@TableName("deposit_record")
public class DepositRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long borrowerId;
    
    @TableField(exist = false)
    private String borrowerName;
    
    @TableField(exist = false)
    private String borrowerPhone;
    
    private BigDecimal amount;
    
    private Integer type;
    
    private BigDecimal balance;
    
    private String orderNo;
    
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
