// Copyright 2014 www.refanqie.com Inc. All Rights Reserved.

package com.mokous.core.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.mokous.core.utils.SQLUtils;

/**
 * @author luofei@refanqie.com (Your Name Here)
 *
 */
public abstract class AbsLoadDao<T> implements LoadDao<T> {

    @Override
    public List<T> queryList(int startId, int size) throws SQLException {
        Map<String, Object> paras = new HashMap<String, Object>();
        paras.put("startId", startId);
        paras.put("size", size);
        return SQLUtils.queryList(getSqlMapClient(), getqueryByStartId(), paras);
    }

    protected abstract String getqueryByStartId();

    protected abstract SqlMapClient getSqlMapClient();

}
