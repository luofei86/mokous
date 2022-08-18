//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.db.dao;

import com.mokous.base.domain.model.DbFields;
import com.mokous.base.domain.model.ORDER_KEY;
import com.mokous.base.exception.BizException;
import com.mokous.base.utils.NormalUtils;
import com.mokous.db.enums.EnumSqlConditionalSymbol;
import com.mokous.db.utils.DaoHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author luofei
 * Generate 2020/1/12
 */
public abstract class AbstractCommonDao<G> implements CommonDao<G> {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractCommonDao.class);

    private String genericClassSimpleName;

    @Override
    public List<G> queryList(G g, int start, int size) throws BizException {
        try {
            return DaoHelper.queryList(getTableName(), g, start, size, this);
        } catch (Exception e) {
            throw BizException.getSqlException();
        }
    }

    @Override
    public List<G> queryList(G g, int start, int size, String orderColumn, boolean orderByDesc) throws BizException {
        try {
            return DaoHelper.queryList(getTableName(), g, start, size, this, orderColumn, orderByDesc);
        } catch (Exception e) {
            throw BizException.getSqlException();
        }
    }

    @Override
    public List<G> queryList(int startId, int size) throws BizException {
        try {
            return DaoHelper.queryList(startId, size, this);
        } catch (Exception e) {
            throw BizException.getSqlException();
        }
    }

    @Override
    public <M> G queryObject(M id) throws BizException {
        try {
            return DaoHelper.queryObject(id, this);
        } catch (Exception e) {
            throw BizException.getSqlException();
        }
    }

    @Override
    public long count(G g) throws BizException {
        try {
            return DaoHelper.count(g, this);
        } catch (Exception e) {
            throw BizException.getSqlException();
        }
    }

    @Override
    public long count(G g, String st, String et) throws BizException {
        try {
            return DaoHelper.count(g, st, et, this);
        } catch (Exception e) {
            throw BizException.getSqlException();
        }
    }

    @Override
    public List<G> queryList(G g, String st, String et, int start, int size) throws BizException {
        try {
            return DaoHelper.queryList(g, st, et, start, size, this);
        } catch (Exception e) {
            throw BizException.getSqlException();
        }
    }

    @Override
    public List<G> queryList(long startId, int size) throws BizException {
        try {
            return DaoHelper.queryListByStartId(null, startId, size, this);
        } catch (Exception e) {
            throw BizException.getSqlException();
        }
    }

    @Override
    public List<G> queryList(G g, String st, String et, int start, int size, String orderColumn, boolean orderByDesc)
            throws BizException {
        try {
            return DaoHelper.queryList(g, st, et, start, size, this, orderColumn, orderByDesc);
        } catch (Exception e) {
            throw BizException.getSqlException();
        }
    }

    @Override
    public <M> List<G> queryListByStartId(G g, M startId, int size) throws BizException {
        try {
            return DaoHelper.queryListByStartId(g, startId, size, this);
        } catch (Exception e) {
            throw BizException.getSqlException();
        }
    }

    @Override
    public <M> List<G> queryListByEndId(G g, M endId, int size) throws BizException {
        try {
            return DaoHelper.queryListByEndId(g, endId, size, this);
        } catch (Exception e) {
            throw BizException.getSqlException();
        }
    }

    @Override
    public void insertOrUpdate(G g) throws BizException {
        try {
            DaoHelper.insertOrUpdate(g, this);
        } catch (Exception e) {
            throw BizException.getSqlException();
        }
    }

    @Override
    public void insertOrUpdate(List<G> gg) throws BizException {
        try {
            DaoHelper.insertOrUpdate(gg, this);
        } catch (Exception e) {
            throw BizException.getSqlException();
        }
    }

    @Override
    public void insertOrUpdateQuickly(List<G> gg) throws BizException {
        try {
            DaoHelper.insertOrUpdateQuickly("", gg, this);
        } catch (Exception e) {
            throw BizException.getSqlException();
        }
    }

    @Override
    public void insertOrIgnore(G g) throws BizException {
        try {
            DaoHelper.insertOrIgnore(g, this);
        } catch (Exception e) {
            throw BizException.getSqlException();
        }
    }

    @Override
    public void insertOrIgnore(List<G> gg) throws BizException {
        try {
            DaoHelper.insertOrIgnore(gg, this);
        } catch (Exception e) {
            throw BizException.getSqlException();
        }
    }

    @Override
    public void insertOrIgnoreQuickly(List<G> gg) throws BizException {
        try {
            DaoHelper.insertOrIgnoreQuickly("", gg, this);
        } catch (Exception e) {
            throw BizException.getSqlException();
        }
    }

    @Override
    public void updateStatus(G g) throws BizException {
        try {
            DaoHelper.updateStatus(g, this);
        } catch (Exception e) {
            throw BizException.getSqlException();
        }
    }

    @Override
    public void updateStatus(List<G> gg) throws BizException {
        try {
            DaoHelper.updateStatus(gg, this);
        } catch (Exception e) {
            throw BizException.getSqlException();
        }
    }

    @Override
    public long count(List<String> columnList, List<EnumSqlConditionalSymbol> symbolList, List<Object> columnValueList)
            throws BizException {
        List<String> sqlExpressionList = buildSqlExpressionList(columnList, symbolList, columnValueList);
        try {
            return DaoHelper.count(sqlExpressionList, this);
        } catch (Exception e) {
            throw BizException.getSqlException();
        }
    }

    @Override
    public List<G> queryList(List<String> columnList, List<EnumSqlConditionalSymbol> symbolList,
            List<Object> columnValueList, String orderColumn, boolean orderByDesc, int start, int size)
            throws BizException {
        List<String> sqlExpressionList = buildSqlExpressionList(columnList, symbolList, columnValueList);
        try {
            return DaoHelper.queryListByExpressions(sqlExpressionList, orderColumn, orderByDesc, this, start, size);
        } catch (Exception e) {
            throw BizException.getSqlException();
        }
    }

    private List<String> buildSqlExpressionList(List<String> columnList, List<EnumSqlConditionalSymbol> symbolList,
            List<Object> columnValueList) throws BizException {
        if (CollectionUtils.isEmpty(columnList) || CollectionUtils.isEmpty(symbolList) || CollectionUtils
                .isEmpty(columnValueList) || columnList.size() != symbolList.size()
                || columnList.size() != columnValueList.size()) {
            throw BizException.getParameterException();
        }
        List<String> sqlExpressionList = new ArrayList<>();
        for (int i = 0; i < columnList.size(); i++) {
            sqlExpressionList
                    .add(symbolList.get(i).toSql(getGenericClass(), columnValueList.get(i), columnList.get(i)));
        }
        return sqlExpressionList;
    }


    @Override
    public G queryObject(List<String> columnList, List<EnumSqlConditionalSymbol> symbolList,
            List<Object> columnValueList) throws BizException {
        List<String> sqlExpressionList = buildSqlExpressionList(columnList, symbolList, columnValueList);
        try {
            return DaoHelper.queryObject(sqlExpressionList, this);
        } catch (Exception e) {
            throw BizException.getSqlException();
        }
    }

    @Override
    public <M> List<G> queryList(List<M> ids) throws BizException {
        try {
            return DaoHelper.queryList(ids, this);
        } catch (Exception e) {
            throw BizException.getSqlException();
        }
    }

    public String getOrderColumn() {
        String orderColumn = null;
        try {
            ORDER_KEY[] keys = getGenericClass().getAnnotationsByType(ORDER_KEY.class);

            if (NormalUtils.isEmpty(keys)) {
                orderColumn = DbFields.ID_COLUMN;
            } else {
                for (ORDER_KEY key : keys) {
                    if (key == null) {
                        continue;
                    }
                    if (!StringUtils.equalsIgnoreCase(key.value(), DbFields.ID_COLUMN) && StringUtils
                            .isNotEmpty(key.value())) {
                        orderColumn = key.value();
                        break;
                    }
                }
            }
        } catch (BizException e) {
            logger.error(e.getMessage(), e);
        }
        if (StringUtils.isEmpty(orderColumn)) {
            orderColumn = DbFields.ID_COLUMN;
        }
        return orderColumn;
    }

    public String getName() throws Exception {
        if (StringUtils.isEmpty(genericClassSimpleName)) {
            Class<G> clazz = getGenericClass();
            if (clazz == null) {
                throw BizException.getInternalException("Unknown generic class name for " + this.getClass().getName());
            }
            genericClassSimpleName = clazz.getSimpleName();
        }
        return genericClassSimpleName;
    }

    protected Class<G> getGenericClass() throws BizException {
        Class<G> clazz = getGenericClass(this.getClass());
        if (clazz == null) {
            throw BizException.getInternalException("Unknown generic class name for " + this.getClass().getName());
        }
        return clazz;
    }

    private Class<G> getGenericClass(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        Type type = clazz.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            return ((Class<G>) ((ParameterizedType) type).getActualTypeArguments()[0]);
        } else {
            Class<?> superClass = clazz.getSuperclass();
            return getGenericClass(superClass);
        }
    }

    public String getTableName() {
        return "";
    }
}
