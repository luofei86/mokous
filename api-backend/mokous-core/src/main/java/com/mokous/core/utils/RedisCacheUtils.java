// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.core.utils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.Tuple;

import com.mokous.core.service.AsyncService;
import com.mokous.web.exception.ServiceException;
import com.mokous.web.utils.CollectionUtils;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
public class RedisCacheUtils {
    public static final String KEY_SPLITCHAR = ":";
    public static final int CACHE_EXPIRE_SEC = 3600;
    private static final Logger log = Logger.getLogger(RedisCacheUtils.class);

    public static String buildKey(String prefixKey, int id) {
        return buildKey(prefixKey, String.valueOf(id));
    }

    public static String buildKey(String... keys) {
        String returnKey = "";
        for (String key : keys) {
            if (StringUtils.isEmpty(key)) {
                continue;
            }
            if (StringUtils.isEmpty(returnKey)) {
                returnKey += key.toLowerCase();
            } else {
                returnKey = returnKey + ":" + key.toLowerCase();
            }
        }
        return returnKey;
    }

    public static void asyncDel(AsyncService asyncService, final List<String> keys, final JedisPool jedisPool) {
        if (CollectionUtils.emptyOrNull(keys)) {
            return;
        }
        asyncService.async(new Runnable() {
            @Override
            public void run() {
                del(keys, jedisPool);
            }
        });
    }

    public static void asyncDel(AsyncService asyncService, final List<String> keys,
            final ShardedJedisPool shardedJedisPool) {
        if (CollectionUtils.emptyOrNull(keys)) {
            return;
        }
        if (asyncService == null) {
            del(keys, shardedJedisPool);
        } else {
            asyncService.async(new Runnable() {
                @Override
                public void run() {
                    del(keys, shardedJedisPool);
                }
            });
        }
    }

    public static void del(List<String> keys, ShardedJedisPool shardedJedisPool) {
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        try {
            for (String key : keys) {
                shardedJedis.del(key);
            }
        } catch (Exception e) {
            log.error("删除缓存内容失败,Errmsg:" + e.getMessage(), e);
        } finally {
            shardedJedis.close();
        }
    }

    public static void asyncDel(AsyncService asyncService, final String key, final ShardedJedisPool shardedJedisPool) {
        asyncService.async(new Runnable() {
            @Override
            public void run() {
                del(key, shardedJedisPool);
            }
        });
    }

    public static void asyncDel(AsyncService asyncService, final byte[] byteKey, final JedisPool jedisPool) {
        asyncService.async(new Runnable() {
            @Override
            public void run() {
                del(byteKey, jedisPool);
            }
        });
    }

    public static <T extends Serializable> void asyncSetexObject(AsyncService asyncService, final List<String> keys,
            final List<T> values, final ShardedJedisPool shardedJedisPool) {
        asyncService.async(new Runnable() {
            @Override
            public void run() {
                ShardedJedis shardedJedis = shardedJedisPool.getResource();
                try {
                    for (int i = 0; i < keys.size(); i++) {
                        String key = keys.get(i);
                        Serializable value = values.get(i);
                        shardedJedis.setex(key.getBytes(), CACHE_EXPIRE_SEC, ObjectBytesUtils.objectToBytes(value));
                    }
                } catch (Exception e) {
                    log.error("设置缓存内容失败,Errmsg:" + e.getMessage(), e);
                } finally {
                    shardedJedis.close();
                }
            }
        });
    }

    public static <T extends Serializable> void asyncSetexObject(AsyncService asyncService,
            final Map<String, T> keyValues, final ShardedJedisPool shardedJedisPool) {
        asyncService.async(new Runnable() {
            @Override
            public void run() {
                ShardedJedis shardedJedis = shardedJedisPool.getResource();
                try {
                    for (Entry<String, T> entry : keyValues.entrySet()) {
                        String key = entry.getKey();
                        Serializable value = entry.getValue();
                        shardedJedis.setex(key.getBytes(), CACHE_EXPIRE_SEC, ObjectBytesUtils.objectToBytes(value));
                    }
                } catch (Exception e) {
                    log.error("设置缓存内容失败,Errmsg:" + e.getMessage(), e);
                } finally {
                    shardedJedis.close();
                }
            }
        });
    }

    /**
     * @param asyncService
     * @param key
     * @param serializedValue
     * @param expiredSeconds TimeUnit:Second
     * @param shardedJedisPool
     */
    public static void asyncSetexObject(AsyncService asyncService, final String key,
            final Serializable serializedValue, final int expiredSeconds, final ShardedJedisPool shardedJedisPool) {
        asyncService.async(new Runnable() {
            @Override
            public void run() {
                setexObject(key, serializedValue, expiredSeconds, shardedJedisPool);
            }
        });
    }

    public static void asyncSetexObject(AsyncService asyncService, final String key,
            final Serializable serializedValue, final ShardedJedisPool shardedJedisPool) {
        asyncSetexObject(asyncService, key, serializedValue, CACHE_EXPIRE_SEC, shardedJedisPool);
    }

    private static final void checkKey(String key) throws ServiceException {
        if (StringUtils.isEmpty(key)) {
            throw ServiceException.getInternalException("未知的缓存键.");
        }
    }

    public static void del(JedisPool jedisPool, String... keys) {
        if (CollectionUtils.emptyOrNull(keys)) {
            return;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.del(keys);
        } catch (Exception e) {
            log.error("删除缓存内容失败,Errmsg:" + e.getMessage(), e);
        } finally {
            jedis.close();
        }
    }

    public static void del(List<String> keys, JedisPool jedisPool) {
        if (CollectionUtils.emptyOrNull(keys)) {
            return;
        }
        String[] keyArray = keys.toArray(new String[] {});
        del(jedisPool, keyArray);
    }

    public static void del(String key, ShardedJedisPool shardedJedisPool) {
        if (StringUtils.isEmpty(key)) {
            return;
        }
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        try {
            shardedJedis.del(key);
        } catch (Exception e) {
            log.error("删除缓存内容失败,Errmsg:" + e.getMessage(), e);
        } finally {
            shardedJedis.close();
        }
    }

    public static void del(byte[] key, JedisPool jedisPool) {
        if (CollectionUtils.emptyOrNull(key)) {
            return;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.del(key);
        } catch (Exception e) {
            log.error("删除缓存内容失败,Errmsg:" + e.getMessage(), e);
        } finally {
            jedis.close();
        }
    }

    public static void expiresKeys(List<String> keys, int expireSec, JedisPool jedisPool) {
        Jedis jedis = jedisPool.getResource();
        try {
            Pipeline pp = jedis.pipelined();
            for (String key : keys) {
                pp.expire(key, expireSec);
            }
            pp.sync();
        } catch (Exception e) {
        } finally {
            jedis.close();
        }
    }

    public static void expiresKey(String key, int expireSec, JedisPool jedisPool) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.expire(key, expireSec);
        } catch (Exception e) {
        } finally {
            jedis.close();
        }
    }

    public static String get(String key, JedisPool jedisPool) {
        checkKey(key);
        String ret = null;
        Jedis jedis = jedisPool.getResource();
        try {
            ret = jedis.get(key);
        } catch (Exception e) {
            log.error("获取缓存消息失败,Key:" + key + ", Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
        return ret;
    }

    /**
     * 返回的map中的value不为null
     *
     * @param keys
     * @param shardedJedisPool
     * @return
     */
    public static <T extends Serializable> Map<String, T> getObject(Collection<String> keys,
            ShardedJedisPool shardedJedisPool) {
        if (CollectionUtils.emptyOrNull(keys)) {
            return Collections.emptyMap();
        }
        Map<String, byte[]> kvs = new LinkedHashMap<String, byte[]>();
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        try {
            for (String key : keys) {
                byte[] values = shardedJedis.get(key.getBytes());
                shardedJedis.expire(key, CACHE_EXPIRE_SEC);
                if (values != null) {
                    kvs.put(key, values);
                }
            }
        } catch (Exception e) {
            log.error("获取缓存消息失败, Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        } finally {
            shardedJedis.close();
        }
        if (CollectionUtils.emptyOrNull(kvs)) {
            return Collections.emptyMap();
        }
        Map<String, T> ret = new LinkedHashMap<String, T>();
        for (Entry<String, byte[]> kvsEntry : kvs.entrySet()) {
            if (CollectionUtils.emptyOrNull(kvsEntry.getValue())) {
                continue;
            }
            try {
                T t = ObjectBytesUtils.bytesToObject(kvsEntry.getValue());
                if (t != null) {
                    ret.put(kvsEntry.getKey(), t);
                }
            } catch (Exception e) {
                log.error("无法将缓存数据转换成对应的数据模型. Errmsg:" + e.getMessage(), e);
            }
        }
        return ret;
    }

    public static <T extends Serializable> T getObject(String key, int expiredSeconds, ShardedJedisPool shardedJedisPool) {
        if (key == null) {
            return null;
        }
        T ret = null;
        byte[] values = null;
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        try {
            values = shardedJedis.get(key.getBytes());
            shardedJedis.expire(key, expiredSeconds);
        } catch (Exception e) {
            throw ServiceException.getCacheBusyException();
        } finally {
            shardedJedis.close();
        }
        if (values != null) {
            try {
                ret = ObjectBytesUtils.bytesToObject(values);
            } catch (Exception e) {
                log.error("无法将缓存数据转换成对应的数据模型. Errmsg:" + e.getMessage(), e);
            }
        }
        return ret;
    }

    public static <T extends Serializable> T getObject(String key, JedisPool jedisPool) {
        if (key == null) {
            return null;
        }
        byte[] values = null;
        Jedis jedis = jedisPool.getResource();
        try {
            values = jedis.get(key.getBytes());
        } catch (Exception e) {
            log.error("获取缓存消息失败,Key:" + key + ", Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
        try {
            return ObjectBytesUtils.bytesToObject(values);
        } catch (ClassNotFoundException e) {
        } catch (IOException e) {
        }
        return null;
    }

    public static <T extends Serializable> T getObject(String key, ShardedJedisPool shardedJedisPool) {
        return getObject(key, shardedJedisPool, true);
    }

    public static <T extends Serializable> T getObject(String key, ShardedJedisPool shardedJedisPool,
            boolean refreshExpire) {
        if (key == null) {
            return null;
        }
        T ret = null;
        byte[] values = null;
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        try {
            values = shardedJedis.get(key.getBytes());
            if (refreshExpire) {
                shardedJedis.expire(key, CACHE_EXPIRE_SEC);
            }
        } catch (Exception e) {
            log.error("获取缓存消息失败,Key:" + key + ", Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        } finally {
            shardedJedis.close();
        }
        if (values != null) {
            try {
                ret = ObjectBytesUtils.bytesToObject(values);
            } catch (Exception e) {
                log.error("无法将缓存数据转换成对应的数据模型. Errmsg:" + e.getMessage(), e);
            }
        }
        return ret;
    }

    private static byte[][] keyToByteKey(Collection<String> keys) {
        if (CollectionUtils.emptyOrNull(keys)) {
            return null;
        }
        byte[][] keyBytesArray = new byte[keys.size()][];
        int i = 0;
        for (String key : keys) {
            keyBytesArray[i] = key.getBytes();
            i++;
        }
        return keyBytesArray;
    }

    public static List<String> mget(Collection<String> keys, JedisPool jedisPool) {
        if (CollectionUtils.emptyOrNull(keys)) {
            return Collections.emptyList();
        }
        Jedis jedis = jedisPool.getResource();
        List<String> ret = null;
        try {
            ret = jedis.mget(CollectionUtils.listToArrays(keys));
        } catch (Exception e) {
            log.error("获取缓存消息失败, Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
        return ret;

    }

    public static Map<String, String> mgetMap(List<String> keys, JedisPool jedisPool) {
        if (CollectionUtils.emptyOrNull(keys)) {
            return Collections.emptyMap();
        }
        Jedis jedis = jedisPool.getResource();
        List<String> ret = null;
        try {
            ret = jedis.mget(CollectionUtils.listToArrays(keys));
        } catch (Exception e) {
            log.error("获取缓存消息失败, Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
        Map<String, String> returnMap = new LinkedHashMap<String, String>();
        for (int i = 0; i < ret.size(); i++) {
            returnMap.put(keys.get(i), ret.get(i));
        }
        return returnMap;
    }

    public static <T extends Serializable> List<T> mgetObject(Collection<String> keys, JedisPool jedisPool) {
        if (CollectionUtils.emptyOrNull(keys)) {
            return Collections.emptyList();
        }
        byte[][] keyBytesArray = keyToByteKey(keys);
        List<byte[]> valueBytesArray = null;
        Jedis jedis = jedisPool.getResource();
        try {
            valueBytesArray = jedis.mget(keyBytesArray);
        } catch (Exception e) {
            log.error("获取缓存消息失败,keys:" + keys + ", Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
        try {
            return ObjectBytesUtils.bytesToObject(valueBytesArray);
        } catch (ClassNotFoundException e) {
        } catch (IOException e) {
        }
        return Collections.emptyList();
    }

    public static <T extends Serializable> Map<String, T> mgetKeyObjectMap(List<String> keys, JedisPool jedisPool) {
        if (CollectionUtils.emptyOrNull(keys)) {
            return Collections.emptyMap();
        }
        byte[][] keyBytesArray = keyToByteKey(keys);
        List<byte[]> valueBytesArray = null;
        Jedis jedis = jedisPool.getResource();
        try {
            valueBytesArray = jedis.mget(keyBytesArray);
        } catch (Exception e) {
            log.error("获取缓存消息失败,keys:" + keys + ", Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
        Map<String, T> ret = new LinkedHashMap<String, T>();
        for (int i = 0; i < valueBytesArray.size(); i++) {
            byte[] bs = valueBytesArray.get(i);
            try {
                T t = ObjectBytesUtils.bytesToObject(bs);
                if (t != null) {
                    ret.put(keys.get(i), t);
                }
            } catch (ClassNotFoundException e) {
                break;
            } catch (IOException e) {
                break;
            }
        }
        return ret;
    }

    public static void mset(Map<String, String> keyValueMap, JedisPool jedisPool) {
        if (CollectionUtils.emptyOrNull(keyValueMap)) {
            return;
        }
        Jedis jedis = jedisPool.getResource();
        String[] keysvalues = CollectionUtils.combine(keyValueMap);
        try {
            jedis.mset(keysvalues);
        } finally {
            jedis.close();
        }
    }

    public static void mset(List<String> keys, List<String> values, JedisPool jedisPool) {
        if (keys.size() != values.size()) {
            throw ServiceException.getInternalException("错误的缓存数据,键与值数据不一致.");
        }
        if (CollectionUtils.emptyOrNull(values)) {
            return;
        }
        Jedis jedis = jedisPool.getResource();
        String[] keysvalues = CollectionUtils.combine(keys, values);
        try {
            jedis.mset(keysvalues);
        } catch (Exception e) {
            log.error("设置缓存内容失败,Errmsg:" + e.getMessage(), e);
        } finally {
            jedis.close();
        }
    }

    public static void msetnx(List<String> keys, List<String> values, JedisPool jedisPool) {
        if (keys.size() != values.size()) {
            throw ServiceException.getInternalException("错误的缓存数据,键与值数据不一致.");
        }
        if (CollectionUtils.emptyOrNull(values)) {
            return;
        }
        Jedis jedis = jedisPool.getResource();
        String[] keysvalues = CollectionUtils.combine(keys, values);
        try {
            jedis.msetnx(keysvalues);
        } catch (Exception e) {
            log.error("设置缓存内容失败,Errmsg:" + e.getMessage(), e);
        } finally {
            jedis.close();
        }
    }

    public static <T extends Serializable> void msetObject(List<String> keys, List<T> values, JedisPool jedisPool) {
        if (keys.size() != values.size()) {
            throw ServiceException.getInternalException("错误的缓存数据,键与值数据不一致.");
        }
        if (CollectionUtils.emptyOrNull(values)) {
            return;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            Pipeline pp = jedis.pipelined();
            for (int i = 0; i < keys.size(); i++) {
                byte[] key = keys.get(i).getBytes();
                byte[] value = ObjectBytesUtils.objectToBytes(values.get(i));
                pp.set(key, value);
            }
            // pp.multi();
            pp.sync();
        } catch (Exception e) {
            log.error("设置缓存内容失败,Errmsg:" + e.getMessage(), e);
        } finally {
            jedis.close();
        }
    }

    public static <T extends Serializable> void msetObject(Map<String, T> keyValueMap, JedisPool jedisPool) {
        if (CollectionUtils.emptyOrNull(keyValueMap)) {
            return;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            Pipeline pp = jedis.pipelined();
            for (Entry<String, T> entry : keyValueMap.entrySet()) {
                byte[] key = entry.getKey().getBytes();
                byte[] value = ObjectBytesUtils.objectToBytes(entry.getValue());
                pp.set(key, value);
            }
            // pp.multi();
            pp.sync();
        } catch (Exception e) {
            log.error("设置缓存内容失败,Errmsg:" + e.getMessage(), e);
        } finally {
            jedis.close();
        }
    }

    public static boolean sadd(String key, String id, JedisPool jedisPool) {
        return sadd(key, Arrays.asList(id), jedisPool);
    }

    public static void sadd(Map<String, List<String>> keyMembers, JedisPool jedisPool) {
        if (CollectionUtils.emptyOrNull(keyMembers)) {
            return;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            Pipeline pp = jedis.pipelined();
            for (Entry<String, List<String>> entry : keyMembers.entrySet()) {
                for (String t : entry.getValue()) {
                    pp.sadd(entry.getKey(), t);
                }
            }
            // pp.multi();
            pp.sync();
        } catch (Exception e) {
            log.error("设置缓存内容失败,Errmsg:" + e.getMessage(), e);
        } finally {
            jedis.close();
        }
    }

    public static <T extends Serializable> void saddObject(Map<String, List<T>> keyMembers, JedisPool jedisPool) {
        if (CollectionUtils.emptyOrNull(keyMembers)) {
            return;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            Pipeline pp = jedis.pipelined();
            for (Entry<String, List<T>> entry : keyMembers.entrySet()) {
                byte[] key = entry.getKey().getBytes();
                for (T t : entry.getValue()) {
                    byte[] valueBytes = ObjectBytesUtils.objectToBytes(t);
                    pp.sadd(key, valueBytes);
                }
            }
            // pp.multi();
            pp.sync();
        } catch (Exception e) {
            log.error("设置缓存内容失败,Errmsg:" + e.getMessage(), e);
        } finally {
            jedis.close();
        }
    }

    public static boolean sadd(String key, List<String> ids, JedisPool jedisPool) {
        if (CollectionUtils.emptyOrNull(ids)) {
            return false;
        }
        checkKey(key);
        String[] values = ids.toArray(new String[] {});
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.sadd(key, values);
            return true;
        } catch (Exception e) {
            log.error("设置缓存内容失败,Errmsg:" + e.getMessage(), e);
        } finally {
            jedis.close();
        }
        return false;
    }

    public static boolean sadd(String key, String id, ShardedJedisPool shardedJedisPool) {
        checkKey(key);
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        boolean success = false;
        try {
            success = shardedJedis.sadd(key, id) == 1;
            shardedJedis.expire(key, CACHE_EXPIRE_SEC);
        } catch (Exception e) {
            log.error("设置缓存内容失败,Errmsg:" + e.getMessage(), e);
        } finally {
            shardedJedis.close();
        }
        return success;
    }

    public static void setexp(String key, String value, int expireSec, JedisPool jedisPool) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.set(key, value);
            jedis.expire(key, expireSec);
        } catch (Exception e) {
            log.error("设置缓存内容失败,Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
    }

    public static void set(String key, String value, JedisPool jedisPool) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.set(key, value);
        } catch (Exception e) {
            log.error("设置缓存内容失败,Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
    }


    public static <T extends Serializable> void setMultiObjects(Map<String, T> keyValueMap,
            ShardedJedisPool shardedJedisPool) {
        setMultiObjects(keyValueMap, CACHE_EXPIRE_SEC, shardedJedisPool);
    }

    public static <T extends Serializable> void setMultiObjects(Map<String, T> keyValueMap, int cacheExpireSeconds,
            ShardedJedisPool shardedJedisPool) {
        if (CollectionUtils.emptyOrNull(keyValueMap)) {
            return;
        }
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        try {
            for (Entry<String, T> entry : keyValueMap.entrySet()) {
                byte[] key = entry.getKey().getBytes();
                byte[] value = ObjectBytesUtils.objectToBytes(entry.getValue());
                shardedJedis.setex(key, cacheExpireSeconds, value);
            }
        } catch (Exception e) {
            log.error("设置缓存内容失败,Errmsg:" + e.getMessage(), e);
        } finally {
            shardedJedis.close();
        }
    }

    /**
     * @param key
     * @param serializedValue
     * @param expiredSeconds TimeUnit:Second
     * @param shardedJedisPool
     */
    public static void setObject(String key, Serializable serializedValue, ShardedJedisPool shardedJedisPool) {
        setexObject(key, serializedValue, CACHE_EXPIRE_SEC, shardedJedisPool);
    }

    /**
     * @param key
     * @param serializedValue
     * @param expiredSeconds TimeUnit:Second
     * @param shardedJedisPool
     */
    public static void setexObject(String key, Serializable serializedValue, int expiredSeconds,
            ShardedJedisPool shardedJedisPool) {
        byte[] value = null;
        try {
            value = ObjectBytesUtils.objectToBytes(serializedValue);
        } catch (IOException e) {
            log.error("将数据进行字节转换失败。Data:" + serializedValue + ", Errmsg:" + e.getMessage(), e);
            return;
        }
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        try {
            shardedJedis.setex(key.getBytes(), expiredSeconds, value);
        } catch (Exception e) {
            log.error("将数据存入键值失败.Key:" + key + ", Errmsg:" + e.getMessage(), e);
        } finally {
            shardedJedis.close();
        }
    }

    public static boolean setnxThenExpire(final String key, final String value, int expiredSeconds,
            final ShardedJedisPool shardedJedisPool) {
        boolean success = false;
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        try {
            Long setnx = shardedJedis.setnx(key, value);
            // 0: not set, already exist
            if (setnx != null && setnx.intValue() == 1) {
                success = true;
                shardedJedis.expire(key, expiredSeconds);
            }
        } catch (Exception e) {
            log.error("设置缓存内容失败,Errmsg:" + e.getMessage(), e);
        } finally {
            shardedJedis.close();
        }
        return success;
    }

    public static <T extends Serializable> void setObject(String key, T value, JedisPool jedisPool) {
        if (StringUtils.isEmpty(key)) {
            return;
        }
        byte[] valueByte;
        try {
            valueByte = ObjectBytesUtils.objectToBytes(value);
        } catch (IOException e) {
            throw ServiceException.getInternalException("将对象进行序列化失败.Value:" + value);
        }
        setObject(key, valueByte, jedisPool);
    }

    public static <T extends Serializable> void setObject(String key, byte[] value, JedisPool jedisPool) {
        if (StringUtils.isEmpty(key)) {
            return;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.set(key.getBytes(), value);
        } catch (Exception e) {
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
    }

    public static <T extends Serializable> void setObjectWithMultiKey(String[] keys, byte[] value, JedisPool jedisPool) {
        if (CollectionUtils.emptyOrNull(keys)) {
            return;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            for (String key : keys) {
                jedis.set(key.getBytes(), value);
            }
        } catch (Exception e) {
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
    }

    public static <T extends Serializable> void setObjectWithMultiKey(String[] keys, byte[] value,
            ShardedJedisPool shardedJedisPool) {
        if (CollectionUtils.emptyOrNull(keys)) {
            return;
        }
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        try {
            for (String key : keys) {
                shardedJedis.set(key.getBytes(), value);
                shardedJedis.expire(key, CACHE_EXPIRE_SEC);
            }
        } catch (Exception e) {
            throw ServiceException.getCacheBusyException();
        } finally {
            shardedJedis.close();
        }
    }

    public static List<String> srandmembers(String key, int size, JedisPool jedisPool) {
        checkKey(key);
        Jedis jedis = jedisPool.getResource();
        List<String> ret;
        try {
            ret = jedis.srandmember(key, size);
        } catch (Exception e) {
            log.error("获取缓存内容失败,Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
        return ret;
    }

    public static String srandmember(String key, JedisPool jedisPool) {
        checkKey(key);
        Jedis jedis = jedisPool.getResource();
        String ret;
        try {
            ret = jedis.srandmember(key);
        } catch (Exception e) {
            log.error("获取缓存内容失败,Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
        return ret;
    }

    public static Map<String, String> srandmembers(Collection<String> keys, JedisPool jedisPool) {
        if (CollectionUtils.emptyOrNull(keys)) {
            return Collections.emptyMap();
        }
        Jedis jedis = jedisPool.getResource();
        Map<String, Response<String>> cacheResps = new LinkedHashMap<String, Response<String>>();
        Map<String, String> ret = new LinkedHashMap<String, String>();
        try {
            Pipeline pp = jedis.pipelined();
            for (String key : keys) {
                cacheResps.put(key, pp.srandmember(key));
            }
            pp.sync();
            for (Entry<String, Response<String>> cacheResp : cacheResps.entrySet()) {
                String data = cacheResp.getValue().get();
                if (StringUtils.isEmpty(data)) {
                    continue;
                }
                ret.put(cacheResp.getKey(), data);
            }
        } catch (Exception e) {
            log.error("获取缓存内容失败,Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
        return ret;
    }

    public static List<Integer> smemberIds(String key, JedisPool jedisPool) {
        Set<String> values = smembers(key, jedisPool);
        return CollectionUtils.collectionsToIntList(values, true);
    }

    public static List<Integer> srandmemberIds(String key, int size, JedisPool jedisPool) {
        return CollectionUtils.collectionsToIntList(srandmembers(key, size, jedisPool), true);
    }

    public static List<String> smembersList(String key, JedisPool jedisPool) {
        return new ArrayList<String>(smembers(key, jedisPool));
    }

    public static Set<String> smembers(String key, JedisPool jedisPool) {
        checkKey(key);
        Jedis jedis = jedisPool.getResource();
        Set<String> ret;
        try {
            ret = jedis.smembers(key);
        } catch (Exception e) {
            log.error("获取缓存内容失败,Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
        return ret;
    }

    public static Map<String, Set<String>> smembers(Collection<String> keys, JedisPool jedisPool) {
        Jedis jedis = jedisPool.getResource();
        Map<String, Response<Set<String>>> cacheResps = new LinkedHashMap<String, Response<Set<String>>>();
        Map<String, Set<String>> ret = new LinkedHashMap<String, Set<String>>();
        try {
            Pipeline pp = jedis.pipelined();
            for (String key : keys) {
                if (StringUtils.isEmpty(key)) {
                    continue;
                }
                Response<Set<String>> futures = pp.smembers(key);
                cacheResps.put(key, futures);
            }
            // pp.multi();
            pp.sync();
            for (Entry<String, Response<Set<String>>> cacheResp : cacheResps.entrySet()) {
                Set<String> values = cacheResp.getValue().get();
                if (CollectionUtils.notEmptyAndNull(values)) {
                    ret.put(cacheResp.getKey(), values);
                }
            }
        } catch (Exception e) {
            log.error("获取缓存内容失败,Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
        return ret;
    }

    public static Map<String, List<String>> smembersList(Collection<String> keys, JedisPool jedisPool) {
        Map<String, Response<Set<String>>> cacheResps = new LinkedHashMap<String, Response<Set<String>>>();
        Map<String, List<String>> ret = new LinkedHashMap<String, List<String>>();
        Jedis jedis = jedisPool.getResource();
        try {
            Pipeline pp = jedis.pipelined();
            for (String key : keys) {
                if (StringUtils.isEmpty(key)) {
                    continue;
                }
                Response<Set<String>> futures = pp.smembers(key);
                cacheResps.put(key, futures);
            }
            // pp.multi();
            pp.sync();
            for (Entry<String, Response<Set<String>>> cacheResp : cacheResps.entrySet()) {
                Set<String> values = cacheResp.getValue().get();
                if (CollectionUtils.notEmptyAndNull(values)) {
                    ret.put(cacheResp.getKey(), new ArrayList<String>(values));
                }
            }
        } catch (Exception e) {
            log.error("获取缓存内容失败,Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
        return ret;
    }

    public static <T extends Serializable> List<T> smembersObjects(String key, JedisPool jedisPool) {
        checkKey(key);
        Jedis jedis = jedisPool.getResource();
        byte[] keyBytes = key.getBytes();
        Set<byte[]> values = null;
        try {
            values = jedis.smembers(keyBytes);
        } catch (Exception e) {
            log.error("获取缓存内容失败,Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
        try {
            return ObjectBytesUtils.bytesToObject(values);
        } catch (ClassNotFoundException e) {
            log.error("转换缓存内容失败,Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        } catch (IOException e) {
            log.error("转换缓存内容失败,Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        }
    }

    public static <T extends Serializable> List<T> smembersObjects(String key, ShardedJedisPool shardedJedisPool) {
        checkKey(key);
        ShardedJedis sharedJedis = shardedJedisPool.getResource();
        byte[] keyBytes = key.getBytes();
        Set<byte[]> values = null;
        try {
            values = sharedJedis.smembers(keyBytes);
        } catch (Exception e) {
            log.error("获取缓存内容失败,Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        } finally {
            sharedJedis.close();
        }
        try {
            return ObjectBytesUtils.bytesToObject(values);
        } catch (ClassNotFoundException e) {
            log.error("转换缓存内容失败,Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        } catch (IOException e) {
            log.error("转换缓存内容失败,Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        }
    }

    public static <T extends Serializable> void saddObjectMembers(String key, T member,
            ShardedJedisPool shardedJedisPool) {
        checkKey(key);
        if (member == null) {
            return;
        }
        ShardedJedis sharedJedis = shardedJedisPool.getResource();
        byte[] keyBytes = key.getBytes();
        try {
            sharedJedis.sadd(keyBytes, ObjectBytesUtils.objectToBytes(member));
        } catch (Exception e) {
            log.error("获取缓存内容失败,Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        } finally {
            sharedJedis.close();
        }
    }

    public static <T extends Serializable> void saddObjectMembers(String key, List<T> members,
            ShardedJedisPool shardedJedisPool) {
        checkKey(key);
        if (CollectionUtils.emptyOrNull(members)) {
            return;
        }
        ShardedJedis sharedJedis = shardedJedisPool.getResource();
        byte[][] valueBytes = new byte[members.size()][];
        for (int i = 0; i < members.size(); i++) {
            T t = members.get(i);
            byte[] values;
            try {
                values = ObjectBytesUtils.objectToBytes(t);
            } catch (IOException e) {
                continue;
            }
            valueBytes[i] = values;
        }
        if (CollectionUtils.emptyOrNull(valueBytes)) {
            return;
        }
        byte[] keyBytes = key.getBytes();
        try {
            sharedJedis.sadd(keyBytes, valueBytes);
        } catch (Exception e) {
            log.error("获取缓存内容失败,Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        } finally {
            sharedJedis.close();
        }
    }

    public static boolean sismember(String key, String member, JedisPool jedisPool) {
        Jedis jedis = jedisPool.getResource();
        boolean ret = false;
        try {
            ret = jedis.sismember(key, member);
        } catch (Exception e) {
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
        return ret;
    }

    public static long scard(String key, JedisPool jedisPool) {
        Jedis jedis = jedisPool.getResource();
        try {
            long ret = jedis.scard(key);
            return ret;
        } catch (Exception e) {
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
    }


    public static <T extends Serializable> void sremObject(String key, T pcSuiteItunesDevice, JedisPool jedisPool) {
        if (pcSuiteItunesDevice == null) {
            return;
        }
        checkKey(key);
        byte[] keyByte = key.getBytes();
        byte[] valueByte;
        try {
            valueByte = ObjectBytesUtils.objectToBytes(pcSuiteItunesDevice);
        } catch (IOException e1) {
            throw ServiceException.getCacheDataException("对数据进行序列化时发生异常.Data:" + pcSuiteItunesDevice + ", Errmsg:"
                    + e1.getMessage());
        }
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.srem(keyByte, valueByte);
        } catch (Exception e) {
            log.error("设置缓存内容失败,Errmsg:" + e.getMessage(), e);
        } finally {
            jedis.close();
        }
    }

    public static <T extends Serializable> void sremObject(Map<String, List<T>> keyMembers, JedisPool jedisPool) {
        if (CollectionUtils.emptyOrNull(keyMembers)) {
            return;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            Pipeline pp = jedis.pipelined();
            for (Entry<String, List<T>> entry : keyMembers.entrySet()) {
                byte[] key = entry.getKey().getBytes();
                for (T t : entry.getValue()) {
                    byte[] valueBytes = ObjectBytesUtils.objectToBytes(t);
                    pp.srem(key, valueBytes);
                }
            }
            pp.sync();
        } catch (Exception e) {
            log.error("设置缓存内容失败,Errmsg:" + e.getMessage(), e);
        } finally {
            jedis.close();
        }
    }

    public static void srem(Map<String, List<String>> keyMembers, JedisPool jedisPool) {
        if (CollectionUtils.emptyOrNull(keyMembers)) {
            return;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            Pipeline pp = jedis.pipelined();
            for (Entry<String, List<String>> entry : keyMembers.entrySet()) {
                for (String t : entry.getValue()) {
                    pp.srem(entry.getKey(), t);
                }
            }
            // pp.multi();
            pp.sync();
        } catch (Exception e) {
            log.error("设置缓存内容失败,Errmsg:" + e.getMessage(), e);
        } finally {
            jedis.close();
        }
    }

    public static void srem(String key, String idString, JedisPool jedisPool) {
        srem(key, Arrays.asList(idString), jedisPool);
    }

    public static void srem(String key, List<String> idString, JedisPool jedisPool) {
        if (CollectionUtils.emptyOrNull(idString)) {
            return;
        }
        String[] values = idString.toArray(new String[] {});
        checkKey(key);
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.srem(key, values);
        } catch (Exception e) {
            log.error("设置缓存内容失败,Errmsg:" + e.getMessage(), e);
        } finally {
            jedis.close();
        }
    }

    public static Double zscore(String key, String member, JedisPool jedisPool) {
        checkKey(key);
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.zscore(key, member);
        } catch (Exception e) {
            log.error("获取缓存内容失败,Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
    }

    public static void zadd(String key, List<String> ids, List<Double> scores, JedisPool jedisPool) {
        if (StringUtils.isEmpty(key)) {
            throw ServiceException.getInternalException("未知的缓存键.");
        }
        if (ids.size() != scores.size()) {
            throw ServiceException.getInternalException("错误的缓存数据,键与值数据不一致.");
        }
        if (CollectionUtils.emptyOrNull(ids)) {
            return;
        }
        Map<String, Double> members = new LinkedHashMap<String, Double>(ids.size(), 1f);
        for (int i = 0; i < ids.size(); i++) {
            String member = ids.get(i);
            double score = scores.get(i);
            members.put(member, score);
        }
        zadd(key, members, jedisPool);
    }

    public static void zadd(String key, Map<String, Double> members, JedisPool jedisPool) {
        checkKey(key);
        if (CollectionUtils.emptyOrNull(members)) {
            return;
        }
        Map<String, Map<String, Double>> keyMembers = new LinkedHashMap<String, Map<String, Double>>();
        keyMembers.put(key, members);
        zadd(keyMembers, jedisPool);
    }

    public static void zadd(Map<String, Map<String, Double>> keyMembers, JedisPool jedisPool) {
        if (CollectionUtils.emptyOrNull(keyMembers)) {
            return;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            Pipeline pp = jedis.pipelined();
            for (Entry<String, Map<String, Double>> entry : keyMembers.entrySet()) {
                pp.zadd(entry.getKey(), entry.getValue());
            }
            // pp.multi();
            pp.sync();
        } catch (Exception e) {
            log.error("设置缓存内容失败,Errmsg:" + e.getMessage(), e);
        } finally {
            jedis.close();
        }
    }

    public static void zadd(String key, String value, double score, JedisPool jedisPool) {
        checkKey(key);
        Map<String, Double> members = new LinkedHashMap<String, Double>();
        members.put(value, score);
        zadd(key, members, jedisPool);
    }


    public static long zcount(String key, double min, double max, JedisPool jedisPool) {
        Jedis jedis = jedisPool.getResource();
        try {
            long ret = jedis.zcount(key, min, max);
            return ret;
        } catch (Exception e) {
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
    }

    public static long zcard(String key, JedisPool jedisPool) {
        Jedis jedis = jedisPool.getResource();
        try {
            long ret = jedis.zcard(key);
            return ret;
        } catch (Exception e) {
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
    }

    public static Long zrank(String key, String member, JedisPool jedisPool) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.zrank(key, member);
        } catch (Exception e) {
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
    }

    public static Long zrevrank(String key, String member, JedisPool jedisPool) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.zrevrank(key, member);
        } catch (Exception e) {
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
    }


    public static Set<String> zrange(String key, int start, int end, JedisPool jedisPool) {
        Jedis jedis = jedisPool.getResource();
        Set<String> ids = null;
        try {
            ids = jedis.zrange(key, start, end);
        } catch (Exception e) {
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
        return ids;
    }

    public static Set<String> zrange(String key, JedisPool jedisPool) {
        return zrange(key, 0, -1, jedisPool);
    }

    public static List<Integer> zrangeIds(String key, int start, int end, JedisPool jedisPool) {
        Set<String> ids = zrange(key, start, end, jedisPool);
        try {
            return CollectionUtils.setStringToListInt(ids);
        } catch (Exception e) {
            throw ServiceException.getCacheDataException("缓存服务器数据错误，请稍后再试.");
        }
    }

    public static List<Integer> zrangeIds(String key, JedisPool jedisPool) {
        return zrangeIds(key, 0, -1, jedisPool);
    }

    public static void zrem(JedisPool jedisPool, String key, List<String> members) {
        checkKey(key);
        if (CollectionUtils.emptyOrNull(members)) {
            return;
        }
        Map<String, List<String>> keyMembers = new LinkedHashMap<String, List<String>>();
        keyMembers.put(key, members);
        zrem(jedisPool, keyMembers);
    }

    public static void zrem(Map<String, List<String>> keyMembers, JedisPool jedisPool) {
        zrem(jedisPool, keyMembers);
    }

    public static void zrem(JedisPool jedisPool, Map<String, List<String>> keyMembers) {
        if (CollectionUtils.emptyOrNull(keyMembers)) {
            return;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            for (Entry<String, List<String>> entry : keyMembers.entrySet()) {
                String[] memberArray = entry.getValue().toArray(new String[] {});
                if (CollectionUtils.emptyOrNull(memberArray)) {
                    continue;
                }
                jedis.zrem(entry.getKey(), memberArray);
            }
        } catch (Exception e) {
            log.error("Handle zrem failed.Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
    }

    public static void zrem(JedisPool jedisPool, String key, String... members) {
        checkKey(key);
        if (CollectionUtils.emptyOrNull(members)) {
            return;
        }
        Map<String, List<String>> keyMembers = new LinkedHashMap<String, List<String>>();
        for (String member : members) {
            try {
                if (StringUtils.isEmpty(member)) {
                    continue;
                }
                CollectionUtils.mapAddList(key, keyMembers, member);
            } catch (IllegalAccessException e) {
            }
        }
        zrem(jedisPool, keyMembers);
    }

    public static void zrem(String key, List<String> members, JedisPool jedisPool) {
        zrem(jedisPool, key, members);
    }

    public static List<Integer> zrevrange(String key, double maxScore, double minScore, boolean includeMax,
            boolean includeMin, int offset, int count, JedisPool jedisPool) {
        Jedis jedis = jedisPool.getResource();
        Set<String> ids = null;
        try {
            if (includeMax && includeMin) {
                return zrevrange(key, maxScore, minScore, offset, count, jedisPool);
            }
            String maxScoreLex = String.valueOf(maxScore);
            if (!includeMax) {
                maxScoreLex = "(" + maxScoreLex;
            }
            String minScoreLex = String.valueOf(minScore);
            if (!includeMin) {
                minScoreLex = "(" + minScore;
            }
            ids = jedis.zrevrangeByScore(key, maxScoreLex, minScoreLex, offset, count);
        } catch (Exception e) {
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
        try {
            return CollectionUtils.setStringToListInt(ids);
        } catch (Exception e) {
            throw ServiceException.getCacheDataException("缓存服务器数据错误，请稍后再试.");
        }
    }

    public static List<Integer> zrevrange(String key, double maxScore, double minScore, int offset, int count,
            JedisPool jedisPool) {
        Jedis jedis = jedisPool.getResource();
        Set<String> ids = null;
        try {
            ids = jedis.zrevrangeByScore(key, maxScore, minScore, offset, count);
        } catch (Exception e) {
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
        try {
            return CollectionUtils.setStringToListInt(ids);
        } catch (Exception e) {
            throw ServiceException.getCacheDataException("缓存服务器数据错误，请稍后再试.");
        }
    }

    public static List<Integer> zrevrange(String key, int start, int end, JedisPool jedisPool) {
        Set<String> ids = zrevrangeIdStringSet(key, start, end, jedisPool);
        try {
            return CollectionUtils.setStringToListInt(ids);
        } catch (Exception e) {
            throw ServiceException.getCacheDataException("缓存服务器数据错误，请稍后再试.");
        }
    }

    public static Set<String> zrevrangeIdStringSet(String key, int start, int end, JedisPool jedisPool) {
        Jedis jedis = jedisPool.getResource();
        Set<String> ids = null;
        try {
            ids = jedis.zrevrange(key, start, end);
        } catch (Exception e) {
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
        return ids;
    }

    public static Set<String> zrevrangebyscore(String key, double max, double min, JedisPool jedisPool) {
        Jedis jedis = jedisPool.getResource();
        Set<String> ids = null;
        try {
            ids = jedis.zrevrangeByScore(key, max, min);
        } catch (Exception e) {
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
        return ids;
    }

    public static Set<String> zrangebyscore(String key, double min, double max, JedisPool jedisPool) {
        Jedis jedis = jedisPool.getResource();
        Set<String> ids = null;
        try {
            ids = jedis.zrangeByScore(key, min, max);
        } catch (Exception e) {
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
        return ids;
    }

    public static boolean zismember(String key, String member, JedisPool jedisPool) {
        Jedis jedis = jedisPool.getResource();
        Long zrank = null;
        try {
            zrank = jedis.zrank(key, member);
        } catch (Exception e) {
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
        return zrank != null;
    }

    public static <G extends Serializable> List<G> lrangeall(String key, ShardedJedisPool shardedJedisPool) {
        if (StringUtils.isEmpty(key)) {
            return Collections.emptyList();
        }
        byte[] keyBytes = key.getBytes();
        List<byte[]> valueBytesArray = null;
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        try {
            valueBytesArray = shardedJedis.lrange(keyBytes, 0, -1);
            shardedJedis.expire(keyBytes, CACHE_EXPIRE_SEC);
        } catch (Exception e) {
            log.error("获取缓存消息失败,Key:" + key + ", Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        } finally {
            shardedJedis.close();
        }
        try {
            return ObjectBytesUtils.bytesToObject(valueBytesArray);
        } catch (ClassNotFoundException e) {
        } catch (IOException e) {
        }
        return Collections.emptyList();
    }

    public static <G extends Serializable> Map<String, List<G>> lrangeall(Collection<String> keys,
            ShardedJedisPool shardedJedisPool) {
        if (CollectionUtils.emptyOrNull(keys)) {
            return Collections.emptyMap();
        }
        List<byte[]> valueBytesArray = null;
        Map<String, List<byte[]>> keyValues = new LinkedHashMap<String, List<byte[]>>();
        Map<String, List<G>> ret = new LinkedHashMap<String, List<G>>();
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        try {
            for (String key : keys) {
                byte[] keyBytes = key.getBytes();
                valueBytesArray = shardedJedis.lrange(keyBytes, 0, -1);
                shardedJedis.expire(keyBytes, CACHE_EXPIRE_SEC);
                keyValues.put(key, valueBytesArray);
            }
        } catch (Exception e) {
            log.error("获取缓存消息失败,Key:" + keys + ", Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        } finally {
            shardedJedis.close();
        }
        for (Entry<String, List<byte[]>> entry : keyValues.entrySet()) {
            if (CollectionUtils.emptyOrNull(entry.getValue())) {
                continue;
            }
            List<G> values = new ArrayList<G>();
            for (byte[] bs : entry.getValue()) {
                try {
                    @SuppressWarnings("unchecked")
                    G g = (G) ObjectBytesUtils.bytesToObject(bs);
                    values.add(g);
                } catch (ClassNotFoundException e) {
                } catch (IOException e) {
                }
            }
            ret.put(entry.getKey(), values);
        }
        return ret;
    }

    public static <T extends Serializable> void asyncLadd(AsyncService asyncService, final String key,
            final List<T> values, final ShardedJedisPool shardedJedisPool) {
        if (CollectionUtils.emptyOrNull(values)) {
            return;
        }
        asyncService.async(new Runnable() {
            @Override
            public void run() {
                ShardedJedis shardedJedis = shardedJedisPool.getResource();
                byte[] keyBytes = key.getBytes();
                byte[][] valuesBytes = new byte[values.size()][];
                for (int i = 0; i < values.size(); i++) {
                    T applicationItunesImgRes = values.get(i);
                    try {
                        valuesBytes[i] = ObjectBytesUtils.objectToBytes(applicationItunesImgRes);
                    } catch (IOException e) {
                    }
                }
                try {
                    shardedJedis.lpush(keyBytes, valuesBytes);
                    shardedJedis.expire(keyBytes, CACHE_EXPIRE_SEC);
                } catch (Exception e) {
                    log.error("删除缓存内容失败,Key:" + key + ",Errmsg:" + e.getMessage(), e);
                } finally {
                    shardedJedis.close();
                }
            }
        });

    }

    /**
     * 不要再使用队列来进行对象存储
     *
     * @param asyncService
     * @param dbValueToCacheMap
     * @param shardedJedisPool
     */
    public static <G extends Serializable> void asyncLadd(AsyncService asyncService,
            final Map<String, List<G>> dbValueToCacheMap, final ShardedJedisPool shardedJedisPool) {
        asyncService.async(new Runnable() {
            @Override
            public void run() {
                ShardedJedis shardedJedis = shardedJedisPool.getResource();
                try {
                    for (Entry<String, List<G>> entry : dbValueToCacheMap.entrySet()) {
                        if (StringUtils.isEmpty(entry.getKey()) || CollectionUtils.emptyOrNull(entry.getValue())) {
                            continue;
                        }
                        byte[] keyBytes = entry.getKey().getBytes();
                        byte[][] valuesBytes = new byte[entry.getValue().size()][];
                        for (int i = 0; i < entry.getValue().size(); i++) {
                            G applicationItunesImgRes = entry.getValue().get(i);
                            try {
                                valuesBytes[i] = ObjectBytesUtils.objectToBytes(applicationItunesImgRes);
                            } catch (IOException e) {
                            }
                        }
                        shardedJedis.lpush(keyBytes, valuesBytes);
                        shardedJedis.expire(keyBytes, CACHE_EXPIRE_SEC);
                    }
                } catch (Exception e) {
                    log.error("删除缓存内容失败,Errmsg:" + e.getMessage(), e);
                } finally {
                    shardedJedis.close();
                }
            }
        });

    }

    public static Set<String> listKeys(String pattern, JedisPool jedisPool) {
        if (StringUtils.isBlank(pattern)) {
            return Collections.emptySet();
        }
        Jedis jedis = jedisPool.getResource();
        try {
            Set<String> keys = jedis.keys(pattern);
            return keys;
        } catch (Exception e) {
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
    }

    /**
     *
     * @param key
     * @param jedisPool
     * @param members
     * @param indexes: 从小到大已排序的
     */
    public static void zsetPutRevPosition(String key, JedisPool jedisPool, List<String> members, List<Integer> indexes) {
        Jedis jedis = jedisPool.getResource();
        try {
            if (members.size() != indexes.size()) {
                throw ServiceException.getParameterException("插入缓存列表非法");
            }
            Pipeline pp = jedis.pipelined();
            for (int i = 0; i < members.size(); i++) {
                String member = members.get(i);
                int index = indexes.get(i);
                if (index < 0) {
                    throw ServiceException.getParameterException("插入缓存位置非法");
                }
                zsetPutCachePosition(pp, key, member, index);
            }
            pp.sync();
        } catch (Exception e) {
            log.error("插入缓存位置出错,Errmsg:" + e.getMessage(), e);
        } finally {
            jedis.close();
        }
    }

    /**
     *
     * @param key
     * @param jedisPool
     * @param member
     * @param index: 0 -based indexes, >= 0
     */
    public static void zsetPutRevPosition(String key, JedisPool jedisPool, String member, int index) {
        Jedis jedis = jedisPool.getResource();
        try {
            if (index < 0) {
                throw ServiceException.getParameterException("插入缓存位置非法");
            }
            Pipeline pp = jedis.pipelined();
            zsetPutCachePosition(pp, key, member, index);
            pp.sync();
        } catch (Exception e) {
            log.error("插入缓存位置出错,Errmsg:" + e.getMessage(), e);
        } finally {
            jedis.close();
        }
    }

    private static void zsetPutCachePosition(Pipeline pp, String key, String member, int index) {
        pp.zrem(key, member);
        int start = index - 1 < 0 ? 0 : index - 1;
        int end = index;
        Response<Set<Tuple>> setResponse = pp.zrevrangeWithScores(key, start, end);
        pp.sync();
        Set<Tuple> tuples = setResponse.get();
        double score = 0;
        int size = tuples.size();
        if (size == 1) {
            Tuple t = (Tuple) tuples.toArray()[0];
            if (index == 0) {
                score = t.getScore() + 1;
            } else {
                score = t.getScore() - 1;
            }
        } else if (size >= 2) {
            for (Tuple tuple : tuples) {
                score += tuple.getScore();
            }
            score /= tuples.size();
        }
        pp.zadd(key, score, member);
    }

    public static void zrembyrank(JedisPool jedisPool, String key, long start, long end) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.zremrangeByRank(key, start, end);
        } catch (Exception e) {
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
    }

    public static void zrevremtail(JedisPool jedisPool, String key, long start) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.zremrangeByRank(key, 0, -start - 1);
        } catch (Exception e) {
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
    }

    public static long incr(String key, JedisPool jedisPool) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.incr(key);
        } catch (Exception e) {
            log.error("Generate id failed.Key:" + key + ", Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
    }

    public static String spop(String key, JedisPool jedisDbPool) {
        Jedis jedis = jedisDbPool.getResource();
        try {
            return jedis.spop(key);
        } catch (Exception e) {
            log.error("Spop value failed.Key:" + key + ", Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
    }

    /**
     * 如果键值不存在则返回-1
     * 
     * @param key
     * @param jedisPool
     * @return
     */
    public static Integer getId(String key, JedisPool jedisPool) {
        String value = get(key, jedisPool);
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            throw ServiceException.getCacheDataException("数据类型数据，无法将字符串类型转换为数据类型.Key:" + key + ", Value:" + value);
        }
    }

    public static boolean exists(String key, JedisPool jedisPool) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.exists(key);
        } catch (Exception e) {
            log.error("Exists value failed.Key:" + key + ", Errmsg:" + e.getMessage(), e);
            throw ServiceException.getCacheBusyException();
        } finally {
            jedis.close();
        }
    }

}
