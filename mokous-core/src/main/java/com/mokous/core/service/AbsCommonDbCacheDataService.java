// Copyright 2015 ios.appchina.com Inc. All Rights Reserved.

package com.mokous.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import redis.clients.jedis.JedisPool;

import com.mokous.core.utils.RedisCacheUtils;
import com.mokous.web.exception.ServiceException;
import com.mokous.web.utils.CollectionUtils;

/**
 * @author luofei@appchina.com (Your Name Here)
 *
 */
public abstract class AbsCommonDbCacheDataService<G extends Serializable> extends AbsCommonDataService<G> {
    protected abstract JedisPool getJedisPool();

    public List<G> getData(List<Integer> gg) {
        Map<String, Integer> keyIdMap = new LinkedHashMap<String, Integer>();
        List<G> ret = new ArrayList<G>();
        for (Integer g : gg) {
            String key = buildDataInfoKey(g);
            if (!StringUtils.isEmpty(key)) {
                keyIdMap.put(key, g);
            }
        }
        Map<String, G> cacheResp = RedisCacheUtils.mgetKeyObjectMap(new ArrayList<String>(keyIdMap.keySet()),
                getJedisPool());
        List<Integer> dbIds = CollectionUtils.notExists(keyIdMap, cacheResp, false);
        if (CollectionUtils.notEmptyAndNull(dbIds)) {
            List<G> dbDatas = getDirectFromDb(dbIds);
            if (CollectionUtils.notEmptyAndNull(dbDatas)) {
                for (G dbData : dbDatas) {
                    boolean resp = toCache(dbData);
                    if (resp) {
                        ret.add(dbData);
                    }
                }
            }
        }
        if (CollectionUtils.notEmptyAndNull(cacheResp.values())) {
            ret.addAll(cacheResp.values());
        }
        return ret;
    }

    public G getData(int id) {
        String key = buildDataInfoKey(id);
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        G ret = null;
        ret = RedisCacheUtils.getObject(key, getJedisPool());
        if (ret != null) {
            return ret;
        } else {
            ret = getDirectFromDb(id);
            boolean resp = toCache(ret);
            if (!resp) {
                ret = null;
            }
        }
        return ret;
    }

    protected boolean toCache(G g) {
        if (g == null) {
            return false;
        }
        return toCache(Arrays.asList(g)).get(0);
    }

    protected List<Boolean> toCache(List<G> gg) {
        if (CollectionUtils.emptyOrNull(gg)) {
            return Collections.emptyList();
        }
        boolean resp = false;
        List<Boolean> legals = new ArrayList<Boolean>();
        List<Boolean> result = new ArrayList<Boolean>();
        Map<String, G> keyValueMap = new HashMap<String, G>();
        List<String> delKeys = new ArrayList<String>();
        for (G g : gg) {
            if (g == null) {
                result.add(false);
                legals.add(false);
            }
            String key = buildDataInfoKey(getId(g));
            boolean legal = legalData(g);
            if (!StringUtils.isEmpty(key)) {
                if (legal) {
                    g = formatBeforeSetToCache(g);
                    keyValueMap.put(key, g);
                    resp = true;
                } else {
                    delKeys.add(key);
                    resp = false;
                }
            }
            legals.add(legal);
            result.add(resp);
        }

        RedisCacheUtils.msetObject(keyValueMap, getJedisPool());
        RedisCacheUtils.del(delKeys, getJedisPool());
        additionalPutToCacheDB(gg, legals);
        return result;
    }

    protected G formatBeforeSetToCache(G g) {
        return g;
    }

    protected void additionalPutToCacheDB(List<G> gg, List<Boolean> legals) {
    }

    protected abstract boolean legalData(G g);

    protected int getId(G g) {
        try {
            return Integer.parseInt(BeanUtils.getProperty(g, "id"));
        } catch (Exception e) {
            throw ServiceException.getInternalException("Cannot get id from value:" + g);
        }
    }


    protected abstract String buildDataInfoKey(int id);

    @Override
    protected void afterModifyData(List<G> gg) {
        for (G g : gg) {
            toCache(g);
        }
    }
}
