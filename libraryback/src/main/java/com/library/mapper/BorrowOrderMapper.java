package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.BorrowOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 借阅订单Mapper接口
 */
@Mapper
public interface BorrowOrderMapper extends BaseMapper<BorrowOrder> {
    
    /**
     * 分页查询借阅订单
     */
    Page<BorrowOrder> selectOrderPage(Page<BorrowOrder> page, 
                                      @Param("orderNo") String orderNo,
                                      @Param("borrowerId") Long borrowerId, 
                                      @Param("bookId") Long bookId,
                                      @Param("status") Integer status,
                                      @Param("depositStatus") Integer depositStatus,
                                      @Param("payStatus") Integer payStatus);
    
    /**
     * 根据订单号查询
     */
    @Select("SELECT * FROM borrow_order WHERE order_no = #{orderNo} AND deleted = 0")
    BorrowOrder selectByOrderNo(@Param("orderNo") String orderNo);
    
    /**
     * 获取订单统计
     */
    Map<String, Object> selectOrderStats();
    
    /**
     * 获取月度订单统计
     */
    List<Map<String, Object>> selectMonthlyStats();
    
    /**
     * 获取逾期未还订单
     */
    List<BorrowOrder> selectOverdueOrders(@Param("currentDate") java.time.LocalDate currentDate);
}
