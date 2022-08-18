//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.db.service;

import com.mokous.base.exception.BizException;
import com.mokous.db.dao.CommonDao;
import com.mokous.db.enums.EnumSqlConditionalSymbol;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luofei
 * Generate 2020/1/13
 */
public interface NormalCommonDataService<G> {
    Logger logger = LoggerFactory.getLogger(NormalCommonDataService.class);

    int MAX_QUERY_LIST_SIZE = 5000;

    CommonDao<G> getCommonDao(G g);

    default List<G> listDirectFromDb(G g, String st, String et, int start, int size) throws BizException {
        if (StringUtils.isAllEmpty(st, et)) {
            return listDirectFromDb(g, start, size);
        }
        return getCommonDao(g).queryList(g, st, et, start, size);
    }

    default long countDirectFromDb(G g) throws BizException {
        return getCommonDao(g).count(g);
    }

    default long countDirectFromDb(G g, String st, String et) throws BizException {
        if (StringUtils.isAllEmpty(st, et)) {
            return countDirectFromDb(g);
        }
        return getCommonDao(g).count(g, st, et);
    }

    default List<G> listDirectFromDb(G g, String st, String et, int start, int size, String orderColumn,
            boolean orderByDesc) throws BizException {
        if (StringUtils.isAllEmpty(st, et)) {
            return listDirectFromDb(g, start, size, orderColumn, orderByDesc);
        }
        return getCommonDao(g).queryList(g, st, et, start, size, orderColumn, orderByDesc);
    }

    default List<G> listDirectFromDb(G g, int start, int size, String orderColumn,
            boolean orderByDesc) throws BizException {
        return getCommonDao(g).queryList(g, start, size, orderColumn, orderByDesc);
    }

    default List<G> listDirectFromDb(G g, int start, int size) throws BizException {
        return getCommonDao(g).queryList(g, start, size);
    }

    default List<G> listDirectFromDb(G g) throws BizException {
        List<G> datas = new ArrayList<>();
        int start = 0;
        List<G> innerDatas;
        do {
            innerDatas = listDirectFromDb(g, start, MAX_QUERY_LIST_SIZE);
            if (CollectionUtils.isEmpty(innerDatas)) {
                break;
            }
            datas.addAll(innerDatas);
            if (innerDatas.size() < MAX_QUERY_LIST_SIZE) {
                break;
            }
            innerDatas.clear();
            start += MAX_QUERY_LIST_SIZE;
        } while (true);
        return datas;
    }

    default List<G> listDirectFromDb(G g, String st, int start, int size) throws BizException {
        return getCommonDao(g).queryList(g, st, null, start, size);
    }

    default G getObjectDirectFromDb(List<String> columnList, List<EnumSqlConditionalSymbol> symbolList,
            List<Object> columnValueList) throws BizException {
        return getCommonDao(null).queryObject(columnList, symbolList, columnValueList);
    }

    default G getObjectDirectFromDb(List<String> columnList, List<Object> columnValueList) throws BizException {
        return getCommonDao(null).queryObject(columnList, columnValueList);
    }

    default List<G> listDirectFromDb(List<String> columnList, List<EnumSqlConditionalSymbol> symbolList,
            List<Object> columnValueList, String orderColumn, boolean orderByDesc, int start, int size)
            throws BizException {
        return getCommonDao(null)
                .queryList(columnList, symbolList, columnValueList, orderColumn, orderByDesc, start, size);
    }

    default List<G> listDirectFromDb(List<String> columnList, List<Object> columnValueList, String orderColumn,
            boolean orderByDesc, int start, int size)
            throws BizException {
        return getCommonDao(null)
                .queryList(columnList, columnValueList, orderColumn, orderByDesc, start, size);
    }

    default List<G> listDirectFromDb(List<String> columnList, List<EnumSqlConditionalSymbol> symbolList,
            List<Object> columnValueList, int start, int size)
            throws BizException {
        return getCommonDao(null)
                .queryList(columnList, symbolList, columnValueList, start, size);
    }

    default long count(List<String> columnList, List<Object> columnValueList) throws BizException {
        return getCommonDao(null).count(columnList, columnValueList);
    }


    default long count(List<String> columnList, List<EnumSqlConditionalSymbol> symbolList, List<Object> columnValueList)
            throws BizException {
        return getCommonDao(null).count(columnList, symbolList, columnValueList);
    }

    default void modifyStatus(G g) throws BizException {
        if (g == null) {
            return;
        }
        List<G> gg = new ArrayList<>();
        gg.add(g);
        modifyStatus(gg);
    }

    default void addData(G g) throws BizException {
        if (g == null) {
            return;
        }
        beforeToDb(g);
        getCommonDao(g).insertOrUpdate(g);
        afterAddData(g);
    }

    default void afterAddData(G g) throws BizException {
        List<G> gg = new ArrayList<>();
        gg.add(g);
        afterAddData(gg);
    }

    default void afterAddData(List<G> gg) throws BizException {
    }

    default void beforeToDb(G g) throws BizException {

    }

    default void batchAdd(List<G> gg) throws BizException {
        if (CollectionUtils.isEmpty(gg)) {
            return;
        }
        beforeToDb(gg);
        getCommonDao(gg.get(0)).insertOrUpdate(gg);
        afterAddData(gg);
    }

    default void beforeToDb(List<G> gg) throws BizException {
        for (G g : gg) {
            beforeToDb(g);
        }
    }

    default void batchAddOrIgnore(List<G> gg) throws BizException {
        if (CollectionUtils.isEmpty(gg)) {
            return;
        }
        beforeToDb(gg);
        getCommonDao(gg.get(0)).insertOrIgnore(gg);
        afterAddData(gg);
    }

    default void batchAddOrUpdate(List<G> gg) throws BizException {

        if (CollectionUtils.isEmpty(gg)) {
            return;
        }
        beforeToDb(gg);
        getCommonDao(gg.get(0)).insertOrUpdate(gg);
        afterAddData(gg);
    }

    default void batchAddOrUpdateQuickly(List<G> gg) throws BizException {
        if (CollectionUtils.isEmpty(gg)) {
            return;
        }
        beforeToDb(gg);
        getCommonDao(gg.get(0)).insertOrUpdateQuickly(gg);
        afterAddData(gg);
    }

    default void batchAddQuickly(List<G> gg) throws BizException {
        if (CollectionUtils.isEmpty(gg)) {
            return;
        }
        beforeToDb(gg);
        getCommonDao(gg.get(0)).insertOrUpdateQuickly(gg);
        afterAddData(gg);
    }

    default void batchAddOrIgnoreQuickly(List<G> gg) throws BizException {
        if (CollectionUtils.isEmpty(gg)) {
            return;
        }
        beforeToDb(gg);
        getCommonDao(gg.get(0)).insertOrIgnoreQuickly(gg);
        afterAddData(gg);
    }

    default boolean addOrIgnoreData(G g) throws BizException {
        if (g == null) {
            return false;
        }
        beforeToDb(g);
        getCommonDao(g).insertOrIgnore(g);
        afterAddData(g);
        return true;
    }

    default void modifyStatus(List<G> gg) throws BizException {
        if (CollectionUtils.isEmpty(gg)) {
            return;
        }
        beforeModifyData(gg);
        getCommonDao(gg.get(0)).updateStatus(gg);
        afterModifyStatus(gg);
    }

    default void afterModifyStatus(List<G> gg) {
    }

    default void beforeModifyData(List<G> gg) {
    }

}
