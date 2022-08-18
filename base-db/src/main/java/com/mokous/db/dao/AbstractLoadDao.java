//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.db.dao;

import com.mokous.base.exception.BizException;
import com.mokous.db.utils.SQLParameterUtils;
import com.mokous.db.utils.SQLUtils;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author luofei
 * Generate 2020/1/12
 */
public abstract class AbstractLoadDao<T> implements LoadDao<T> {
    @Override
    public List<T> queryList(int startId, int size) throws BizException {
        Map<String, Object> paras = SQLParameterUtils.buildStartIdSizeMap(startId, size);
        SQLParameterUtils.initTableNameToMap(paras, getTableName());
        try {
            return SQLUtils.queryList(getSqlSessionFactory(), getQueryByStartId(), paras);
        } catch (Exception e) {
            throw BizException.getSqlException();
        }
    }
    @Override
    public List<T> queryList(long startId, int size) throws BizException {
        Map<String, Object> paras = SQLParameterUtils.buildStartIdSizeMap(startId, size);
        SQLParameterUtils.initTableNameToMap(paras, getTableName());
        try {
            return SQLUtils.queryList(getSqlSessionFactory(), getQueryByStartId(), paras);
        } catch (Exception e) {
            throw BizException.getSqlException();
        }
    }

    protected abstract String getQueryByStartId();

    protected abstract SqlSessionFactory getSqlSessionFactory();

    protected String getTableName() {
        return "";
    }
}
