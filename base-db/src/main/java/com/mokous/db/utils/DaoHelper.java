//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.db.utils;

import com.mokous.base.domain.model.DbFields;
import com.mokous.base.exception.BizException;
import com.mokous.base.utils.NormalUtils;
import com.mokous.db.dao.AbstractCommonDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author luofei
 * Generate 2020/1/12
 */
public class DaoHelper {
    private static final Logger logger = LoggerFactory.getLogger(DaoHelper.class);
    public static final int PARALLEL_QUERY_SIZE = 5000;
    private static final int THREADS = Runtime.getRuntime().availableProcessors();

    public static <G> List<G> queryList(G g, int start, int size, AbstractCommonDao<G> dao) throws Exception {
        return queryList("", g, start, size, dao);
    }

    public static <G> List<G> queryList(String polymorphismTable, G g, int start, int size, AbstractCommonDao<G> dao)
            throws Exception {
        return queryList(g, start, size, dao, true);
    }

    public static <G> List<G> queryList(G g, int start, int size, AbstractCommonDao<G> dao, boolean orderByDesc)
            throws Exception {
        return queryList("", g, start, size, dao, orderByDesc);
    }

    public static <G> List<G> queryList(String polymorphismTable, G g, int start, int size, AbstractCommonDao<G> dao,
            boolean orderByDesc) throws Exception {
        return queryList(polymorphismTable, g, start, size, dao, dao.getOrderColumn(), orderByDesc);
    }

    public static <G> List<G> queryList(G g, int start, int size, AbstractCommonDao<G> dao, String orderColumn,
            boolean orderByDesc) throws Exception {
        return queryList("", g, start, size, dao, orderColumn, orderByDesc);
    }

    public static <G> List<G> queryList(String polymorphismTable, G g, int start, int size, AbstractCommonDao<G> dao,
            String orderColumn,
            boolean orderByDesc) throws Exception {
        Map<String, Object> para = SQLParameterUtils.buildListPara(g);
        SQLParameterUtils.initTableNameToMap(para, polymorphismTable);
        return queryList(para, start, size, dao, orderColumn, orderByDesc);
    }


    public static <G> List<G> queryList(Map<String, Object> para, int start, int size, AbstractCommonDao<G> dao,
            String orderColumn,
            boolean orderByDesc) throws Exception {
        para.putIfAbsent(DbFields.DEL_FLAG_COLUMN, DbFields.DEL_FLAG_OK);
        SQLParameterUtils.buildOrder(para, orderColumn, orderByDesc);
        return SQLUtils.queryList(dao.getSqlSessionFactory(), getQueryListSqlId(dao), para, start, size);
    }


    public static <G> List<G> queryList(G g, String st, String et, int start, int size, AbstractCommonDao<G> dao)
            throws Exception {
        return queryList(g, st, et, start, size, dao, dao.getOrderColumn(), true);
    }

    public static <G> List<G> queryList(G g, String st, String et, int start, int size, AbstractCommonDao<G> dao,
            String orderColumn,
            boolean orderByDesc) throws Exception {
        return queryList("", g, st, et, start, size, dao, orderColumn, orderByDesc);
    }

    public static <G> List<G> queryList(String polymorphismTable, G g, String st, String et, int start, int size,
            AbstractCommonDao<G> dao,
            String orderColumn,
            boolean orderByDesc) throws Exception {
        Map<String, Object> para = SQLParameterUtils.buildListPara(g);
        SQLParameterUtils.buildStEtMap(para, st, et);
        SQLParameterUtils.initTableNameToMap(para, polymorphismTable);
        return queryList(para, start, size, dao, orderColumn, orderByDesc);
    }

    public static <M, G> List<G> queryListByStartId(G g, M startId, int size, AbstractCommonDao<G> dao)
            throws Exception {
        return queryListByStartId("", g, startId, size, dao);
    }

    private static <G, M> List<G> queryListByStartId(String polymorphismTable, G g, M startId, int size,
            AbstractCommonDao<G> dao)
            throws Exception {
        Map<String, Object> para = SQLParameterUtils.buildListPara(g);
        SQLParameterUtils.initTableNameToMap(para, polymorphismTable);
        return SQLUtils
                .queryListByStartId(dao.getSqlSessionFactory(), getQueryListByStartIdFilterSqlId(dao), para, startId,
                        size);
    }

    public static <M, G> List<G> queryListByEndId(G g, M endId, int size, AbstractCommonDao<G> dao)
            throws Exception {
        return queryListByEndId("", g, endId, size, dao);
    }

    public static <M, G> List<G> queryListByEndId(String polymorphismTable, G g, M endId, int size,
            AbstractCommonDao<G> dao) throws Exception {
        Map<String, Object> para = SQLParameterUtils.buildListPara(g);
        SQLParameterUtils.initTableNameToMap(para, polymorphismTable);
        return SQLUtils
                .queryListByEndId(dao.getSqlSessionFactory(), getQueryListByEndIdFilterSqlId(dao), para, endId, size);
    }

    public static <M, G> List<G> queryList(M startId, int size, AbstractCommonDao<G> dao) throws Exception {
        return queryList("", startId, size, dao);
    }

    public static <M, G> List<G> queryList(String polymorphismTable, M startId, int size, AbstractCommonDao<G> dao)
            throws Exception {
        Map<String, Object> para = SQLParameterUtils.buildStartIdSizeMap(startId, size);
        SQLParameterUtils.initTableNameToMap(para, polymorphismTable);
        return SQLUtils.queryList(dao.getSqlSessionFactory(), getQueryListByStartIdSqlId(dao), para);
    }

    public static <M, G> G queryObject(M id, AbstractCommonDao<?> dao) throws Exception {
        return queryObject("", id, dao);
    }

    public static <M, G> G queryObject(String polymorphismTable, M id, AbstractCommonDao<?> dao) throws Exception {
        return SQLUtils.queryObject(dao.getSqlSessionFactory(), getQueryObjectSqlId(dao),
                SQLParameterUtils.buildPolymorphismTableMap(id, polymorphismTable));
    }


    public static <G> G queryObject(List<String> expressions, AbstractCommonDao<?> dao) throws Exception {
        return queryObject("", expressions, dao);
    }

    public static <G> G queryObject(String polymorphismTable, List<String> expressions, AbstractCommonDao<?> dao)
            throws Exception {
        Map<String, Object> para = SQLParameterUtils.buildExpressionPara(expressions);
        SQLParameterUtils.initTableNameToMap(para, polymorphismTable);
        return SQLUtils.queryObject(dao.getSqlSessionFactory(), getQueryObjectSqlIdByExpressions(dao), para);
    }

    public static <M, G> List<G> queryList(List<M> ids, AbstractCommonDao<G> dao)
            throws Exception {
        return queryList("", ids, dao);
    }

    public static <M, G> List<G> queryList(String polymorphismTable, List<M> ids, AbstractCommonDao<G> dao)
            throws Exception {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        if (ids.size() > PARALLEL_QUERY_SIZE) {
            ids = new ArrayList<>(new HashSet<>(ids));
            List<List<M>> subIdsList = NormalUtils.splitList(ids, PARALLEL_QUERY_SIZE);
            return parallelQueryList(polymorphismTable, subIdsList, dao);
        }
        Map<String, Object> para = SQLParameterUtils.buildIdsPara(ids);
        SQLParameterUtils.initTableNameToMap(para, polymorphismTable);
        return SQLUtils.queryList(dao.getSqlSessionFactory(), getQueryListByIdsSqlId(dao), para);
    }

    public static <M, G> List<G> parallelQueryList(String polymorphismTable, List<List<M>> subIdsList,
            AbstractCommonDao<?> dao) throws Exception {
        List<Future<List<G>>> futures = new ArrayList<>();
        ExecutorService es = Executors.newFixedThreadPool(THREADS);
        for (List<M> ids : subIdsList) {
            Future<List<G>> future = es.submit(() -> (List<G>) queryList(polymorphismTable, ids, dao));
            futures.addAll(futures);
        }
        List<G> result = new ArrayList<>();
        try {
            for (Future<List<G>> future : futures) {
                List<G> innerResult;
                try {
                    innerResult = future.get();
                    if (!CollectionUtils.isEmpty(innerResult)) {
                        result.addAll(innerResult);
                    }
                } catch (Exception e) {
                    logger.error("Get data list failed. Error msg:" + e.getMessage(), e);
                    throw BizException.getSqlException();
                }
            }
        } finally {
            es.shutdown();
        }
        return result;
    }

    public static <G> List<G> queryListByExpressions(List<String> expressions, String orderColumn, boolean orderByDesc,
            AbstractCommonDao<?> dao, int start, int size) throws Exception {
        return queryListByExpressions("", expressions, orderColumn, orderByDesc, dao, start, size);
    }

    private static <G> List<G> queryListByExpressions(String polymorphismTable, List<String> expressions,
            String orderColumn, boolean orderByDesc, AbstractCommonDao<?> dao, int start, int size) throws Exception {
        if (CollectionUtils.isEmpty(expressions)) {
            return Collections.emptyList();
        }
        Map<String, Object> para = SQLParameterUtils.buildExpressionPara(expressions);
        SQLParameterUtils.buildOrder(para, orderColumn, orderByDesc);
        SQLParameterUtils.buildStartSizeMap(para, start, size);
        SQLParameterUtils.initTableNameToMap(para, polymorphismTable);
        return SQLUtils.queryList(dao.getSqlSessionFactory(), getQueryListByExpressionsSqlId(dao), para);
    }

    public static <G> void insertOrUpdate(G g, AbstractCommonDao<?> dao) throws Exception {
        insertOrUpdate("", g, dao);
    }

    public static <G> void insertOrUpdate(String polymorphismTable, G g, AbstractCommonDao<?> dao) throws Exception {
        SQLUtils.insertOrUpdate(dao.getSqlSessionFactory(), getInsertOrUpdateSqlId(dao), g);
    }

    public static <G> void insertOrUpdate(List<G> gg, AbstractCommonDao<?> dao) throws Exception {
        insertOrUpdate("", gg, dao);
    }

    public static <G> void insertOrUpdate(String polymorphismTable, List<G> gg, AbstractCommonDao<?> dao)
            throws Exception {
        SQLUtils.batchInsertOrUpdate(dao.getSqlSessionFactory(), getInsertOrUpdateSqlId(dao), polymorphismTable, gg);
    }

    public static <G> void insertOrUpdateQuickly(String polymorphismTable, List<G> gg, AbstractCommonDao<?> dao)
            throws Exception {
        SQLUtils.batchInsertOrUpdateQuickly(dao.getSqlSessionFactory(), getInsertOrUpdateBatchSqlId(dao), polymorphismTable,
                gg);
    }

    public static <G> boolean insertOrIgnore(G g, AbstractCommonDao<?> dao) throws Exception {
        return insertOrIgnore("", g, dao);
    }

    public static <G> boolean insertOrIgnore(String polymorphismTable, G g, AbstractCommonDao<?> dao) throws Exception {
        return SQLUtils.insertOrIgnore(dao.getSqlSessionFactory(), getInsertOrIgnoreSqlId(dao), polymorphismTable, g);
    }

    public static <G> void insertOrIgnore(List<G> gg, AbstractCommonDao<?> dao) throws Exception {
        insertOrIgnore("", gg, dao);
    }

    public static <G> void insertOrIgnore(String polymorphismTable, List<G> gg, AbstractCommonDao<?> dao)
            throws Exception {
        SQLUtils.insertOrIgnore(dao.getSqlSessionFactory(), getInsertOrIgnoreSqlId(dao), polymorphismTable, gg);
    }

    public static <G> void insertOrIgnoreQuickly(String polymorphismTable, List<G> gg, AbstractCommonDao<?> dao)
            throws Exception {
        SQLUtils.batchInsertOrUpdateQuickly(dao.getSqlSessionFactory(), getInsertOrIgnoreBatchSqlId(dao), polymorphismTable,
                gg);
    }

    public static <G> void updateStatus(G g, AbstractCommonDao<?> dao) throws Exception {
        updateStatus("", g, dao);
    }

    public static <G> void updateStatus(List<G> gg, AbstractCommonDao<?> dao) throws Exception {
        updateStatus("", gg, dao);
    }

    public static <G> void updateStatus(String polymorphismTable, List<G> gg, AbstractCommonDao<?> dao)
            throws Exception {
        SQLUtils.batchInsertOrUpdate(dao.getSqlSessionFactory(), getUpdateStatusSqlId(dao), polymorphismTable, gg);
    }

    public static <G> void updateStatus(String polymorphismTable, G g, AbstractCommonDao<?> dao) throws Exception {
        SQLUtils.update(dao.getSqlSessionFactory(), getUpdateSqlId(dao), polymorphismTable, g);
    }

    public static <G> long count(G g, AbstractCommonDao<G> dao) throws Exception {
        return count(g, "", "", dao);
    }

    public static <G> long count(String polymorphismTable, G g, AbstractCommonDao<G> dao) throws Exception {
        return count(polymorphismTable, g, "", "", dao);
    }


    public static <G> long count(G g, String st, String et, AbstractCommonDao<G> dao) throws Exception {
        return count("", g, st, et, dao);
    }

    public static <G> long count(String polymorphismTable, G g, String st, String et, AbstractCommonDao<G> dao)
            throws Exception {
        Map<String, Object> para = SQLParameterUtils.buildCountPara(g);
        SQLParameterUtils.initTableNameToMap(para, polymorphismTable);
        SQLParameterUtils.buildStEtMap(para, st, et);
        return SQLUtils.count(dao.getSqlSessionFactory(), getCountSqlId(dao), para);
    }

    public static <G> long count(List<String> expressions, AbstractCommonDao<G> dao) throws Exception {
        return count("", expressions, dao);
    }

    public static <G> long count(String polymorphismTable, List<String> expressions, AbstractCommonDao<G> dao)
            throws Exception {
        Map<String, Object> para = SQLParameterUtils.buildExpressionPara(expressions);
        SQLParameterUtils.initTableNameToMap(para, polymorphismTable);
        return SQLUtils.count(dao.getSqlSessionFactory(), getCountByExpressionSqlId(dao), para);
    }

    protected static String getQueryListByStartIdSqlId(AbstractCommonDao<?> dao) throws Exception {
        return "query" + dao.getName() + "ListByStartId";
    }

    protected static String getQueryListSqlId(AbstractCommonDao<?> dao) throws Exception {
        return "query" + dao.getName() + "ListByFilter";
    }

    protected static String getQueryListByStartIdFilterSqlId(AbstractCommonDao<?> dao) throws Exception {
        return "query" + dao.getName() + "ListByStartIdFilter";
    }

    protected static String getQueryListByEndIdFilterSqlId(AbstractCommonDao<?> dao) throws Exception {
        return "query" + dao.getName() + "ListByEndIdFilter";
    }


    protected static String getQueryObjectSqlId(AbstractCommonDao<?> dao) throws Exception {
        return "query" + dao.getName() + "ById";
    }


    protected static String getQueryObjectSqlIdByExpressions(AbstractCommonDao<?> dao) throws Exception {
        return "query" + dao.getName() + "ListByExpressions";
    }

    protected static String getQueryListByIdsSqlId(AbstractCommonDao<?> dao) throws Exception {
        return "query" + dao.getName() + "ListByIds";
    }

    private static String getQueryListByExpressionsSqlId(AbstractCommonDao<?> dao) throws Exception {
        return "query" + dao.getName() + "ListByExpressions";
    }

    protected static String getInsertOrUpdateSqlId(AbstractCommonDao<?> dao) throws Exception {
        return "insertOrUpdate" + dao.getName();
    }

    protected static String getInsertOrIgnoreSqlId(AbstractCommonDao<?> dao) throws Exception {
        return "insertOrIgnore" + dao.getName();
    }

    protected static String getInsertOrUpdateBatchSqlId(AbstractCommonDao<?> dao) throws Exception {
        return "insertOrUpdate" + dao.getName() + "Batch";
    }

    protected static String getInsertOrIgnoreBatchSqlId(AbstractCommonDao<?> dao) throws Exception {
        return "insertOrIgnore" + dao.getName() + "Batch";
    }

    protected static String getUpdateStatusSqlId(AbstractCommonDao<?> dao) throws Exception {
        return "update" + dao.getName() + "Status";
    }

    protected static String getUpdateSqlId(AbstractCommonDao<?> dao) throws Exception {
        return "update" + dao.getName();
    }

    protected static <G> String getCountSqlId(AbstractCommonDao<G> dao) throws Exception {
        return "count" + dao.getName() + "ByFilter";
    }

    protected static <G> String getCountByExpressionSqlId(AbstractCommonDao<G> dao) throws Exception {
        return "count" + dao.getName() + "ByExpressions";
    }
}
