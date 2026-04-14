package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.DepositRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 押金明细Mapper接口
 */
@Mapper
public interface DepositRecordMapper extends BaseMapper<DepositRecord> {
    
    /**
     * 分页查询押金明细
     */
    Page<DepositRecord> selectDepositPage(Page<DepositRecord> page, 
                                          @Param("borrowerId") Long borrowerId, 
                                          @Param("type") Integer type);
    
    /**
     * 查询借阅人员的最新余额
     */
    DepositRecord selectLatestRecord(@Param("borrowerId") Long borrowerId);
}
