package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.DepositRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

@Mapper
public interface DepositRecordMapper extends BaseMapper<DepositRecord> {
    
    Page<DepositRecord> selectDepositPage(Page<DepositRecord> page,
                                           @Param("borrowerId") Long borrowerId,
                                           @Param("type") Integer type,
                                           @Param("status") Integer status);
    
    @Select("SELECT COALESCE(SUM(amount), 0) FROM deposit_record WHERE borrower_id = #{borrowerId} AND type = 1 AND status = 1 AND deleted = 0")
    BigDecimal selectTotalIncome(@Param("borrowerId") Long borrowerId);
    
    @Select("SELECT COALESCE(SUM(amount), 0) FROM deposit_record WHERE borrower_id = #{borrowerId} AND type = 2 AND status = 1 AND deleted = 0")
    BigDecimal selectTotalExpense(@Param("borrowerId") Long borrowerId);
}
