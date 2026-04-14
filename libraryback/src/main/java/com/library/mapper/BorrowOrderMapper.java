package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.BorrowOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

@Mapper
public interface BorrowOrderMapper extends BaseMapper<BorrowOrder> {
    
    Page<BorrowOrder> selectOrderPage(Page<BorrowOrder> page,
                                       @Param("orderNo") String orderNo,
                                       @Param("borrowerId") Long borrowerId,
                                       @Param("bookId") Long bookId,
                                       @Param("status") Integer status,
                                       @Param("depositStatus") Integer depositStatus,
                                       @Param("paymentStatus") Integer paymentStatus);
    
    @Select("SELECT bo.*, b.name as borrower_name, bi.title as book_title, bi.isbn as book_isbn " +
            "FROM borrow_order bo " +
            "LEFT JOIN borrower b ON bo.borrower_id = b.id " +
            "LEFT JOIN book_info bi ON bo.book_id = bi.id " +
            "WHERE bo.id = #{id} AND bo.deleted = 0")
    BorrowOrder selectOrderDetail(@Param("id") Long id);
    
    @Select("SELECT COUNT(*) FROM borrow_order WHERE borrower_id = #{borrowerId} AND status = 0 AND deleted = 0")
    Integer selectBorrowingCount(@Param("borrowerId") Long borrowerId);
    
    @Select("SELECT COUNT(*) FROM borrow_order WHERE status = 0 AND due_date < #{today} AND deleted = 0")
    Integer selectOverdueCount(@Param("today") LocalDate today);
}
