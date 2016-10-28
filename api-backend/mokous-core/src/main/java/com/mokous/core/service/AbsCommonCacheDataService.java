// Copyright 2015 ios.appchina.com Inc. All Rights Reserved.

package com.mokous.core.service;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import redis.clients.jedis.ShardedJedisPool;

import com.mokous.core.dto.DbKey;
import com.mokous.core.dto.DbStatus;
import com.mokous.core.utils.RedisCacheUtils;
import com.mokous.web.exception.ServiceException;
import com.mokous.web.utils.CollectionUtils;

/**
 * @author luofei@appchina.com (Your Name Here)
 *
 */
public abstract class AbsCommonCacheDataService<G extends Serializable> extends AbsCommonDataService<G> {
    private static final Logger log = Logger.getLogger(AbsCommonCacheDataService.class);

    protected abstract ShardedJedisPool getSharedJedisPool();

    protected Logger getLogger() {
        return log;
    }

    /**
     * ONLY RETURN LEGAL DATA
     * 
     * @param id
     * @return
     */
    public G getData(int id) {
        List<G> datas = getData(new ArrayList<Integer>(Arrays.asList(id)));
        G g = datas.size() == 1 ? datas.get(0) : null;
        formatData(g);
        return g;
    }

    protected G formatData(G g) {
        return g;
    }

    public List<G> getData(List<Integer> ids) {
        Map<String, Integer> keyIdMap = new LinkedHashMap<String, Integer>();
        List<G> ret = new ArrayList<G>();
        List<Integer> pureDbFetcIds = new ArrayList<Integer>();
        for (Integer id : ids) {
            String key = buildDataInfoKey(id);
            if (!StringUtils.isEmpty(key)) {
                keyIdMap.put(key, id);
            } else {
                try {
                    getLogger().warn(getClass().getName() + " get data direct from db.");
                } catch (Exception e) {
                }
                pureDbFetcIds.add(id);
            }
        }
        Map<String, G> cacheResp = RedisCacheUtils.getObject(keyIdMap.keySet(), getSharedJedisPool());
        List<Integer> dbIds = CollectionUtils.notExists(keyIdMap, cacheResp, false);
        dbIds.addAll(pureDbFetcIds);
        if (CollectionUtils.notEmptyAndNull(dbIds)) {
            List<G> dbDatas = getDirectFromDb(dbIds);
            if (CollectionUtils.notEmptyAndNull(dbDatas)) {
                Map<String, G> toCacheMap = new HashMap<String, G>();
                for (G dbData : dbDatas) {
                    if (!canPutToCache(dbData)) {
                        continue;
                    }
                    dbData = beforeToCache(dbData);
                    ret.add(dbData);
                    String key = buildDataInfoKey(getIdFromG(dbData));
                    if (!StringUtils.isEmpty(key)) {
                        toCacheMap.put(key, dbData);
                    }
                }
                if (CollectionUtils.notEmptyAndNull(toCacheMap)) {
                    RedisCacheUtils.setMultiObjects(toCacheMap, getSharedJedisPool());
                }
            }
        }
        if (CollectionUtils.notEmptyAndNull(cacheResp.values())) {
            ret.addAll(cacheResp.values());
        }
        return ret;
    }

    protected boolean canPutToCache(G g) {
        try {
            return DbStatus.STATUS_OK == Integer.parseInt(BeanUtils.getProperty(g, "status"));
        } catch (Exception e) {
            throw ServiceException.getInternalException("Cannot get status from value:" + g);
        }
    }

    protected int getIdFromG(G g) {
        try {
            return Integer.parseInt(BeanUtils.getProperty(g, "id"));
        } catch (Exception e) {
            Class<?> clazz = g.getClass();
            Class<?> superClazz = clazz.getSuperclass();
            BeanInfo beanInfo;
            try {
                beanInfo = Introspector.getBeanInfo(clazz);
            } catch (IntrospectionException e1) {
                throw ServiceException.getInternalException("Cannot get id from value:" + g);
            }
            BeanInfo superBeanInfo = null;
            try {
                superBeanInfo = Introspector.getBeanInfo(superClazz);
            } catch (IntrospectionException e1) {
                throw ServiceException.getInternalException("Cannot get id from value:" + g);
            }
            PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
            if (props == null) {
                throw ServiceException.getInternalException("Cannot get id from value:" + g);
            }
            PropertyDescriptor[] superProps = superBeanInfo == null ? null : superBeanInfo.getPropertyDescriptors();
            List<PropertyDescriptor> propList = new ArrayList<PropertyDescriptor>(Arrays.asList(props));
            if (CollectionUtils.notEmptyAndNull(superProps)) {
                propList.removeAll(Arrays.asList(superProps));
            }
            for (PropertyDescriptor prop : propList) {
                String name = prop.getName();
                try {
                    Field f = clazz.getDeclaredField(name);
                    f.setAccessible(true);
                    if (f.isAnnotationPresent(DbKey.PRIMARY_KEY.class)) {
                        return Integer.parseInt(f.get(g).toString());
                    }
                } catch (Exception e1) {
                    throw ServiceException.getInternalException("Cannot get id from value:" + g);
                }
            }

            throw ServiceException.getInternalException("Cannot get id from value:" + g);
        }
    }

    protected abstract String buildDataInfoKey(int id);

    protected G beforeToCache(G g) {
        return g;
    }

    protected List<G> beforeToCache(List<G> gg) {
        for (G g : gg) {
            beforeToCache(g);
        }
        return gg;
    }

    @Override
    protected void afterModifyStatus(List<G> gg) {
        List<String> keys = new ArrayList<String>();
        for (G g : gg) {
            String key = buildDataInfoKey(getIdFromG(g));
            if (!StringUtils.isEmpty(key)) {
                keys.add(key);
            }
        }
        if (CollectionUtils.notEmptyAndNull(keys)) {
            if (getSharedJedisPool() != null) {
                RedisCacheUtils.del(keys, getSharedJedisPool());
            }
        }
    }

}
