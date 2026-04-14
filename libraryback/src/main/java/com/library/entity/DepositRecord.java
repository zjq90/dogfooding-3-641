package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("deposit_record")
public class DepositRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long borrowerId;
    
    private String borrowerName;
    
    private BigDecimal amount;
    
    private Integer type;
    
    private Integer status;
    
    private String remark;
    
    private Long operatorId;
    
    private String operatorName;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    @TableField(select = false)
    private Integer deleted;
}
