//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.db.dao;

import com.mokous.base.domain.model.DbFields;
import com.mokous.base.exception.BizException;
import com.mokous.db.enums.EnumSqlConditionalSymbol;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author luofei
 * Generate 2020/1/12
 */
public interface CommonDao<G> extends LoadDao<G> {
    long count(G g) throws BizException;

    long count(G g, String st, String et) throws BizException;

    List<G> queryList(G g, int start, int size) throws BizException;

    List<G> queryList(G g, int start, int size, String orderColumn, boolean orderByDesc) throws BizException;

    List<G> queryList(G g, String st, String et, int start, int size) throws BizException;

    List<G> queryList(G g, String st, String et, int start, int size, String orderColumn, boolean orderByDesc)
            throws BizException;

    <M> List<G> queryList(List<M> ids) throws BizException;

    <M> G queryObject(M id) throws BizException;

    <M> List<G> queryListByStartId(G g, M startId, int size) throws BizException;

    <M> List<G> queryListByEndId(G g, M endId, int size) throws BizException;

    void insertOrUpdate(G g) throws BizException;

    void insertOrUpdate(List<G> gg) throws BizException;

    void insertOrUpdateQuickly(List<G> gg) throws BizException;

    void insertOrIgnore(G g) throws BizException;

    void insertOrIgnore(List<G> gg) throws BizException;

    void insertOrIgnoreQuickly(List<G> gg) throws BizException;

    void updateStatus(G g) throws BizException;

    void updateStatus(List<G> gg) throws BizException;

    SqlSessionFactory getSqlSessionFactory();

    G queryObject(List<String> columnList, List<EnumSqlConditionalSymbol> symbolList, List<Object> columnValueList)
            throws BizException;

    default G queryObject(List<String> columnList, List<Object> columnValueList)
            throws BizException {
        if (CollectionUtils.isEmpty(columnList) || CollectionUtils.isEmpty(columnValueList)
                || columnList.size() != columnValueList.size()) {
            throw BizException.getParameterException();
        }
        List<EnumSqlConditionalSymbol> symbolList = new ArrayList<>();
        for (String s : columnList) {
            symbolList.add(EnumSqlConditionalSymbol.EQ);
        }
        return queryObject(columnList, symbolList, columnValueList);
    }

    List<G> queryList(List<String> columnList, List<EnumSqlConditionalSymbol> symbolList, List<Object> columnValueList,
            String orderColumn, boolean orderByDesc, int start, int size)
            throws BizException;


    default List<G> queryList(List<String> columnList,
            List<Object> columnValueList, String orderColumn, boolean orderByDesc, int start, int size)
            throws BizException {
        boolean emptyAnyCollections = CollectionUtils.isEmpty(columnList) || CollectionUtils.isEmpty(columnValueList);

        if (emptyAnyCollections || columnList.size() != columnValueList.size()) {
            throw BizException.getParameterException();
        }
        List<EnumSqlConditionalSymbol> symbolList = new ArrayList<>();
        for (String s : columnList) {
            symbolList.add(EnumSqlConditionalSymbol.EQ);
        }
        return queryList(columnList, symbolList, columnValueList, orderColumn, orderByDesc, start, size);
    }

    default List<G> queryList(List<String> columnList, List<EnumSqlConditionalSymbol> symbolList,
            List<Object> columnValueList, int start, int size)
            throws BizException {
        return queryList(columnList, symbolList, columnValueList, DbFields.ID_COLUMN, true, start, size);
    }

    default List<G> queryList(List<String> columnList,
            List<Object> columnValueList, int start, int size)
            throws BizException {
        return queryList(columnList, columnValueList, DbFields.ID_COLUMN, true, start, size);
    }

    long count(List<String> columnList, List<EnumSqlConditionalSymbol> symbolList, List<Object> columnValueList)
            throws BizException;

    default long count(List<String> columnList, List<Object> columnValueList)
            throws BizException {
        boolean emptyAnyCollections = CollectionUtils.isEmpty(columnList) || CollectionUtils.isEmpty(columnValueList);
        if (emptyAnyCollections
                || columnList.size() != columnValueList.size()) {
            throw BizException.getParameterException();
        }
        List<EnumSqlConditionalSymbol> symbolList = new ArrayList<>();
        for (int i = 0; i < columnList.size(); i++) {
            symbolList.add(EnumSqlConditionalSymbol.EQ);
        }
        return count(columnList, symbolList, columnValueList);

    }
}
