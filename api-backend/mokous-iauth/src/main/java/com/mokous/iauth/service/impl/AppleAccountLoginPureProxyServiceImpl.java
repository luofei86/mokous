// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.iauth.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mokous.core.cache.appleaccount.login.AuthActionResponse;
import com.mokous.core.utils.ReturnDataHandleUtils;
import com.mokous.iauth.model.AppleIdAuthParameter;
import com.mokous.iauth.service.AppleAccountLoginPureProxyService;
import com.mokous.web.exception.ServiceException;
import com.mokous.web.model.ApiRespWrapper;
import com.mokous.web.model.ParametersHandle;
import com.mokous.web.utils.RemoteDataUtil;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月6日
 */
@Service("appleAccountLoginPureProxyService")
public class AppleAccountLoginPureProxyServiceImpl implements AppleAccountLoginPureProxyService {
    private static final Logger log = Logger.getLogger(AppleAccountLoginPureProxyServiceImpl.class);
    @Value("${ios.auth.authorizer.winexe.proxy.apple.account.login.api}")
    private String loginApi = "http://10.18.0.36:8080/ios-winexe-proxy/account/authorizer/login.json";
    private static final int APPLE_ACCOUNT_TIMEOUT = 35000;

    @Override
    public ApiRespWrapper<AuthActionResponse> login(AppleIdAuthParameter param) throws ServiceException {
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("email", param.getEmail());
        try {
            return RemoteDataUtil.get(loginApi, param, ParametersHandle.PS_HANDLE,
                    ReturnDataHandleUtils.APPLEIDAUTH_RD_HANDLE, false, APPLE_ACCOUNT_TIMEOUT, headerMap);
        } catch (Exception e) {
            log.error("Auth account failed.Param:" + param + ", errMsg:" + e.getMessage(), e);
        }
        return null;
    }
}
