package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.Borrower;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BorrowerMapper extends BaseMapper<Borrower> {
    
    @Select("SELECT * FROM borrower WHERE phone = #{phone} AND deleted = 0")
    Borrower selectByPhone(@Param("phone") String phone);
    
    @Select("SELECT * FROM borrower WHERE id_card = #{idCard} AND deleted = 0")
    Borrower selectByIdCard(@Param("idCard") String idCard);
    
    Page<Borrower> selectBorrowerPage(Page<Borrower> page,
                                       @Param("name") String name,
                                       @Param("phone") String phone,
                                       @Param("status") Integer status);
}
