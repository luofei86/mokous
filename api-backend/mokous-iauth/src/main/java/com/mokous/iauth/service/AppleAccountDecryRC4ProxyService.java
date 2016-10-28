// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.iauth.service;

import com.mokous.core.cache.appleaccount.login.AccountInfo;
import com.mokous.web.exception.ServiceException;
import com.mokous.web.model.ApiRespWrapper;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月6日
 */
public interface AppleAccountDecryRC4ProxyService {
    ApiRespWrapper<AccountInfo> login(String email, String passwd, String guid, boolean readFromCache)
            throws ServiceException;

    String decryAndDecode(String data);

}
