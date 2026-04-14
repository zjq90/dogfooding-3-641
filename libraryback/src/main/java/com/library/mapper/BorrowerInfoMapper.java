package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.BorrowerInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 借阅人员信息Mapper接口
 */
@Mapper
public interface BorrowerInfoMapper extends BaseMapper<BorrowerInfo> {
    
    /**
     * 分页查询借阅人员
     */
    Page<BorrowerInfo> selectBorrowerPage(Page<BorrowerInfo> page, 
                                          @Param("name") String name, 
                                          @Param("phone") String phone, 
                                          @Param("status") Integer status);
    
    /**
     * 根据手机号查询借阅人员
     */
    @Select("SELECT * FROM borrower_info WHERE phone = #{phone} AND deleted = 0")
    BorrowerInfo selectByPhone(@Param("phone") String phone);
    
    /**
     * 更新押金余额
     */
    @Update("UPDATE borrower_info SET deposit_amount = deposit_amount + #{amount} WHERE id = #{id}")
    int updateDepositAmount(@Param("id") Long id, @Param("amount") java.math.BigDecimal amount);
}
