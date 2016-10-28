// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.iauth.service;

import com.mokous.core.cache.appleaccount.login.AuthActionResponse;
import com.mokous.iauth.model.AppleIdAuthParameter;
import com.mokous.web.exception.ServiceException;
import com.mokous.web.model.ApiRespWrapper;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月6日
 */
public interface AppleAccountLoginPureProxyService {
    public static final int LOGIN_SUCCESS = 0;

    ApiRespWrapper<AuthActionResponse> login(AppleIdAuthParameter param) throws ServiceException;
}
