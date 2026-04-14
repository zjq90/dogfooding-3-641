package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.common.PageResult;
import com.library.entity.BorrowerInfo;

import java.math.BigDecimal;

/**
 * 借阅人员信息服务接口
 */
public interface BorrowerInfoService extends IService<BorrowerInfo> {
    
    PageResult<BorrowerInfo> getBorrowerPage(Integer page, Integer size, String name, String phone, Integer status);
    
    BorrowerInfo getBorrowerById(Long id);
    
    boolean addBorrower(BorrowerInfo borrower);
    
    boolean updateBorrower(BorrowerInfo borrower);
    
    boolean deleteBorrower(Long id);
    
    boolean addDeposit(Long borrowerId, BigDecimal amount, String remark);
    
    boolean refundDeposit(Long borrowerId, BigDecimal amount, String remark);
}
