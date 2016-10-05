// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.winexeproxy.service;

import com.mokous.core.cache.appleaccount.login.AccountInfo;
import com.mokous.core.cache.appleaccount.login.MachineInfo;


/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
public interface AppleAccountLoginService {

    AccountInfo login(String appleId, String pwd, String ip, Integer port, MachineInfo machineInfo,
            boolean createSession);
}
