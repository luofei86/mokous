// Copyright 2015 www.refanqie.com Inc. All Rights Reserved.

package com.mokous.account.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mokous.account.dao.AppleAccountMachineInfoDao;
import com.mokous.core.dao.CommonDao;
import com.mokous.core.dto.account.AppleAccountMachineInfo;
import com.mokous.core.service.account.AppleAccountMachineInfoService;

/**
 * @author luofei@refanqie.com (Your Name Here)
 *
 */
@Service("appleAccountMachineInfoService")
public class AppleAccountMachineInfoServiceImpl extends AppleAccountMachineInfoService {
    @Autowired
    private AppleAccountMachineInfoDao appleAccountMachineInfoDao;

    @Override
    public CommonDao<AppleAccountMachineInfo> getCommonDao() {
        return appleAccountMachineInfoDao;
    }

}
