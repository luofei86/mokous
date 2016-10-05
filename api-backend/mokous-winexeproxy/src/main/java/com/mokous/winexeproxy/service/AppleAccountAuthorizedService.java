// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.winexeproxy.service;

import com.mokous.web.exception.ServiceException;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
public interface AppleAccountAuthorizedService {
    public boolean authPcByExe(String appleId, String pwd, String ip, int port, boolean createSession)
            throws ServiceException;

    public String requestAuthroizedInfo(String appleId, String authJson, String ikma, String ikmb)
            throws ServiceException;
}
