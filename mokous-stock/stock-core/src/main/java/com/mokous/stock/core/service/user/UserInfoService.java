// Copyright 2016 ios.appchina.com Inc. All Rights Reserved.

package com.mokous.stock.core.service.user;

import com.mokous.core.service.AbsCommonDataService;
import com.mokous.stock.core.dto.user.UserInfo;
import com.mokous.web.exception.ServiceException;

/**
 * @author luofei@appchina.com create date: Nov 27, 2016
 *
 */
public abstract class UserInfoService extends AbsCommonDataService<UserInfo> {
    public abstract int getUserId(String uid) throws ServiceException;
}
