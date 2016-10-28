// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.iauth.service;

import com.mokous.core.cache.appleaccount.login.AccountInfo;
import com.mokous.web.exception.ServiceException;
import com.mokous.web.model.ApiRespWrapper;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月6日
 */
public interface AppleAccountLoginProxyService {

    ApiRespWrapper<AccountInfo> login(String email, String passwd, String uid, Integer accountId, boolean refresh,
            boolean needAuth, boolean appleSeession, boolean needConfirmTerm) throws ServiceException;

    boolean verifyAppleAccount(String email, String passwd, String uid, Integer accountId, boolean appleSession,
            boolean needConfirmTerm) throws ServiceException;

}
