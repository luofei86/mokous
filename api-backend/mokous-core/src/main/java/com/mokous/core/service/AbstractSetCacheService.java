// Copyright 2015 ios.appchina.com Inc. All Rights Reserved.

package com.mokous.core.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import redis.clients.jedis.JedisPool;

import com.mokous.core.cache.model.CacheFilter.SizeCacheFilter;
import com.mokous.core.dto.StatusSerializable;
import com.mokous.core.utils.RedisCacheUtils;
import com.mokous.web.model.ListWrapResp;
import com.mokous.web.utils.CollectionUtils;

/**
 * 
 * @author luofei@appchina.com (Your Name Here)
 *
 */
public abstract class AbstractSetCacheService<T extends StatusSerializable> extends AbsCommonCacheDataLoadService<T> {

    public ListWrapResp<T> listInfo(SizeCacheFilter filter) {
        long total = count(filter);
        List<Integer> ids = getIds(filter);
        if (ids.isEmpty()) {
            return new ListWrapResp<T>(total, new ArrayList<T>(0), false, 0);
        }
        List<T> values = getData(ids);
        return formatRespValues(values, total, 0, filter.getSize());
    }

    public ListWrapResp<Integer> listId(SizeCacheFilter filter) {
        long total = count(filter);
        List<Integer> ids = getIds(filter);
        boolean more = filter.getSize() > total;
        int next = 0;
        return new ListWrapResp<Integer>(total, ids, more, next);
    }

    /**
     * 所有实现此方法的类都需要保证返回值与values中对应值的顺序一致
     * 
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    protected <N> List<N> formatCacheValues(List<T> values) {
        return (List<N>) values;
    }

    public long count(SizeCacheFilter filter) {
        String key = buildSetKey(filter);
        if (StringUtils.isEmpty(key)) {
            return 0l;
        }
        return RedisCacheUtils.scard(key, getJedisPool());
    }

    protected abstract String buildSetKey(SizeCacheFilter filter);

    protected List<String> buildSetKeys(SizeCacheFilter filter) {
        List<String> keys = new ArrayList<String>();
        keys.add(buildSetKey(filter));
        return keys;
    }

    protected List<Integer> getIds(SizeCacheFilter filter) {
        String key = buildSetKey(filter);
        if (StringUtils.isEmpty(key)) {
            return Collections.emptyList();
        }
        if (filter.getSize() <= 0) {
            return RedisCacheUtils.smemberIds(key, getJedisPool());
        }
        return RedisCacheUtils.srandmemberIds(key, filter.getSize(), getJedisPool());
    }

    protected ListWrapResp<T> formatRespValues(List<T> values, long total, int start, int size) {
        return new ListWrapResp<T>(total, (List<T>) values, total > start + size, start + values.size());
    }

    protected abstract JedisPool getJedisPool();

    @Override
    protected void putToCacheDb(List<T> values, long versionCode) {
        Map<String, List<String>> addMembers = new HashMap<String, List<String>>();
        Map<String, List<String>> removeMembers = new HashMap<String, List<String>>();
        List<String> removeInfoKeys = new ArrayList<String>();
        for (T value : values) {
            boolean illegalValue = illegalValue(value);
            String infoKey = buildDataInfoKey(getIdFromG(value));
            if (illegalValue && !StringUtils.isEmpty(infoKey)) {
                removeInfoKeys.add(infoKey);
            }
            SizeCacheFilter filter = buildCacheFilter(value);
            List<String> idzSetKeys = buildSetKeys(filter);
            if (CollectionUtils.emptyOrNull(idzSetKeys)) {
                continue;
            }
            Map<String, List<String>> usingKeyMemberMap = illegalValue ? removeMembers : addMembers;
            List<String> members = buildSetMembers(value);
            for (int i = 0; i < idzSetKeys.size(); i++) {
                String idzSetKey = idzSetKeys.get(i);
                if (StringUtils.isEmpty(idzSetKey)) {
                    continue;
                }
                String member = members.get(i);
                if (StringUtils.isEmpty(member)) {
                    continue;
                }
                try {
                    CollectionUtils.mapAddList(idzSetKey, usingKeyMemberMap, member);
                } catch (IllegalAccessException e) {
                }
            }

        }
        RedisCacheUtils.sadd(addMembers, getJedisPool());
        RedisCacheUtils.srem(removeMembers, getJedisPool());

        RedisCacheUtils.asyncDel(null, removeInfoKeys, getSharedJedisPool());

        additionalPutToCacheDB(values, versionCode);
    }

    /**
     * 如果实现有更多需要在缓存重载中进行操作，可以实现此方法
     * 
     * @param values
     * @param versionCode
     */
    protected void additionalPutToCacheDB(List<T> values, long versionCode) {
    }

    protected boolean illegalValue(T value) {
        return value.isDel();
    }

    protected abstract SizeCacheFilter buildCacheFilter(T value);

    protected String buildSetMember(T t) {
        return String.valueOf(getIdFromG(t));
    }

    protected List<String> buildSetMembers(T t) {
        List<String> members = new ArrayList<String>();
        members.add(buildSetMember(t));
        return members;
    }

}
