// Copyright 2015 www.refanqie.com Inc. All Rights Reserved.

package com.mokous.account.dao;

import java.sql.SQLException;
import java.util.List;

import com.mokous.core.dao.AbsCommonDao;
import com.mokous.core.dto.account.AppleAccount;

/**
 * @author luofei@refanqie.com (Your Name Here)
 *
 */
public abstract class AppleAccountDao extends AbsCommonDao<AppleAccount> {

    public abstract void updateBindAccountId(AppleAccount data) throws SQLException;

    public abstract void updateAppleAccountUid(AppleAccount data) throws SQLException;

    public abstract List<AppleAccount> queryAppleAccount(Integer source, Integer status, Boolean bind, int start,
            int size) throws SQLException;

    public abstract long countAppleAccount(Integer source, Integer status, Boolean bind) throws SQLException;

    public abstract AppleAccount queryByEmail(String email) throws SQLException;

    public abstract void updateLoginStatus(AppleAccount appleAccount) throws SQLException;

    public abstract void updateLoginStatusAndBindeAccountId(AppleAccount appleAccount) throws SQLException;
}
