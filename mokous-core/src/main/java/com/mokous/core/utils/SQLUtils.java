// Copyright 2014 www.refanqie.com Inc. All Rights Reserved.

package com.mokous.core.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.mokous.core.dto.DbField.SELECT_ALL_KEY;
import com.mokous.core.dto.DbField.UNSIGNED_INT;
import com.mokous.core.dto.DbField.ZERO_ENABLE;
import com.mokous.core.dto.DbData;
import com.mokous.core.dto.StatusType;

/**
 * @author luofei@refanqie.com (Your Name Here)
 *
 */
public class SQLUtils {
    private static final Logger log = Logger.getLogger(SQLUtils.class);

    public static final int INSERT_ONDUPLICATEKEY_INGORE_INSERT = 0;
    public static final int INSERT_ONDUPLICATEKEY_UPDATE_INSERT = 1;
    public static final int INSERT_ONDUPLICATEKEY_UPDATE_UPDATE = 2;

    public static <G> void resetDbValue(SqlMapClient sqlMapClient, String initSqlId, Object initParas,
            String updateSqlId, List<G> datas) throws SQLException {
        if (datas == null || datas.isEmpty()) {
            return;
        }
        sqlMapClient.startTransaction();
        try {
            sqlMapClient.startBatch();
            sqlMapClient.update(initSqlId, initParas);
            for (G data : datas) {
                sqlMapClient.update(updateSqlId, data);
            }
            sqlMapClient.executeBatch();
            sqlMapClient.commitTransaction();
        } catch (SQLException e) {
            log.error("对数据库中进行数据插入失败, Errmsg:" + e.getMessage(), e);
            throw e;
        } finally {
            sqlMapClient.endTransaction();
        }

    }

    private static final int MAX_BATCH_SIZE = 1000;

    private static <G> void subBatchInsertOrUpdate(SqlMapClient sqlMapClient, String sqlId, List<G> datas)
            throws SQLException {
        if (datas == null || datas.isEmpty()) {
            return;
        }
        int fromIndex = 0;
        int toIndex = MAX_BATCH_SIZE;
        do {
            toIndex = datas.size() > toIndex ? toIndex : datas.size();
            List<G> subDatas = datas.subList(fromIndex, toIndex);
            batchInsertOrUpdate(sqlMapClient, sqlId, subDatas);
            fromIndex += subDatas.size();
            if (fromIndex >= datas.size()) {
                break;
            }
            toIndex = (fromIndex + subDatas.size());
        } while (true);
    }

    public static <G> void batchInsertOrUpdate(SqlMapClient sqlMapClient, String sqlId, String tableSuffix,
            List<G> datas) throws SQLException {
        if (datas == null || datas.isEmpty()) {
            return;
        }
        List<Map<String, Object>> paras = new ArrayList<Map<String, Object>>();
        for (G g : datas) {
            Map<String, Object> e = new HashMap<String, Object>();
            e.put("g", g);
            initTableSuffix(e, tableSuffix);
            paras.add(e);
        }
        batchInsertOrUpdate(sqlMapClient, sqlId, paras);
    }

    public static <G> void batchInsertOrUpdate(SqlMapClient sqlMapClient, String sqlId, List<G> datas)
            throws SQLException {
        if (datas == null || datas.isEmpty()) {
            return;
        }
        if (datas.size() > MAX_BATCH_SIZE) {
            subBatchInsertOrUpdate(sqlMapClient, sqlId, datas);
            return;
        }
        int tryTiems = 3;
        SQLException throwe = null;
        while (tryTiems > 0) {
            sqlMapClient.startTransaction();
            try {
                sqlMapClient.startBatch();
                for (G data : datas) {
                    sqlMapClient.update(sqlId, data);
                }
                sqlMapClient.executeBatch();
                sqlMapClient.commitTransaction();
                return;
            } catch (SQLException e) {
                log.warn("对数据库中进行数据插入失败, Errmsg:" + e.getMessage(), e);
                if (tryTiems <= 0) {
                    break;
                }
                tryTiems--;
                throwe = e;
            } finally {
                sqlMapClient.endTransaction();
            }
        }
        if (throwe != null) {
            log.error("对数据库中进行数据插入失败, Errmsg:" + throwe.getMessage(), throwe);
            throw throwe;
        }
    }

    public static void batchAddByTableName(SqlMapClient sqlMapClient, String sqlId, Map<String, Object> params)
            throws SQLException {
        int tryTimes = 5;
        SQLException throwe = null;
        do {
            try {
                sqlMapClient.insert(sqlId, params);
                return;
            } catch (SQLException e) {
                if (StringUtils.equalsIgnoreCase("23000", e.getSQLState())) {
                    throw e;
                }
                throwe = e;
            }
        } while (tryTimes-- > 0);
        if (throwe != null) {
            log.error("对数据库中进行数据插入失败, Errmsg:" + throwe.getMessage(), throwe);
        }
        throw throwe;
    }

    @SuppressWarnings("unchecked")
    public static <G> List<G> queryList(SqlMapClient sqlMapClient, String queryId, Object parameter)
            throws SQLException {
        try {
            return sqlMapClient.queryForList(queryId, parameter);
        } catch (SQLException e) {
            log.error("从数据库中进行数据查询失败, Errmsg:" + e.getMessage(), e);
            throw e;
        }
    }

    public static <G> List<G> queryList(SqlMapClient sqlMapClient, String queryId, Object filterObject, int start,
            int size) throws SQLException {
        return queryList(sqlMapClient, queryId, buildStartSizeMap(filterObject, start, size));
    }

    public static <G> List<G> queryListByStartId(SqlMapClient sqlMapClient, String queryId, Object filterObject,
            int startId, int size) throws SQLException {
        return queryList(sqlMapClient, queryId, buildObjectStartIdSizeMap(filterObject, startId, size));
    }

    public static <G> List<G> queryListByEndId(SqlMapClient sqlMapClient, String queryId, Object filterObject,
            int endId, int size) throws SQLException {
        return queryList(sqlMapClient, queryId, buildObjectEndIdSizeMap(filterObject, endId, size));
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> queryMap(SqlMapClient sqlMapClient, String queryId, Object parameter,
            String keyProperties) throws SQLException {
        try {
            return sqlMapClient.queryForMap(queryId, parameter, keyProperties);
        } catch (SQLException e) {
            log.error("从数据库中进行数据查询失败, Errmsg:" + e.getMessage(), e);
            throw e;
        }
    }

    public static long count(SqlMapClient sqlMapClient, String queryId, Object parameter) throws SQLException {
        try {
            Long ret = (Long) sqlMapClient.queryForObject(queryId, parameter);
            return ret == null ? 0 : ret.longValue();
        } catch (SQLException e) {
            log.error("从数据库中进行数据查询失败, Errmsg:" + e.getMessage(), e);
            throw e;
        }
    }

    public static int countReturnInt(SqlMapClient sqlMapClient, String queryId, Object parameter) throws SQLException {
        try {
            Integer ret = (Integer) sqlMapClient.queryForObject(queryId, parameter);
            return ret == null ? 0 : ret.intValue();
        } catch (SQLException e) {
            log.error("从数据库中进行数据查询失败, Errmsg:" + e.getMessage(), e);
            throw e;
        }

    }

    public static int queryId(SqlMapClient sqlMapClient, String queryId, Object parameter, int defaultInt)
            throws SQLException {
        try {
            Integer ret = (Integer) sqlMapClient.queryForObject(queryId, parameter);
            return ret == null ? defaultInt : ret.intValue();
        } catch (SQLException e) {
            log.error("从数据库中进行数据查询失败, Errmsg:" + e.getMessage(), e);
            throw e;
        }

    }

    @SuppressWarnings("unchecked")
    public static <G> G queryObject(SqlMapClient sqlMapClient, String queryId, Object parameter) throws SQLException {
        try {
            return (G) sqlMapClient.queryForObject(queryId, parameter);
        } catch (SQLException e) {
            log.error("从数据库中进行数据查询失败, Errmsg:" + e.getMessage(), e);
            throw e;
        }
    }

    public static Map<String, Object> introspect(Object obj) {
        return introspect(obj, false, null);
    }

    /**
     * if will ignore the int value is zero if the dose not have @ZERO_ENABLE
     * annotation
     */
    public static Map<String, Object> introspectForQuery(Object obj) {
        return introspect(obj, true, null);
    }

    public static Map<String, Object> introspect(String tableSuffix, Object obj, boolean ignoreZero,
            List<String> noIgnoreZeroField) {
        Map<String, Object> result = introspect(obj, ignoreZero, noIgnoreZeroField);
        initTableSuffix(result, tableSuffix);
        return result;
    }

    public static Map<String, Object> initTableSuffix(String tableSuffix) {
        Map<String, Object> result = new HashMap<String, Object>();
        initPutTableSuffix(result, tableSuffix);
        return result;
    }

    private static void initPutTableSuffix(Map<String, Object> result, String tableSuffix) {
        if (!StringUtils.isEmpty(tableSuffix) && !StringUtils.startsWith(tableSuffix, "_")) {
            tableSuffix = "_" + tableSuffix;
        }
        result.put("tableSuffix", tableSuffix);
    }

    private static void initTableSuffix(Map<String, Object> result, String tableSuffix) {
        initPutTableSuffix(result, tableSuffix);
    }

    public static Map<String, Object> introspect(Object obj, boolean ignoreZero, List<String> noIgnoreZeroField) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (obj == null) {
            return result;
        }
        try {
            BeanInfo info = Introspector.getBeanInfo(obj.getClass());
            for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                if (pd.getPropertyType() == Class.class) {
                    continue;
                }
                Method reader = pd.getReadMethod();
                if (reader != null) {
                    Object putObject = reader.invoke(obj);
                    if (putObject == null) {
                        continue;
                    }
                    if (putObject instanceof Date) {
                        putObject = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(putObject);
                    } else if (putObject instanceof String) {
                        if (((String) putObject).isEmpty()) {
                            continue;
                        }
                    } else if (ignoreZero
                            && !BeanAnnotatiionUtils.isAnnotationPresent(obj.getClass(), pd.getName(),
                                    ZERO_ENABLE.class)) {
                        if (noIgnoreZeroField == null || !noIgnoreZeroField.contains(pd.getName())) {
                            if (!StringUtils.equals(DbData.STATUS_COLUMN, pd.getName())) {
                                // status默认0 如果需要查状态为0的数据时，不要过滤，所以此处需要做一点处理 较为丑陋
                                if (isZero(putObject)) {
                                    continue;
                                }
                            } else {
                                // status全部时，由于数据库中无此状态，所以无需加入到查询条件中，所以此处需要做一点处理
                                // 较为丑陋
                                int status = Integer.valueOf(String.valueOf(putObject));
                                if (status == StatusType.STATUS_ALL.getStatus()) {
                                    continue;
                                }
                            }
                        }
                        // 一些可以为0的标签，如果其值为9999，则略过
                        if (noIgnoreZeroField != null && noIgnoreZeroField.contains(pd.getName())) {
                            int value = Integer.valueOf(String.valueOf(putObject));
                            if (value == StatusType.STATUS_ALL.getStatus()) {
                                continue;
                            }
                        }
                    }
                    // 对于id字段，如果不大于0，不进查询条件
                    if (StringUtils.equals(DbData.ID_COLUMN, pd.getName())) {
                        int value = Integer.valueOf(String.valueOf(putObject));
                        if (value <= 0) {
                            continue;
                        }
                    }
                    if (BeanAnnotatiionUtils.isAnnotationPresent(obj.getClass(), pd.getName(), UNSIGNED_INT.class)) {
                        int value = Integer.valueOf(String.valueOf(putObject));
                        if (value < 0) {
                            continue;
                        }
                    }
                    // @SEL_ALL_KEY
                    // 对于arch字段，如果值为9999，代表查询全部，不用指定查询条件即条件设空
                    // check int attribute all value and String
                    // attribute all value
                    if (BeanAnnotatiionUtils.isAnnotationPresent(obj.getClass(), pd.getName(), SELECT_ALL_KEY.class)) {
                        if (StringUtils.equalsIgnoreCase(String.valueOf(DbData.STATUS_ALL), putObject.toString())
                                || StringUtils.equalsIgnoreCase(DbData.STATUS_ALL_DESC, putObject.toString())) {
                            continue;
                        }
                    }
                    result.put(pd.getName(), putObject);
                }
            }
        } catch (Exception e) {
        }
        return result;
    }

    private static boolean isZero(Object object) {
        return isZero(object, int.class, Integer.class, float.class, Float.class, double.class, Double.class,
                long.class, Long.class, short.class, Short.class);
    }

    private static boolean isZero(Object object, Class<?>... clazzes) {
        if (object == null) {
            return false;
        }
        Class<?> objectClass = object.getClass();
        for (Class<?> clazz : clazzes) {
            if (clazz == objectClass) {
                return ((Number) object).equals(clazz.cast(0));
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> buildStartSizeMap(Object object, int start, int size) {
        Map<String, Object> ret;
        if (object != null) {
            if (object instanceof Map) {
                return buildStartSizeMap((Map<String, Object>) object, start, size);
            }
            try {
                ret = introspectForQuery(object);
                ret.put("start", start);
                ret.put("size", size);
                return ret;
            } catch (Exception e) {
                log.error("Change object to map failed.Errmsg:" + e.getMessage() + ", Data:" + object, e);
                return buildStartSizeMap(start, size);
            }
        } else {
            return buildStartSizeMap(start, size);
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> buildObjectStartIdSizeMap(Object object, int startId, int size) {
        Map<String, Object> ret;
        if (object != null) {
            if (object instanceof Map) {
                return buildStartIdSizeMap((Map<String, Object>) object, startId, size);
            }
            try {
                ret = introspectForQuery(object);
                ret.put("startId", startId);
                ret.put("size", size);
                return ret;
            } catch (Exception e) {
                log.error("Change object to map failed.Errmsg:" + e.getMessage() + ", Data:" + object, e);
                return buildStartIdSizeMap(startId, size);
            }
        } else {
            return buildStartIdSizeMap(startId, size);
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> buildObjectEndIdSizeMap(Object object, int endId, int size) {
        Map<String, Object> ret;
        if (object != null) {
            if (object instanceof Map) {
                return buildEndIdSizeMap((Map<String, Object>) object, endId, size);
            }
            try {
                ret = introspectForQuery(object);
                ret.put("endId", endId);
                ret.put("size", size);
                return ret;
            } catch (Exception e) {
                log.error("Change object to map failed.Errmsg:" + e.getMessage() + ", Data:" + object, e);
                return buildEndIdSizeMap(endId, size);
            }
        } else {
            return buildEndIdSizeMap(endId, size);
        }
    }

    public static Map<String, Object> buildStartSizeMap(Map<String, Object> objectMap, int start, int size) {
        try {
            objectMap.put("start", start);
            objectMap.put("size", size);
            return objectMap;
        } catch (Exception e) {
            log.error("Change objectMap to map failed.Errmsg:" + e.getMessage() + ", Data:" + objectMap, e);
            return buildStartSizeMap(start, size);
        }
    }

    public static Map<String, Object> buildStartIdSizeMap(Map<String, Object> objectMap, int startId, int size) {
        try {
            objectMap.put("startId", startId);
            objectMap.put("size", size);
            return objectMap;
        } catch (Exception e) {
            log.error("Change objectMap to map failed.Errmsg:" + e.getMessage() + ", Data:" + objectMap, e);
            return buildStartIdSizeMap(startId, size);
        }
    }

    public static Map<String, Object> buildEndIdSizeMap(Map<String, Object> objectMap, int endId, int size) {
        try {
            objectMap.put("endId", endId);
            objectMap.put("size", size);
            return objectMap;
        } catch (Exception e) {
            log.error("Change objectMap to map failed.Errmsg:" + e.getMessage() + ", Data:" + objectMap, e);
            return buildEndIdSizeMap(endId, size);
        }
    }



    public static Map<String, Object> buildStartSizeMap(int start, int size) {
        Map<String, Object> ret = new HashMap<String, Object>();
        ret.put("start", start);
        ret.put("size", size);
        return ret;
    }

    public static String formatOrder(String order) {
        return order == null || StringUtils.equalsIgnoreCase("ASC", order) ? " ASC " : " DESC ";
    }

    /**
     * IF FAILED WILL TRY FIVE TIMES
     * 
     * @param sqlMapClient
     * @param sqlId
     * @param g
     * @throws SQLException
     */
    public static int update(SqlMapClient sqlMapClient, String sqlId, String tableSuffix, Object g) throws SQLException {
        Map<String, Object> para = new HashMap<>();
        para.put("g", g);
        SQLUtils.initTableSuffix(para, tableSuffix);
        return update(sqlMapClient, sqlId, para);
    }


    /**
     * IF FAILED WILL TRY FIVE TIMES
     * 
     * @param sqlMapClient
     * @param sqlId
     * @param g
     * @throws SQLException
     */
    public static int update(SqlMapClient sqlMapClient, String sqlId, Object g) throws SQLException {
        int tryTimes = 5;
        SQLException throwe = null;
        do {
            try {
                return sqlMapClient.update(sqlId, g);
            } catch (SQLException e) {
                if (StringUtils.equalsIgnoreCase("23000", e.getSQLState())) {
                    throw e;
                }
                throwe = e;
            }
        } while (tryTimes-- > 0);
        if (throwe != null) {
            log.error("对数据库中进行数据插入失败, Errmsg:" + throwe.getMessage(), throwe);
        }
        throw throwe;
    }

    /**
     * IF FAILED WILL TRY FIVE TIMES
     * 
     * @param sqlMapClient
     * @param sqlId
     * @param g
     * @throws SQLException
     */
    public static void insertOrUpdate(SqlMapClient sqlMapClient, String sqlId, Object g) throws SQLException {
        int tryTimes = 5;
        SQLException throwe = null;
        do {
            try {
                sqlMapClient.insert(sqlId, g);
                return;
            } catch (SQLException e) {
                if (StringUtils.equalsIgnoreCase("23000", e.getSQLState())) {
                    throw e;
                }
                throwe = e;
            }
        } while (tryTimes-- > 0);
        if (throwe != null) {
            log.error("对数据库中进行数据插入失败, Errmsg:" + throwe.getMessage(), throwe);
        }
        throw throwe;
    }

    /**
     * IF FAILED WILL TRY FIVE TIMES
     * 
     * @param sqlMapClient
     * @param sqlId
     * @param g
     * @throws SQLException
     */
    public static void insertOrUpdate(SqlMapClient sqlMapClient, String sqlId, String tableSuffix, Object g)
            throws SQLException {
        Map<String, Object> para = new HashMap<>();
        para.put("g", g);
        SQLUtils.initTableSuffix(para, tableSuffix);
        insertOrUpdate(sqlMapClient, sqlId, para);
    }


    /**
     * IF FAILED WILL TRY FIVE TIMES
     * 
     * if insert will return true else return false
     * 
     * @param sqlMapClient
     * @param insertOrIgnoreSqlId
     * @param appRes
     * @throws SQLException
     */
    public static boolean insertOrIgnore(SqlMapClient sqlMapClient, String sqlId, String tableSuffix, Object g)
            throws SQLException {
        Map<String, Object> para = new HashMap<>();
        para.put("g", g);
        SQLUtils.initTableSuffix(para, tableSuffix);
        return insertOrIgnore(sqlMapClient, sqlId, para);
    }

    /**
     * IF FAILED WILL TRY FIVE TIMES
     * 
     * if insert will return true else return false
     * 
     * @param sqlMapClient
     * @param insertOrIgnoreSqlId
     * @param g
     * @throws SQLException
     */
    public static boolean insertOrIgnore(SqlMapClient sqlMapClient, String insertOrIgnoreSqlId, Object g)
            throws SQLException {
        int tryTimes = 5;
        SQLException throwe = null;
        do {
            try {
                return sqlMapClient.update(insertOrIgnoreSqlId, g) > 0;
            } catch (SQLException e) {
                if (StringUtils.equalsIgnoreCase("23000", e.getSQLState())) {
                    throw e;
                }
                throwe = e;
            }
        } while (tryTimes-- > 0);
        if (throwe != null) {
            log.error("对数据库中进行数据插入失败, Errmsg:" + throwe.getMessage(), throwe);
        }
        throw throwe;
    }

    /**
     * IF FAILED WILL TRY FIVE TIMES
     * 
     * @param sqlMapClient
     * @param sqlId
     * @param appRes
     * @throws SQLException
     */
    public static int insertReturnId(SqlMapClient sqlMapClient, String sqlId, String tableSuffix, Object g)
            throws SQLException {
        Map<String, Object> para = new HashMap<>();
        para.put("g", g);
        SQLUtils.initTableSuffix(para, tableSuffix);
        return insertReturnId(sqlMapClient, sqlId, para);
    }

    /**
     * IF FAILED WILL TRY FIVE TIMES
     * 
     * @param sqlMapClient
     * @param sqlId
     * @param g
     * @throws SQLException
     */
    public static int insertReturnId(SqlMapClient sqlMapClient, String sqlId, Object appRes) throws SQLException {
        int tryTimes = 5;
        SQLException throwe = null;
        do {
            try {
                return (int) sqlMapClient.insert(sqlId, appRes);
            } catch (SQLException e) {
                if (StringUtils.equalsIgnoreCase("23000", e.getSQLState())) {
                    throw e;
                }
                throwe = e;
            }
        } while (tryTimes-- > 0);
        if (throwe != null) {
            log.error("对数据库中进行数据插入失败, Errmsg:" + throwe.getMessage(), throwe);
        }
        throw throwe;
    }

    /**
     * IF FAILED WILL TRY FIVE TIMES
     * 
     * @param sqlMapClient
     * @param sqlId
     * @param g
     * @throws SQLException
     */
    public static long insertReturnBigId(SqlMapClient sqlMapClient, String sqlId, String tableSuffix, Object g)
            throws SQLException {
        Map<String, Object> para = new HashMap<>();
        para.put("g", g);
        SQLUtils.initTableSuffix(para, tableSuffix);
        return insertReturnBigId(sqlMapClient, sqlId, para);

    }

    /**
     * IF FAILED WILL TRY FIVE TIMES
     * 
     * @param sqlMapClient
     * @param sqlId
     * @param g
     * @throws SQLException
     */
    public static long insertReturnBigId(SqlMapClient sqlMapClient, String sqlId, Object g) throws SQLException {
        int tryTimes = 5;
        SQLException throwe = null;
        do {
            try {
                return (long) sqlMapClient.insert(sqlId, g);
            } catch (SQLException e) {
                if (StringUtils.equalsIgnoreCase("23000", e.getSQLState())) {
                    throw e;
                }
                throwe = e;
            }
        } while (tryTimes-- > 0);
        if (throwe != null) {
            log.error("对数据库中进行数据插入失败, Errmsg:" + throwe.getMessage(), throwe);
        }
        throw throwe;
    }

    public static String formatLeftLike(String value) {
        return value == null ? "%%" : "%" + value;
    }

    public static String formatFullLike(String value) {
        return value == null ? "%%" : "%" + value + "%";
    }

    public static Integer queryMax(SqlMapClient sqlMapClient, String queryId, Object parameter) throws SQLException {
        return queryObject(sqlMapClient, queryId, parameter);
    }

    public static Integer queryMin(SqlMapClient sqlMapClient, String queryId, Object parameter) throws SQLException {
        return queryObject(sqlMapClient, queryId, parameter);
    }

    public static Map<String, Object> buildStartIdSizeMap(int startId, int size) {
        Map<String, Object> ret = new HashMap<String, Object>();
        ret.put("startId", startId);
        ret.put("size", size);
        return ret;
    }

    public static Map<String, Object> buildShrdingStartIdSizeMap(String tableSuffix, int startId, int size) {
        Map<String, Object> ret = buildStartIdSizeMap(startId, size);
        initTableSuffix(ret, tableSuffix);
        return ret;
    }

    public static Map<String, Object> buildEndIdSizeMap(int endId, int size) {
        Map<String, Object> ret = new HashMap<String, Object>();
        ret.put("endId", endId);
        ret.put("size", size);
        return ret;
    }

    public static void createTable(SqlMapClient sqlMapClient, String sqlId, String sql) throws SQLException {
        int tryTimes = 5;
        SQLException throwe = null;
        do {
            try {
                sqlMapClient.insert(sqlId, sql);
                return;
            } catch (SQLException e) {
                if (StringUtils.equalsIgnoreCase("23000", e.getSQLState())) {
                    throw e;
                }
                throwe = e;
            }
        } while (tryTimes-- > 0);
        if (throwe != null) {
            log.error("对数据库中进行表创建失败, Errmsg:" + throwe.getMessage(), throwe);
        }
        throw throwe;
    }

    public static void initStartEndTime(Map<String, Object> para, String st, String et) {
        if (!StringUtils.isEmpty(st)) {
            para.put("st", st);
        }
        if (!StringUtils.isEmpty(et)) {
            para.put("et", et);
        }
    }

    public static Object buildIdShardingMap(int id, String tableSuffix) {
        Map<String, Object> para = new HashMap<String, Object>();
        para.put("id", id);
        initTableSuffix(para, tableSuffix);
        return para;
    }

    public static Map<String, Object> buildIdsPara(List<Integer> ids) {
        Map<String, Object> para = new HashMap<String, Object>();
        para.put("ids", ids);
        return para;
    }

    public static Map<String, Object> buildIdsPara(String tableSuffix, List<Integer> ids) {
        Map<String, Object> para = buildIdsPara(ids);
        initTableSuffix(para, tableSuffix);
        return para;
    }
}
