// Copyright 2015 ios.appchina.com Inc. All Rights Reserved.

package com.mokous.core.dao;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.mokous.core.utils.SQLUtils;
import com.mokous.web.utils.CollectionUtils;


/**
 * @author luofei@appchina.com (Your Name Here)
 *
 */
public final class DaoHelper<G> {
    public static <G> List<G> queryList(G g, int start, int size, AbsCommonDao<G> dao) throws SQLException {
        Map<String, Object> para = dao.buildListPara(g);
        return SQLUtils.queryList(dao.getSqlMapClient(), getQueryListSqlId(dao), para, start, size);
    }

    public static <G> List<G> queryListByStartId(G g, int startId, int size, AbsCommonDao<G> dao) throws SQLException {
        Map<String, Object> para = dao.buildListPara(g);
        return SQLUtils.queryListByStartId(dao.getSqlMapClient(), getQueryListByStartIdFilterSqlId(dao), para, startId, size);
    }

    public static <G> List<G> queryListByEndId(G g, int endId, int size, AbsCommonDao<G> dao) throws SQLException {
        Map<String, Object> para = dao.buildListPara(g);
        return SQLUtils.queryListByEndId(dao.getSqlMapClient(), getQueryListByEndIdFilterSqlId(dao), para, endId, size);
    }

    public static <G> List<G> queryList(G g, String st, String et, int start, int size, AbsCommonDao<G> dao)
            throws SQLException {
        Map<String, Object> para = dao.buildListPara(g);
        if (!StringUtils.isEmpty(st)) {
            para.put("st", st);
        }
        if (!StringUtils.isEmpty(et)) {
            para.put("et", et);
        }
        return SQLUtils.queryList(dao.getSqlMapClient(), getQueryListSqlId(dao), para, start, size);
    }

    public static <G> List<G> queryList(int startId, int size, AbsCommonDao<G> dao) throws SQLException {
        return SQLUtils.queryList(dao.getSqlMapClient(), getQueryListByStartIdSqlId(dao),
                SQLUtils.buildStartIdSizeMap(startId, size));
    }

    public static <G> G queryObject(int id, AbsCommonDao<?> dao) throws SQLException {
        return SQLUtils.queryObject(dao.getSqlMapClient(), getQueryObjectSqlId(dao), id);
    }

    public static <G> List<G> queryList(List<Integer> ids, AbsCommonDao<?> dao) throws SQLException {
        if (CollectionUtils.emptyOrNull(ids)) {
            return Collections.emptyList();
        }
        Map<String, Object> para = buildIdsPara(ids);
        return SQLUtils.queryList(dao.getSqlMapClient(), getQueryListByIdsSqlId(dao), para);
    }

    public static Map<String, Object> buildIdsPara(List<Integer> ids) {
        Map<String, Object> para = new HashMap<String, Object>();
        para.put("ids", ids);
        return para;
    }

    public static <G> void insertOrUpdate(G g, AbsCommonDao<?> dao) throws SQLException {
        SQLUtils.insertOrUpdate(dao.getSqlMapClient(), getInsertOrUpdateSqlId(dao), g);
    }

    public static <G> void insertOrUpdate(List<G> gg, AbsCommonDao<?> dao) throws SQLException {
        SQLUtils.batchInsertOrUpdate(dao.getSqlMapClient(), getInsertOrUpdateSqlId(dao), gg);
    }

    public static <G> boolean insertOrIgnore(G g, AbsCommonDao<?> dao) throws SQLException {
        return SQLUtils.insertOrIgnore(dao.getSqlMapClient(), getInsertOrIgnoreSqlId(dao), g);
    }

    public static <G> void updateStatus(G g, AbsCommonDao<?> dao) throws SQLException {
        SQLUtils.update(dao.getSqlMapClient(), getUpdateStatusSqlId(dao), g);
    }

    public static <G> void updateStatus(List<G> gg, AbsCommonDao<?> dao) throws SQLException {
        SQLUtils.batchInsertOrUpdate(dao.getSqlMapClient(), getUpdateStatusSqlId(dao), gg);
    }

    public static <G> void update(G g, AbsCommonDao<?> dao) throws SQLException {
        SQLUtils.update(dao.getSqlMapClient(), getUpdateSqlId(dao), g);
    }

    public static <G> long count(G g, AbsCommonDao<G> dao) throws SQLException {
        Map<String, Object> para = dao.buildCountPara(g);
        return SQLUtils.count(dao.getSqlMapClient(), getCountSqlId(dao), para);
    }

    public static <G> long count(G g, String st, String et, AbsCommonDao<G> dao) throws SQLException {
        Map<String, Object> para = dao.buildCountPara(g);
        if (!StringUtils.isEmpty(st)) {
            para.put("st", st);
        }
        if (!StringUtils.isEmpty(et)) {
            para.put("et", et);
        }
        return SQLUtils.count(dao.getSqlMapClient(), getCountSqlId(dao), para);
    }

    protected static String getQueryListByStartIdSqlId(AbsCommonDao<?> dao) {
        return "query" + dao.getName() + "ListByStartId";
    }

    protected static String getQueryListSqlId(AbsCommonDao<?> dao) {
        return "query" + dao.getName() + "ListByFilter";
    }

    protected static String getQueryListByStartIdFilterSqlId(AbsCommonDao<?> dao) {
        return "query" + dao.getName() + "ListByStartIdFilter";
    }

    protected static String getQueryListByEndIdFilterSqlId(AbsCommonDao<?> dao) {
        return "query" + dao.getName() + "ListByEndIdFilter";
    }


    protected static String getQueryObjectSqlId(AbsCommonDao<?> dao) {
        return "query" + dao.getName() + "ById";
    }

    protected static String getQueryListByIdsSqlId(AbsCommonDao<?> dao) {
        return "query" + dao.getName() + "ByIds";
    }

    protected static String getInsertOrUpdateSqlId(AbsCommonDao<?> dao) {
        return "insertOrUpdate" + dao.getName();
    }

    protected static String getInsertOrIgnoreSqlId(AbsCommonDao<?> dao) {
        return "insertOrIgnore" + dao.getName();
    }

    protected static String getUpdateStatusSqlId(AbsCommonDao<?> dao) {
        return "update" + dao.getName() + "Status";
    }

    protected static String getUpdateSqlId(AbsCommonDao<?> dao) {
        return "update" + dao.getName();
    }

    protected static <G> String getCountSqlId(AbsCommonDao<G> dao) {
        return "count" + dao.getName() + "ByFilter";
    }
}
