// Copyright 2015 ios.appchina.com Inc. All Rights Reserved.

package com.mokous.core.dao;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.mokous.core.dto.DbField;
import com.mokous.core.utils.SQLUtils;
import com.mokous.web.exception.ServiceException;
import com.mokous.web.utils.CollectionUtils;

/**
 * @author luofei@appchina.com (Your Name Here)
 *
 */
public abstract class AbsCommonDao<G> implements CommonDao<G> {
    @Override
    public List<G> queryList(G g, int start, int size) throws SQLException {
        return DaoHelper.queryList(g, start, size, this);
    }

    @Override
    public List<G> queryListByStartId(G g, int startId, int size) throws SQLException {
        return DaoHelper.queryListByStartId(g, startId, size, this);
    }

    @Override
    public List<G> queryListByEndId(G g, int endId, int size) throws SQLException {
        return DaoHelper.queryListByEndId(g, endId, size, this);
    }

    @Override
    public G queryObject(int id) throws SQLException {
        return DaoHelper.queryObject(id, this);
    }

    @Override
    public List<G> queryList(List<Integer> ids) throws SQLException {
        return DaoHelper.queryList(ids, this);
    }

    @Override
    public void insertOrUpdate(G g) throws SQLException {
        DaoHelper.insertOrUpdate(g, this);
    }

    @Override
    public void insertOrUpdate(List<G> gg) throws SQLException {
        DaoHelper.insertOrUpdate(gg, this);
    }

    @Override
    public boolean insertOrIgnore(G g) throws SQLException {
        return DaoHelper.insertOrIgnore(g, this);
    }

    @Override
    public void updateStatus(G g) throws SQLException {
        DaoHelper.updateStatus(g, this);
    }

    @Override
    public void updateStatus(List<G> gg) throws SQLException {
        DaoHelper.updateStatus(gg, this);
    }

    @Override
    public void update(G g) throws SQLException {
        DaoHelper.update(g, this);
    }

    @Override
    public long count(G g) throws SQLException {
        return DaoHelper.count(g, this);
    }


    @Override
    public List<G> queryList(int startId, int size) throws SQLException {
        return DaoHelper.queryList(startId, size, this);
    }

    public String getName() {
        String clazzParameterizedTypeName = getName(this.getClass());
        if (clazzParameterizedTypeName == null) {
            throw ServiceException.getInternalException("Unknown class name for " + this.getName());
        }
        return clazzParameterizedTypeName;
    }

    private static final String getName(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        Type type = clazz.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            return getName(type);
        } else {
            Class<?> superClass = clazz.getSuperclass();
            return getName(superClass);
        }
    }

    private static String getName(Type type) {
        return ((Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0]).getSimpleName();
    }

    @Override
    public Map<String, Object> buildListPara(G g) {
        if (g == null) {
            return new HashMap<String, Object>();
        }
        List<String> noIgnoreZeroFieldList = buildNoIgnoreZeroFieldList(g);
        return SQLUtils.introspect(g, true, noIgnoreZeroFieldList);
    }

    private List<String> buildNoIgnoreZeroFieldList(G object) {
        List<String> zeroFieldList = new ArrayList<String>();
        Class<?> clazz = object.getClass();
        Class<?> superClazz = clazz.getSuperclass();
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(clazz);
        } catch (IntrospectionException e1) {
            return zeroFieldList;
        }
        BeanInfo superBeanInfo = null;
        try {
            superBeanInfo = Introspector.getBeanInfo(superClazz);
        } catch (IntrospectionException e1) {
        }
        PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
        PropertyDescriptor[] superProps = superBeanInfo == null ? null : superBeanInfo.getPropertyDescriptors();
        if (superProps == null && props == null) {
            return zeroFieldList;
        }
        List<PropertyDescriptor> propList = new ArrayList<PropertyDescriptor>(Arrays.asList(props));
        List<PropertyDescriptor> superPropList = superProps == null ? new ArrayList<PropertyDescriptor>()
                : new ArrayList<PropertyDescriptor>(Arrays.asList(superProps));
        if (CollectionUtils.notEmptyAndNull(superPropList)) {
            propList.removeAll(superPropList);
        }
        initZeroFieldlList(superPropList, superClazz, zeroFieldList);
        initZeroFieldlList(propList, clazz, zeroFieldList);
        return zeroFieldList;
    }

    private static void initZeroFieldlList(List<PropertyDescriptor> propList, Class<?> clazz, List<String> zeroFieldList) {
        for (PropertyDescriptor prop : propList) {
            String name = prop.getName();
            try {
                Field f = clazz.getDeclaredField(name);
                f.setAccessible(true);
                if (f.isAnnotationPresent(DbField.ZERO_ENABLE.class)) {
                    zeroFieldList.add(f.getName());
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    public Map<String, Object> buildCountPara(G g) {
        return buildListPara(g);
    }

    @Override
    public long count(G g, String st, String et) throws SQLException {
        if (StringUtils.isEmpty(st) && StringUtils.isEmpty(et)) {
            return count(g);
        }
        return DaoHelper.count(g, st, et, this);
    }

    @Override
    public List<G> queryList(G g, String st, String et, int start, int size) throws SQLException {
        if (StringUtils.isEmpty(st) && StringUtils.isEmpty(et)) {
            return queryList(g, start, size);
        }
        return DaoHelper.queryList(g, st, et, start, size, this);
    }
}
