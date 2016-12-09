// Copyright 2015 www.refanqie.com Inc. All Rights Reserved.

package com.mokous.apple.core.service.account;

import com.mokous.apple.core.dto.account.AppleAccount;
import com.mokous.core.service.AbsCommonDataService;
import com.mokous.web.exception.ServiceException;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月6日
 */
public abstract class AppleAccountService extends AbsCommonDataService<AppleAccount> {

    public abstract AppleAccount getAppleAccountByEmail(String email) throws ServiceException;
    // public abstract AppleAccount randomeAppleAccount(int bindAccountId)
    // throws ServiceException;
    //
    // public abstract AppleAccount getAppleAccount(int bindAccountId) throws
    // ServiceException;
    //
    // public abstract List<AppleAccount> getAppleAccount(Integer source,
    // Integer status, Boolean bind, int start, int size)
    // throws ServiceException;
    //
    // public abstract long countAppleAccount(Integer source, Integer status,
    // Boolean bind) throws ServiceException;
    //
    // public abstract AppleAccount getAppleAccountByEmail(String email) throws
    // ServiceException;
    //
    // public abstract AppleAccount generateAppleAccount(String email, String
    // uid) throws ServiceException;
    //
    // public abstract void afterLogin(AppleAccount appleAccount, Integer
    // accountId, boolean loginSuccess);
}
