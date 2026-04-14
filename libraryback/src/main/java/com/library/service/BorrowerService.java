package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.common.PageResult;
import com.library.entity.Borrower;

public interface BorrowerService extends IService<Borrower> {
    
    PageResult<Borrower> getBorrowerPage(Integer page, Integer size, String name, String phone, Integer status);
    
    Borrower getByPhone(String phone);
    
    boolean addBorrower(Borrower borrower);
    
    boolean updateBorrower(Borrower borrower);
    
    boolean updateDeposit(Long borrowerId, java.math.BigDecimal amount, Integer type);
}
