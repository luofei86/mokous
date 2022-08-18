//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.db.utils;

import com.mokous.base.domain.model.DbFieldConstraint;
import com.mokous.base.domain.model.DbFields;
import com.mokous.base.utils.BeanAnnotationUtils;
import com.mokous.base.utils.DateFormatPatternUtils;
import com.mokous.db.enums.EnumSqlOrderType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author luofei
 * Generate 2020/1/12
 */
public class SQLParameterUtils {
    private static final Logger logger = LoggerFactory.getLogger(SQLParameterUtils.class);
    public static final String SIZE_SQL_PARAMETER = "size";
    public static final String START_SQL_PARAMETER = "start";
    public static final String START_ID_SQL_PARAMETER = "startId";
    public static final String END_ID_SQL_PARAMETER = "endId";
    public static final String ID_SQL_PARAMETER = "id";
    public static final String IDS_SQL_PARAMETER = "ids";
    private static final String TABLE_NAME_PARAMETER = "tableName";
    private static final String ST_SQL_PARAMETER = "st";
    private static final String ET_SQL_PARAMETER = "et";
    private static final String ORDER_COLUMN_SORT_SQL_PARAMETER = "orderColumnSort";
    private static final String ORDER_COLUMN_SQL_PARAMETER = "orderColumn";
    private static final String EXPRESSIONS_SQL_PARAMETER = "expressions";


    public static Map<String, Object> introspect(Object obj, boolean ignoreZero, List<String> noIgnoreZeroField) {
        Map<String, Object> result = new HashMap<>();
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
                        putObject = DateFormatUtils.format((Date) putObject, DateFormatPatternUtils.YMD_HMS_FORMAT);
                    } else if (putObject instanceof String) {
                        if (((String) putObject).isEmpty()) {
                            continue;
                        }
                    } else if (ignoreZero
                            && !BeanAnnotationUtils.isAnnotationPresent(obj.getClass(), pd.getName(),
                            DbFieldConstraint.ZERO_ENABLE.class)) {
                        if (noIgnoreZeroField == null || !noIgnoreZeroField.contains(pd.getName())) {
                            if (!StringUtils.equals(DbFields.DEL_FLAG_COLUMN, pd.getName())) {
                                // status默认0 如果需要查状态为0的数据时，不要过滤，所以此处需要做一点处理 较为丑陋
                                if (isZero(putObject)) {
                                    continue;
                                }
                            } else {
                                // status全部时，由于数据库中无此状态，所以无需加入到查询条件中，所以此处需要做一点处理
                                // 较为丑陋
                                int status = Integer.parseInt(String.valueOf(putObject));
                                if (status == DbFields.SELECT_ALL_INT_COLUMN_ALL_VALUE) {
                                    continue;
                                }
                            }
                        }
                        // 一些可以为0的标签，如果其值为9999，则略过
                        if (noIgnoreZeroField != null && noIgnoreZeroField.contains(pd.getName())) {
                            int value = Integer.parseInt(String.valueOf(putObject));
                            if (value == DbFields.SELECT_ALL_INT_COLUMN_ALL_VALUE) {
                                continue;
                            }
                        }
                    }
                    // 对于id字段，如果不大于0，不进查询条件
                    if (StringUtils.equals(DbFields.ID_COLUMN, pd.getName())) {
                        int value = Integer.parseInt(String.valueOf(putObject));
                        if (value <= 0) {
                            continue;
                        }
                    }
                    if (BeanAnnotationUtils
                            .isAnnotationPresent(obj.getClass(), pd.getName(), DbFieldConstraint.UNSIGNED_INT.class)) {
                        int value = Integer.parseInt(String.valueOf(putObject));
                        if (value < 0) {
                            continue;
                        }
                    }
                    // @SEL_ALL_KEY
                    // 对于arch字段，如果值为9999，代表查询全部，不用指定查询条件即条件设空
                    // check int attribute all value and String
                    // attribute all value
                    if (BeanAnnotationUtils.isAnnotationPresent(obj.getClass(), pd.getName(),
                            DbFieldConstraint.SELECT_ALL_KEY.class)) {
                        if (StringUtils.equalsIgnoreCase(DbFields.SELECT_ALL_INT_COLUMN_ALL_VALUE_DESC,
                                putObject.toString())
                                || StringUtils
                                .equalsIgnoreCase(DbFields.SELECT_ALL_STRING_COLUMN_ALL_VALUE, putObject.toString())) {
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

    public static void initTableNameToMap(Map<String, Object> para, String tableSuffix) {
        if (para != null && StringUtils.isNotBlank(tableSuffix)) {
            para.put(TABLE_NAME_PARAMETER, tableSuffix);
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
        Map<String, Object> result = new HashMap<>();
        initPutTableSuffix(result, tableSuffix);
        return result;
    }

    private static void initPutTableSuffix(Map<String, Object> result, String tableSuffix) {
        if (!StringUtils.isEmpty(tableSuffix) && !StringUtils.startsWith(tableSuffix, "_")) {
            tableSuffix = "_" + tableSuffix;
        }
        result.put("tableSuffix", tableSuffix);
    }

    public static void initTableSuffix(Map<String, Object> result, String tableSuffix) {
        initPutTableSuffix(result, tableSuffix);
    }

    private static boolean isZero(Object object) {
        return isZero(object, int.class, Integer.class, float.class, Float.class, double.class, Double.class,
                long.class, Long.class, short.class, Short.class);
    }

    private static boolean isZero(Object object, Class<?>... classList) {
        if (object == null) {
            return false;
        }
        Class<?> objectClass = object.getClass();
        for (Class<?> clazz : classList) {
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
                ret.put(START_SQL_PARAMETER, start);
                ret.put(SIZE_SQL_PARAMETER, size);
                return ret;
            } catch (Exception e) {
                logger.error("Change object to map failed.Error msg:" + e.getMessage() + ", Data:" + object, e);
                return buildStartSizeMap(start, size);
            }
        } else {
            return buildStartSizeMap(start, size);
        }
    }

    @SuppressWarnings("unchecked")
    public static <M> Map<String, Object> buildObjectStartIdSizeMap(Object object, M startId, int size) {
        Map<String, Object> ret;
        if (object != null) {
            if (object instanceof Map) {
                return buildStartIdSizeMap((Map<String, Object>) object, startId, size);
            }
            try {
                ret = introspectForQuery(object);
                ret.put(START_ID_SQL_PARAMETER, startId);
                ret.put(SIZE_SQL_PARAMETER, size);
                return ret;
            } catch (Exception e) {
                logger.error("Change object to map failed.Error msg:" + e.getMessage() + ", Data:" + object, e);
                return buildStartIdSizeMap(startId, size);
            }
        } else {
            return buildStartIdSizeMap(startId, size);
        }
    }

    @SuppressWarnings("unchecked")
    public static <M> Map<String, Object> buildObjectEndIdSizeMap(Object object, M endId, int size) {
        Map<String, Object> ret;
        if (object != null) {
            if (object instanceof Map) {
                return buildEndIdSizeMap((Map<String, Object>) object, endId, size);
            }
            try {
                ret = introspectForQuery(object);
                ret.put(END_ID_SQL_PARAMETER, endId);
                ret.put(SIZE_SQL_PARAMETER, size);
                return ret;
            } catch (Exception e) {
                logger.error("Change object to map failed. Error msg:" + e.getMessage() + ", Data:" + object, e);
                return buildEndIdSizeMap(endId, size);
            }
        } else {
            return buildEndIdSizeMap(endId, size);
        }
    }

    public static Map<String, Object> buildStartSizeMap(Map<String, Object> objectMap, int start, int size) {
        try {
            objectMap.put(START_SQL_PARAMETER, start);
            objectMap.put(SIZE_SQL_PARAMETER, size);
            return objectMap;
        } catch (Exception e) {
            logger.error("Change objectMap to map failed.Error msg:" + e.getMessage() + ", Data:" + objectMap, e);
            return buildStartSizeMap(start, size);
        }
    }

    public static <M> Map<String, Object> buildStartIdSizeMap(Map<String, Object> objectMap, M startId, int size) {
        try {
            objectMap.put(START_ID_SQL_PARAMETER, startId);
            objectMap.put(SIZE_SQL_PARAMETER, size);
            return objectMap;
        } catch (Exception e) {
            logger.error("Change objectMap to map failed. Error msg:" + e.getMessage() + ", Data:" + objectMap, e);
            return buildStartIdSizeMap(startId, size);
        }
    }

    public static <M> Map<String, Object> buildEndIdSizeMap(Map<String, Object> objectMap, M endId, int size) {
        try {
            objectMap.put(END_ID_SQL_PARAMETER, endId);
            objectMap.put(SIZE_SQL_PARAMETER, size);
            return objectMap;
        } catch (Exception e) {
            logger.error("Change objectMap to map failed.Error msg:" + e.getMessage() + ", Data:" + objectMap, e);
            return buildEndIdSizeMap(endId, size);
        }
    }


    public static <M> Map<String, Object> buildIdsPara(List<M> ids) {
        Map<String, Object> para = new HashMap<>();
        para.put(IDS_SQL_PARAMETER, ids);
        return para;
    }

    public static final Map<String, Object> buildExpressionPara(List<String> expressions) {
        Map<String, Object> para = new HashMap<>();
        para.put(EXPRESSIONS_SQL_PARAMETER, expressions);
        return para;
    }


    public static Map<String, Object> buildStartSizeMap(int start, int size) {
        Map<String, Object> ret = new HashMap<>();
        ret.put(START_SQL_PARAMETER, start);
        ret.put(SIZE_SQL_PARAMETER, size);
        return ret;
    }


    public static Map<String, Object> buildStEtMap(String st, String et) {
        Map<String, Object> ret = new HashMap<>();
        buildStEtMap(ret, st, et);
        return ret;
    }

    public static void buildStEtMap(Map<String, Object> ret, String st, String et) {
        if (StringUtils.isNotBlank(st)) {
            ret.put(ST_SQL_PARAMETER, st);
        }
        if (StringUtils.isNotBlank(et)) {
            ret.put(ET_SQL_PARAMETER, et);
        }
    }


    public static String formatOrder(String order) {
        return EnumSqlOrderType.enumValueOf(order).getSql();
    }


    public static String formatLeftLike(String value) {
        return value == null ? "%%" : "%" + value;
    }

    public static String formatFullLike(String value) {
        return value == null ? "%%" : "%" + value + "%";
    }

    public static <M> Map<String, Object> buildStartIdSizeMap(M startId, int size) {
        Map<String, Object> ret = new HashMap<>();
        ret.put(START_ID_SQL_PARAMETER, startId);
        ret.put(SIZE_SQL_PARAMETER, size);
        return ret;
    }

    public static <M> Map<String, Object> buildShardingStartIdSizeMap(String tableSuffix, M startId, int size) {
        Map<String, Object> ret = buildStartIdSizeMap(startId, size);
        initTableSuffix(ret, tableSuffix);
        return ret;
    }

    public static <M> Map<String, Object> buildEndIdSizeMap(M endId, int size) {
        Map<String, Object> ret = new HashMap<>();
        ret.put(END_ID_SQL_PARAMETER, endId);
        ret.put(SIZE_SQL_PARAMETER, size);
        return ret;
    }

    public static void initStartEndTime(Map<String, Object> para, String st, String et) {
        if (!StringUtils.isEmpty(st)) {
            para.put(ST_SQL_PARAMETER, st);
        }
        if (!StringUtils.isEmpty(et)) {
            para.put(ET_SQL_PARAMETER, et);
        }
    }

    public static Object buildIdShardingMap(int id, String tableSuffix) {
        Map<String, Object> para = new HashMap<>();
        para.put(ID_SQL_PARAMETER, id);
        initTableSuffix(para, tableSuffix);
        return para;
    }



    public static Map<String, Object> introspectForUpdate(Object g) {
        return introspect(g, false, null);
    }

    public static <G> Map<String, Object> introspectForInsertOrUpdate(G g) {
        return introspect(g, false, null);
    }

    public static void buildOrder(Map<String, Object> para, String orderColumn, boolean orderByDesc) {
        if (!CollectionUtils.isEmpty(para)) {
            para.put(ORDER_COLUMN_SQL_PARAMETER, orderColumn);
            para.put(ORDER_COLUMN_SORT_SQL_PARAMETER,
                    orderByDesc ? EnumSqlOrderType.DESC.getSql() : EnumSqlOrderType.ASC.getSql());
        }
    }

    public static <M> Object buildPolymorphismTableMap(M id, String polymorphismTable) {
        Map<String, Object> para = new HashMap<>();
        para.put(ID_SQL_PARAMETER, id);
        initTableNameToMap(para, polymorphismTable);
        return para;
    }

    public static <G> Map<String, Object> buildListPara(G g) {
        if (g == null) {
            return new HashMap<>();
        }
        return introspect(g, true, buildNoIgnoreZeroFieldList(g));
    }

    public static <G> Map<String, Object> buildCountPara(G g) {
        return buildListPara(g);
    }

    public static <G> List<String> buildNoIgnoreZeroFieldList(G object) {
        List<String> zeroFieldList = new ArrayList<>();
        Class<?> clazz = object.getClass();
        Class<?> superClazz = clazz.getSuperclass();
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(clazz);
        } catch (IntrospectionException e) {
            logger.error("Get bean info failed. This class:" + clazz + ", Error msg:" + e.getMessage());
            return zeroFieldList;
        }
        BeanInfo superBeanInfo = null;
        try {
            superBeanInfo = Introspector.getBeanInfo(superClazz);
        } catch (IntrospectionException e) {
            logger.error("Get bean info failed. This class:" + superClazz + ", Error msg:" + e.getMessage());

        }
        PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
        PropertyDescriptor[] superProps = (superBeanInfo == null) ? null : superBeanInfo.getPropertyDescriptors();
        if (props == null && superProps == null) {
            return zeroFieldList;
        }
        List<PropertyDescriptor> propList = props == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(props));
        List<PropertyDescriptor> superPropList = superProps == null ? new ArrayList<>() : Arrays.asList(superProps);
        if (!CollectionUtils.isEmpty(superPropList)) {
            propList.removeAll(superPropList);
        }
        initZeroFieldList(superPropList, superClazz, zeroFieldList);
        initZeroFieldList(propList, superClazz, zeroFieldList);
        return zeroFieldList;

    }

    private static void initZeroFieldList(List<PropertyDescriptor> propList, Class<?> clazz, List<String> zeroFieldList) {
        for (PropertyDescriptor propertyDescriptor : propList) {
            String name = propertyDescriptor.getName();
            try {
                Field f = clazz.getDeclaredField(name);
                f.setAccessible(true);
                if (f.isAnnotationPresent(DbFieldConstraint.ZERO_ENABLE.class)) {
                    zeroFieldList.add(f.getName());
                }
            } catch (Exception e) {
                if (e instanceof NoSuchFieldException) {
                    continue;
                }
                logger.error("Init zero field failed. Error msg:" + e.getMessage());
            }
        }
    }
}
