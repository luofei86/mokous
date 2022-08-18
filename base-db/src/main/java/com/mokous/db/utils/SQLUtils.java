//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.db.utils;

import com.mokous.base.utils.NormalUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.sql.SQLException;
import java.util.*;

/**
 * @author luofei
 * Generate 2020/1/12
 */
public class SQLUtils {
    private static final Logger logger = LoggerFactory.getLogger(SQLUtils.class);
    public static final int INSERT_ONDUPLICATEKEY_INGORE_INSERT = 0;
    public static final int INSERT_ONDUPLICATEKEY_UPDATE_INSERT = 1;
    public static final int INSERT_ONDUPLICATEKEY_UPDATE_UPDATE = 2;
    private static final int MAX_TRY_TIMES = 5;
    private static final int MAX_QUERY_SIZE = 10000;
    private static final int MAX_BATCH_SIZE = 1000;

    private static <G> void subBatchInsertOrUpdate(SqlSessionFactory sqlSessionFactory, String sqlId, List<G> gg)
            throws Exception {
        if (CollectionUtils.isEmpty(gg)) {
            return;
        }
        int fromIndex = 0;
        int toIndex = MAX_BATCH_SIZE;
        do {
            toIndex = Math.min(gg.size(), toIndex);
            List<G> subList = gg.subList(fromIndex, toIndex);
            batchInsertOrUpdate(sqlSessionFactory, sqlId, subList);
            fromIndex += subList.size();
            if (fromIndex >= gg.size()) {
                break;
            }
            toIndex = (fromIndex + subList.size());
        } while (true);
    }

    private static <G> void subBatchInsertOrUpdateQuickly(SqlSessionFactory sqlSessionFactory, String sqlId, List<G> gg)
            throws Exception {
        if (CollectionUtils.isEmpty(gg)) {
            return;
        }
        int fromIndex = 0;
        int toIndex = MAX_BATCH_SIZE;
        do {
            if (toIndex > gg.size()) {
                toIndex = gg.size();
            }
            List<G> subList = gg.subList(fromIndex, toIndex);
            batchInsertOrUpdateQuickly(sqlSessionFactory, sqlId, subList);
            fromIndex += subList.size();
            if (fromIndex >= gg.size()) {
                break;
            }
            toIndex = (fromIndex + subList.size());
        } while (true);
    }

    private static <G> void batchInsertOrUpdateQuickly(SqlSessionFactory sqlSessionFactory, String sqlId,
            List<G> dataList)
            throws Exception {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        if (dataList.size() > MAX_BATCH_SIZE) {
            subBatchInsertOrUpdateQuickly(sqlSessionFactory, sqlId, dataList);
            return;
        }
        SqlSession sqlSession = sqlSessionFactory.openSession(true);
        try {
            sqlSession.insert(sqlId, dataList);
        } catch (Exception e) {
            logger.error("从数据库中插入数据失败.SqlId:" + sqlId + ", Data:" + dataList + ", Error msg:" + e.getMessage(), e);
            throw e;
        } finally {
            sqlSession.close();
        }
    }

    public static <G> void batchInsertOrUpdate(SqlSessionFactory sqlSessionFactory, String sqlId, String tableSuffix,
            List<G> dataList) throws Exception {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        if (StringUtils.isEmpty(tableSuffix)) {
            batchInsertOrUpdate(sqlSessionFactory, sqlId, dataList);
            return;
        }
        List<Map<String, Object>> paras = new ArrayList<Map<String, Object>>();
        for (G g : dataList) {
            Map<String, Object> para = SQLParameterUtils.introspectForInsertOrUpdate(g);
            SQLParameterUtils.initTableNameToMap(para, tableSuffix);
            paras.add(para);
        }
        batchInsertOrUpdate(sqlSessionFactory, sqlId, paras);
    }

    public static <G> void batchInsertOrUpdateQuickly(SqlSessionFactory sqlSessionFactory, String sqlId,
            String tableSuffix, List<G> dataList)
            throws Exception {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        if (StringUtils.isEmpty(tableSuffix)) {
            batchInsertOrUpdateQuickly(sqlSessionFactory, sqlId, dataList);
            return;
        }
        List<Map<String, Object>> paras = new ArrayList<>();
        for (G g : dataList) {
            Map<String, Object> para = SQLParameterUtils.introspectForInsertOrUpdate(g);
            SQLParameterUtils.initTableNameToMap(para, tableSuffix);
            paras.add(para);
        }
        batchInsertOrUpdateQuickly(sqlSessionFactory, sqlId, paras);
    }

    public static <G> void resetDbValue(SqlSessionFactory sqlSessionFactory, String resetSqlId, G resetParameter,
            String sqlId, List<G> dataList) throws Exception {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        int tryTimes = MAX_TRY_TIMES;
        Exception throwException = null;
        while (tryTimes > 0) {
            SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
            try {
                sqlSession.update(resetSqlId, resetParameter);
                for (G data : dataList) {
                    sqlSession.update(sqlId, data);
                }
                sqlSession.commit(true);
            } catch (Exception e) {
                sqlSession.rollback(true);
                throwException = e;
                logger.error("对数据库中进行数据插入失败, resetSqlId:" + resetSqlId + ", SqlId:" + sqlId + ", Data:" +
                        dataList + ", Error msg:" + throwException.getMessage(), throwException);
                if (tryTimes <= 0) {
                    break;
                }
                tryTimes--;
            } finally {
                sqlSession.close();
            }
        }
        logger.error("对数据库中进行数据插入失败, SqlId:" + sqlId + ", Data:" + dataList + ",Error msg:" + throwException
                .getMessage(), throwException);
        throw throwException;
    }


    public static <G> void batchInsertOrUpdate(SqlSessionFactory sqlSessionFactory, String sqlId, List<G> dataList)
            throws Exception {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        if (dataList.size() > MAX_BATCH_SIZE) {
            subBatchInsertOrUpdate(sqlSessionFactory, sqlId, dataList);
            return;
        }
        int tryTimes = MAX_TRY_TIMES;
        Exception throwException = null;
        while (tryTimes > 0) {
            SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
            try {
                for (G data : dataList) {
                    sqlSession.update(sqlId, data);
                }
                sqlSession.commit(true);
                return;
            } catch (Exception e) {
                sqlSession.rollback(true);
                throwException = e;
                logger.warn("对数据库中进行数据插入失败, Error msg:" + e.getMessage(), e);
                if (tryTimes <= 0) {
                    break;
                }
                tryTimes--;
                throwException = e;
            } finally {
                sqlSession.close();
            }
        }
        logger.error("对数据库中进行数据插入失败, SqlId:" + sqlId + ", Data:" + dataList + ",Error msg:" + throwException
                .getMessage(), throwException);
        throw throwException;
    }

    public static void batchAddByTableName(SqlSessionFactory sqlSessionFactory, String sqlId,
            Map<String, Object> params)
            throws Exception {
        int tryTimes = MAX_TRY_TIMES;
        Exception throwException = null;
        do {
            SqlSession sqlSession = sqlSessionFactory.openSession(true);
            try {
                sqlSession.insert(sqlId, params);
                return;
            } catch (Exception e) {
                if (e instanceof SQLException) {
                    if (StringUtils.equalsIgnoreCase("23000", ((SQLException) e).getSQLState())) {
                        throw e;
                    }
                }
                throwException = e;
                logger.warn("对数据库中进行数据插入失败, SqlId:" + sqlId + ", Error msg:" + e.getMessage(), e);
            }
        } while (tryTimes-- > 0);
        logger.warn(
                "对数据库中进行数据插入失败, SqlId:" + sqlId + ", Data:" + params + ", Error msg:" + throwException.getMessage(),
                throwException);
        throw throwException;
    }

    @SuppressWarnings("unchecked")
    public static <G> List<G> queryList(SqlSessionFactory sqlSessionFactory, String queryId, Object parameter)
            throws Exception {
        if (parameter != null && parameter instanceof Map) {
            Map map = (Map) parameter;
            if (map.containsKey(SQLParameterUtils.SIZE_SQL_PARAMETER) && map
                    .containsKey(SQLParameterUtils.START_SQL_PARAMETER)) {
                Object startValue = map.get(SQLParameterUtils.START_SQL_PARAMETER);
                Object sizeValue = map.get(SQLParameterUtils.SIZE_SQL_PARAMETER);
                if (startValue != null && sizeValue != null) {
                    boolean parsed = false;
                    int start = 0;
                    int size = 0;
                    try {
                        start = Integer.parseInt(startValue.toString());
                        size = Integer.parseInt(sizeValue.toString());
                        parsed = true;
                    } catch (Exception e) {
                    }
                    if (parsed && size >= MAX_QUERY_SIZE) {
                        List<G> result = subQuery(sqlSessionFactory, queryId, map, start, size);
                        map.put(SQLParameterUtils.START_SQL_PARAMETER, startValue);
                        map.put(SQLParameterUtils.SIZE_SQL_PARAMETER, sizeValue);
                        return result;
                    }
                }
            } else {
                Set<Map.Entry> entries = map.entrySet();
                for (Map.Entry entry : entries) {
                    Object value = entry.getValue();
                    if (value == null) {
                        continue;
                    }
                    if (value instanceof List) {
                        List values = (List) value;
                        if (values.size() > DaoHelper.PARALLEL_QUERY_SIZE) {
                            return subQueries(sqlSessionFactory, queryId, map, entry.getKey(), values,
                                    DaoHelper.PARALLEL_QUERY_SIZE);
                        }
                    }
                }
            }
        }
        return directQueryList(sqlSessionFactory, queryId, parameter);
    }

    private static <G> List<G> subQueries(SqlSessionFactory sqlSessionFactory, String queryId, Map map, Object key,
            List values, int subSize) throws Exception {
        values = new ArrayList(new HashSet(values));
        List<List<?>> subIdsList = NormalUtils.splitList(values, subSize);
        List<G> results = new ArrayList<>();
        for (List<?> objects : subIdsList) {
            map.put(key, objects);
            List<G> innerResult = queryList(sqlSessionFactory, queryId, map);
            results.addAll(innerResult);
        }
        return results;
    }

    private static <G> List<G> subQuery(SqlSessionFactory sqlSessionFactory, String queryId, Map parameter, int start,
            int size) throws Exception {
        List<G> result = new ArrayList<>();
        for (int fetchedSize = 0; fetchedSize < size; ) {
            parameter.put(SQLParameterUtils.START_SQL_PARAMETER, start + fetchedSize);
            int needSize;
            if (size - fetchedSize > MAX_BATCH_SIZE) {
                needSize = MAX_BATCH_SIZE;
            } else {
                needSize = size - fetchedSize;
            }
            parameter.put(SQLParameterUtils.START_SQL_PARAMETER, needSize);
            List<G> subResult = directQueryList(sqlSessionFactory, queryId, parameter);
            if (CollectionUtils.isEmpty(subResult)) {
                break;
            }
            result.addAll(subResult);
            if (result.size() == size || subResult.size() < needSize) {
                break;
            } else {
                fetchedSize = result.size();
            }
        }
        return result;
    }

    private static <G> List<G> directQueryList(SqlSessionFactory sqlSessionFactory, String sqlId, Object parameter) {
        SqlSession sqlSession = sqlSessionFactory.openSession(true);
        try {
            return sqlSession.selectList(sqlId, parameter);
        } catch (Exception e) {
            logger.error("从数据库中进行数据查询失败。SqlId:" + sqlId + ", Data:" + parameter + ", Error msg:" + e.getMessage(), e);
            throw e;
        } finally {
            sqlSession.close();
        }
    }

    public static int sub(SqlSessionFactory sqlSessionFactory, String sqlId, Object g) throws Exception {
        Integer sum = queryObject(sqlSessionFactory, sqlId, g);
        return sum == null ? 0 : sum;
    }

    public static <G> G subAsObject(SqlSessionFactory sqlSessionFactory, String sqlId, Object g) throws Exception {
        return queryObject(sqlSessionFactory, sqlId, g);
    }

    public static <G> List<G> queryList(SqlSessionFactory sqlSessionFactory, String queryId, Object filterObject,
            int start,
            int size) throws Exception {
        return queryList(sqlSessionFactory, queryId, SQLParameterUtils.buildStartSizeMap(filterObject, start, size));
    }

    public static <M, G> List<G> queryListByStartId(SqlSessionFactory sqlSessionFactory, String queryId,
            Object filterObject,
            M startId, int size) throws Exception {
        return queryList(sqlSessionFactory, queryId,
                SQLParameterUtils.buildObjectStartIdSizeMap(filterObject, startId, size));
    }

    public static <M, G> List<G> queryListByEndId(SqlSessionFactory sqlSessionFactory, String queryId, Object filterObject,
            M endId, int size) throws Exception {
        return queryList(sqlSessionFactory, queryId,
                SQLParameterUtils.buildObjectEndIdSizeMap(filterObject, endId, size));
    }

    public static <K, V> Map<K, V> queryMap(SqlSessionFactory sqlSessionFactory, String queryId, Object parameter,
            String keyProperties) throws Exception {
        SqlSession sqlSession = sqlSessionFactory.openSession(true);
        try {
            return sqlSession.selectMap(queryId, parameter, keyProperties);
        } catch (Exception e) {
            logger.error(
                    "从数据库中进行数据查询失败, SqId:" + queryId + ", Parameter:" + parameter + ", Error msg:" + e.getMessage(), e);
            throw e;
        } finally {
            sqlSession.close();
        }
    }

    public static long count(SqlSessionFactory sqlSessionFactory, String queryId, Object parameter)
            throws Exception {
        SqlSession sqlSession = sqlSessionFactory.openSession(true);
        try {
            Long ret = (Long) sqlSession.selectOne(queryId, parameter);
            return ret == null ? 0 : ret;
        } catch (Exception e) {
            logger.error(
                    "从数据库中进行数据查询失败, SqId:" + queryId + ", Parameter:" + parameter + ", Error msg:" + e.getMessage(), e);
            throw e;
        } finally {
            sqlSession.close();
        }
    }

    public static int countReturnInt(SqlSessionFactory sqlSessionFactory, String queryId, Object parameter)
            throws Exception {
        SqlSession sqlSession = sqlSessionFactory.openSession(true);
        try {
            Integer ret = (Integer) sqlSession.selectOne(queryId, parameter);
            return ret == null ? 0 : ret;
        } catch (Exception e) {
            logger.error(
                    "从数据库中进行数据查询失败, SqId:" + queryId + ", Parameter:" + parameter + ", Error msg:" + e.getMessage(), e);
            throw e;
        } finally {
            sqlSession.close();
        }
    }

    public static <M> M queryId(SqlSessionFactory sqlSessionFactory, String queryId, Object parameter, M defaultValue)
            throws SQLException {
        SqlSession sqlSession = sqlSessionFactory.openSession(true);
        try {
            M ret = sqlSession.selectOne(queryId, parameter);
            return ret == null ? defaultValue : ret;
        } catch (Exception e) {
            logger.error(
                    "从数据库中进行数据查询失败, SqId:" + queryId + ", Parameter:" + parameter + ", Error msg:" + e.getMessage(), e);
            throw e;
        } finally {
            sqlSession.close();
        }
    }

    public static long queryIdReturnBigId(SqlSessionFactory sqlSessionFactory, String queryId, Object parameter,
            Long defaultValue)
            throws SQLException {
        return queryId(sqlSessionFactory, queryId, parameter, defaultValue);
    }

    @SuppressWarnings("unchecked")
    public static <G> G queryObject(SqlSessionFactory sqlSessionFactory, String queryId, Object parameter)
            throws Exception {
        SqlSession sqlSession = sqlSessionFactory.openSession(true);
        try {
            return (G) sqlSession.selectOne(queryId, parameter);
        } catch (Exception e) {
            logger.error(
                    "从数据库中进行数据查询失败, SqId:" + queryId + ", Parameter:" + parameter + ", Error msg:" + e.getMessage(), e);
            throw e;
        } finally {
            sqlSession.close();
        }
    }

    /**
     * IF FAILED WILL TRY FIVE TIMES
     *
     * @param sqlSessionFactory sqlSessionFactory
     * @param sqlId             sqlId
     * @param tableName         tableName
     * @param g                 g
     * @throws SQLException e
     */
    public static int update(SqlSessionFactory sqlSessionFactory, String sqlId, String tableName, Object g)
            throws Exception {
        if (StringUtils.isEmpty(tableName)) {
            return update(sqlSessionFactory, sqlId, g);
        }
        Map<String, Object> para = SQLParameterUtils.introspectForUpdate(g);
        SQLParameterUtils.initTableSuffix(para, tableName);
        return update(sqlSessionFactory, sqlId, para);
    }

    public static boolean updateReturnResult(SqlSessionFactory sqlSessionFactory, String sqlId, String tableName,
            Object g)
            throws Exception {
        return update(sqlSessionFactory, sqlId, g) > INSERT_ONDUPLICATEKEY_INGORE_INSERT;
    }


    /**
     * IF FAILED WILL TRY FIVE TIMES
     *
     * @param sqlSessionFactory sqlSessionFactory
     * @param sqlId             sqlId
     * @param g                 g
     * @throws SQLException e
     */
    public static int update(SqlSessionFactory sqlSessionFactory, String sqlId, Object g) throws Exception {
        int tryTimes = MAX_TRY_TIMES;
        Exception throwException = null;
        do {
            SqlSession sqlSession = sqlSessionFactory.openSession(true);
            try {
                return sqlSession.update(sqlId, g);
            } catch (Exception e) {
                logger.warn("对数据库中进行数据更新失败, SqlId:" + sqlId + ", Error msg:" + e.getMessage(), e);
                if (e instanceof SQLException) {
                    if (StringUtils.equalsIgnoreCase("23000", ((SQLException) e).getSQLState())) {
                        throw e;
                    }
                }
                throwException = e;
            } finally {
                sqlSession.close();
            }
        } while (tryTimes-- > 0);
        logger.error("对数据库中进行数据更新失败, SqlId:" + sqlId + ", Data:" + g + ", Error msg:" + throwException.getMessage(),
                throwException);
        throw throwException;
    }
    public static void insertOrUpdate(SqlSessionFactory sqlSessionFactory, String sqlId, Object g) throws Exception {
        int tryTimes = MAX_TRY_TIMES;
        Exception throwException = null;
        do {
            SqlSession sqlSession = sqlSessionFactory.openSession(true);
            try {
                sqlSession.insert(sqlId, g);
                return;
            } catch (Exception e) {
                logger.warn("对数据库中进行数据更新失败, SqlId:" + sqlId + ", Error msg:" + e.getMessage(), e);
                if (e instanceof SQLException) {
                    if (StringUtils.equalsIgnoreCase("23000", ((SQLException) e).getSQLState())) {
                        throw e;
                    }
                }
                throwException = e;
            } finally {
                sqlSession.close();
            }
        } while (tryTimes-- > 0);
        logger.error("对数据库中进行数据更新失败, SqlId:" + sqlId + ", Data:" + g + ", Error msg:" + throwException.getMessage(),
                throwException);
        throw throwException;
    }

    /**
     * IF FAILED WILL TRY FIVE TIMES
     *
     * @param sqlSessionFactory sqlSessionFactory
     * @param sqlId             sqlId
     * @param g                 g
     * @throws SQLException e
     */
    public static void insertOrUpdate(SqlSessionFactory sqlSessionFactory, String sqlId, String tableSuffix, Object g)
            throws Exception {
        if (StringUtils.isEmpty(tableSuffix)) {
            insertOrUpdate(sqlSessionFactory, sqlId, g);
            return;
        }
        Map<String, Object> para = SQLParameterUtils.introspectForUpdate(g);
        SQLParameterUtils.initTableSuffix(para, tableSuffix);
        insertOrUpdate(sqlSessionFactory, sqlId, para);
    }


    /**
     * IF FAILED WILL TRY FIVE TIMES
     * <p>
     * if insert will return true else return false
     *
     * @param sqlSessionFactory sqlSessionFactory
     * @param sqlId             sqlId
     * @param tableSuffix       tableSuffix
     * @param g                 g
     * @throws SQLException e
     */
    public static boolean insertOrIgnore(SqlSessionFactory sqlSessionFactory, String sqlId, String tableSuffix,
            Object g)
            throws Exception {
        if (StringUtils.isEmpty(tableSuffix)) {
            return insertOrIgnore(sqlSessionFactory, sqlId, g);
        }
        Map<String, Object> para = SQLParameterUtils.introspectForUpdate(g);
        SQLParameterUtils.initTableSuffix(para, tableSuffix);
        return insertOrIgnore(sqlSessionFactory, sqlId, para);
    }

    /**
     * IF FAILED WILL TRY FIVE TIMES
     * <p>
     * if insert will return true else return false
     *
     * @param sqlSessionFactory sqlSessionFactory
     * @param sqlId             sqlId
     * @param g                 g
     * @throws SQLException e
     */
    public static boolean insertOrIgnore(SqlSessionFactory sqlSessionFactory, String sqlId, Object g)
            throws Exception {
        int tryTimes = MAX_TRY_TIMES;
        Exception throwException = null;
        do {
            SqlSession sqlSession = sqlSessionFactory.openSession(true);
            try {
                return sqlSession.update(sqlId, g) > 0;
            } catch (Exception e) {
                logger.warn("对数据库中进行数据更新失败, SqlId:" + sqlId + ", Error msg:" + e.getMessage(), e);
                if (e instanceof SQLException) {
                    if (StringUtils.equalsIgnoreCase("23000", ((SQLException) e).getSQLState())) {
                        throw e;
                    }
                }
                throwException = e;
            } finally {
                sqlSession.close();
            }
        } while (tryTimes-- > 0);
        logger.error("对数据库中进行数据更新失败, SqlId:" + sqlId + ", Data:" + g + ", Error msg:" + throwException.getMessage(),
                throwException);
        throw throwException;
    }

    public static int insertReturnId(SqlSessionFactory sqlSessionFactory, String sqlId, String tableSuffix, Object g)
            throws Exception {
        if (StringUtils.isEmpty(tableSuffix)) {
            return insertReturnId(sqlSessionFactory, sqlId, g);
        }
        Map<String, Object> para = SQLParameterUtils.introspectForUpdate(g);
        SQLParameterUtils.initTableSuffix(para, tableSuffix);
        return insertReturnId(sqlSessionFactory, sqlId, para);
    }

    public static int insertReturnId(SqlSessionFactory sqlSessionFactory, String sqlId, Object g)
            throws Exception {
        int tryTimes = MAX_TRY_TIMES;
        Exception throwException = null;
        do {
            SqlSession sqlSession = sqlSessionFactory.openSession(true);
            try {
                return (int) sqlSession.insert(sqlId, g);
            } catch (Exception e) {
                logger.warn("对数据库中进行数据更新失败, SqlId:" + sqlId + ", Error msg:" + e.getMessage(), e);
                if (e instanceof SQLException) {
                    if (StringUtils.equalsIgnoreCase("23000", ((SQLException) e).getSQLState())) {
                        throw e;
                    }
                }
                throwException = e;
            } finally {
                sqlSession.close();
            }
        } while (tryTimes-- > 0);
        logger.error("对数据库中进行数据更新失败, SqlId:" + sqlId + ", Data:" + g + ", Error msg:" + throwException.getMessage(),
                throwException);
        throw throwException;
    }

    public static long insertReturnBigId(SqlSessionFactory sqlSessionFactory, String sqlId, String tableSuffix,
            Object g)
            throws Exception {
        if (StringUtils.isEmpty(tableSuffix)) {
            return insertReturnBigId(sqlSessionFactory, sqlId, g);
        }
        Map<String, Object> para = SQLParameterUtils.introspectForUpdate(g);
        SQLParameterUtils.initTableSuffix(para, tableSuffix);
        return insertReturnBigId(sqlSessionFactory, sqlId, para);

    }

    public static long insertReturnBigId(SqlSessionFactory sqlSessionFactory, String sqlId, Object g)
            throws Exception {
        int tryTimes = MAX_TRY_TIMES;
        Exception throwException = null;
        do {
            SqlSession sqlSession = sqlSessionFactory.openSession(true);
            try {
                return (long) sqlSession.insert(sqlId, g);
            } catch (Exception e) {
                logger.warn("对数据库中进行数据更新失败, SqlId:" + sqlId + ", Error msg:" + e.getMessage(), e);
                if (e instanceof SQLException) {
                    if (StringUtils.equalsIgnoreCase("23000", ((SQLException) e).getSQLState())) {
                        throw e;
                    }
                }
                throwException = e;
            } finally {
                sqlSession.close();
            }
        } while (tryTimes-- > 0);
        logger.error("对数据库中进行数据更新失败, SqlId:" + sqlId + ", Data:" + g + ", Error msg:" + throwException.getMessage(),
                throwException);
        throw throwException;
    }

    public static Integer queryMax(SqlSessionFactory sqlSessionFactory, String queryId, Object parameter)
            throws Exception {
        return queryObject(sqlSessionFactory, queryId, parameter);
    }

    public static Integer queryMin(SqlSessionFactory sqlSessionFactory, String queryId, Object parameter)
            throws Exception {
        return queryObject(sqlSessionFactory, queryId, parameter);
    }


    public static void createTable(SqlSessionFactory sqlSessionFactory, String sqlId, String sql) throws Exception {
        insertOrUpdate(sqlSessionFactory, sqlId, sql);
    }

}
