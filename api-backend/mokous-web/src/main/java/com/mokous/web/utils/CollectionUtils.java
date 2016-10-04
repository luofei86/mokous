// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.web.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import com.mokous.web.exception.ServiceException;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月4日
 * 
 */
public class CollectionUtils {
    /**
     * retain the keyIdMap keys not in keyValueMap.keys()
     * 
     * @param keyValueMap
     * @param keyIdMap
     * @return
     */
    public static <G> List<G> retainKeysNotIn(Map<String, G> keyIdMap, Map<String, ?> keyValueMap) {
        List<G> result = new ArrayList<G>();
        for (Entry<String, G> keyIdMapEntry : keyIdMap.entrySet()) {
            if (keyValueMap.get(keyIdMapEntry.getKey()) == null) {
                result.add(keyIdMapEntry.getValue());
            }
        }
        return result;
    }

    public static void mapAddList(String key, Map<String, List<String>> maps, List<String> adds)
            throws IllegalAccessException {
        if (StringUtils.isEmpty(key) || maps == null || adds == null) {
            throw new IllegalAccessException("参数key,maps,adds不能为空");
        }
        List<String> preRemoveVideoIds = maps.put(key, adds);
        if (!CollectionUtils.emptyOrNull(preRemoveVideoIds)) {
            adds.addAll(preRemoveVideoIds);
        }
    }

    public static void mapAddList(String key, Map<String, List<String>> maps, String add) throws IllegalAccessException {
        if (StringUtils.isEmpty(key) || maps == null || add == null) {
            throw new IllegalAccessException("参数key,maps,adds不能为空");
        }
        List<String> members = new ArrayList<String>();
        members.add(add);
        mapAddList(key, maps, members);
    }

    public static <G, T> void mapAddObjectList(G key, Map<G, List<T>> keyMembers, List<T> innerList)
            throws IllegalAccessException {
        if (key == null || keyMembers == null || innerList == null) {
            throw new IllegalAccessException("参数key,maps,adds不能为空");
        }
        List<T> preRemoveVideoIds = keyMembers.put(key, innerList);
        if (!CollectionUtils.emptyOrNull(preRemoveVideoIds)) {
            innerList.addAll(preRemoveVideoIds);
        }
    }

    /**
     * key keyMembers and member should not be null
     * 
     * @param key
     * @param keyMembers
     * @param member
     * @throws IllegalAccessException
     */
    public static <G, T> void mapAddObject(G key, Map<G, List<T>> keyMembers, T member) throws IllegalAccessException {
        if (key == null || keyMembers == null || member == null) {
            throw new IllegalAccessException("参数key,maps,member不能为空");
        }
        List<T> innerList = new ArrayList<T>();
        innerList.add(member);
        CollectionUtils.mapAddObjectList(key, keyMembers, innerList);
    }

    public static void mapAddList(String key, Map<String, Map<String, Double>> maps, Map<String, Double> adds)
            throws IllegalAccessException {
        if (StringUtils.isEmpty(key) || maps == null || adds == null) {
            throw new IllegalAccessException("参数key,maps,adds不能为null");
        }
        Map<String, Double> preRemoveVideoIds = maps.put(key, adds);
        if (!CollectionUtils.emptyOrNull(preRemoveVideoIds)) {
            adds.putAll(preRemoveVideoIds);
        }
    }

    public static void mapAddList(String key, Map<String, Map<String, Double>> maps, String member, double score)
            throws IllegalAccessException {
        if (StringUtils.isEmpty(key) || maps == null || StringUtils.isEmpty(member)) {
            throw new IllegalAccessException("参数key,maps,adds不能为null");
        }
        Map<String, Double> memberScoreMap = new HashMap<String, Double>();
        memberScoreMap.put(member, score);
        mapAddList(key, maps, memberScoreMap);
    }

    public static boolean emptyOrNull(byte[] values) {
        return values == null || values.length == 0;
    }

    public static boolean emptyOrNull(Object[] values) {
        return values == null || values.length == 0;
    }

    public static boolean emptyOrNull(Map<?, ?> values) {
        return values == null || values.isEmpty();
    }

    public static boolean emptyOrNull(Collection<?> values) {
        return values == null || values.isEmpty();
    }

    public static boolean notEmptyAndNull(Collection<?> values) {
        return values != null && !values.isEmpty();
    }

    public static boolean notEmptyAndNull(Map<?, ?> values) {
        return values != null && !values.isEmpty();
    }

    public static boolean notEmptyAndNull(String[] values) {
        return values != null && values.length != 0;
    }

    public static <T> boolean notEmptyAndNull(T[] values) {
        return values != null && values.length != 0;
    }

    public static String[] listToArrays(Collection<String> values) {
        String[] ret = new String[values.size()];
        values.toArray(ret);
        return ret;
    }

    public static List<Integer> setStringToListInt(Collection<String> ids) {
        return stringCollectionsToIntegerList(ids, true);
    }

    public static List<Integer> stringCollectionsToIntegerList(Collection<String> ids, boolean ignoreNil) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<Integer> ret = new ArrayList<Integer>(ids.size());
        for (String id : ids) {
            if (StringUtils.isEmpty(id)) {
                if (!ignoreNil) {
                    ret.add(null);
                }
                continue;
            }
            ret.add(Integer.valueOf(id));
        }
        return ret;
    }

    public static List<Integer> stringArrayToIntegerList(String[] ids, boolean ignoreNil) {
        if (ids == null || ids.length == 0) {
            return Collections.emptyList();
        }
        List<Integer> ret = new ArrayList<Integer>(ids.length);
        for (String id : ids) {
            if (StringUtils.isEmpty(id)) {
                if (!ignoreNil) {
                    ret.add(null);
                }
                continue;
            }
            ret.add(Integer.valueOf(id));
        }
        return ret;
    }


    static class IdScore implements Comparable<IdScore> {
        private String id;
        private double score;

        public IdScore(String id, double score) {
            super();
            this.id = id;
            this.score = score;
        }

        @Override
        public int compareTo(IdScore o) {
            return Double.compare(this.score, o.score);
        }
    }

    public static class IdValue implements Comparable<IdValue> {
        private int id;
        private int score;

        public IdValue(int id, int score) {
            super();
            this.id = id;
            this.score = score;
        }

        @Override
        public int compareTo(IdValue o) {
            return Integer.compare(this.score, o.score);
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        @Override
        public String toString() {
            return "IdValue [id=" + id + ", score=" + score + "]";
        }
    }

    public static List<String> sort(List<String> ids, List<Double> s) {
        List<IdScore> sortValues = new ArrayList<IdScore>();
        for (int i = 0; i < ids.size(); i++) {
            String id = ids.get(i);
            Double score = s.get(i);
            IdScore idScore = new IdScore(id, score);
            sortValues.add(idScore);
        }
        Collections.sort(sortValues);
        List<String> ret = new ArrayList<String>();
        for (IdScore idScore : sortValues) {
            ret.add(idScore.id);
        }
        return ret;
    }

    public static List<Integer> sortInt(List<Integer> ids, List<Integer> s) {
        List<IdValue> sortValues = new ArrayList<IdValue>();
        for (int i = 0; i < ids.size(); i++) {
            Integer id = ids.get(i);
            Integer score = s.get(i);
            IdValue idScore = new IdValue(id, score);
            sortValues.add(idScore);
        }
        Collections.sort(sortValues);
        List<Integer> ret = new ArrayList<Integer>();
        for (IdValue idScore : sortValues) {
            ret.add(idScore.id);
        }
        return ret;
    }

    public static List<Double> collectionsToDoubleList(Collection<String> ids) {
        if (emptyOrNull(ids)) {
            return Collections.emptyList();
        }
        List<Double> ret = new ArrayList<Double>(ids.size());
        for (String id : ids) {
            if (StringUtils.isEmpty(id)) {
                ret.add(0d);
            } else {
                ret.add(Double.valueOf(id));
            }
        }
        return ret;
    }

    public static List<Integer> collectionsToIntList(Collection<String> ids, boolean ignoreNil) {
        return stringCollectionsToIntegerList(ids, ignoreNil);
    }

    public static <K> String listToString(List<K> values, String splitChar) {
        splitChar = StringUtils.isEmpty(splitChar) ? "," : splitChar;
        String ret = "";
        if (values == null || values.isEmpty()) {
            return ret;
        }
        for (int i = 0; i < values.size(); i++) {
            K value = values.get(i);
            if (value == null) {
                continue;
            }
            if (i == values.size() - 1) {
                ret += value.toString();
            } else {
                ret += value.toString() + splitChar;
            }
        }
        return ret;
    }

    public static <K, V> Map<K, V> map(List<K> keys, List<V> values) {
        return map(keys, values, true);
    }

    public static <K, V> Map<K, V> map(List<K> keys, List<V> values, boolean ignoreNull) {
        Map<K, V> ret = new LinkedHashMap<K, V>();
        for (int i = 0; i < keys.size(); i++) {
            V v = values.get(i);
            if (ignoreNull && v == null) {
                continue;
            }
            ret.put(keys.get(i), values.get(i));
        }
        return ret;
    }

    public static <G> Map<String, G> transferRootNameMap(Map<Integer, Integer> rootIdIdMap, Map<Integer, G> idValueMap,
            G defaultValue) {
        Map<String, G> rootValueMap = new LinkedHashMap<String, G>();
        for (Entry<Integer, Integer> entry : rootIdIdMap.entrySet()) {
            if (idValueMap.containsKey(entry.getValue())) {
                rootValueMap.put(entry.getKey().toString(), idValueMap.get(entry.getValue()));
            } else {
                rootValueMap.put(entry.getKey().toString(), defaultValue);
            }
        }
        return rootValueMap;
    }

    /**
     * 交叉插入keys与values 比如 {1,3,5} 与 {2,4,6}得到[1,2,3,4,5,6]
     * 
     * @param keys
     * @param values
     * @return
     */
    public static String[] combine(List<String> keys, List<String> values) {
        if (emptyOrNull(keys) || emptyOrNull(values) || keys.size() != values.size()) {
            throw new IllegalArgumentException("参数错误");
        }
        String[] ret = new String[keys.size() * 2];
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = values.get(i);
            ret[i * 2] = key;
            ret[i * 2 + 1] = value;
        }
        return ret;
    }

    public static <K, G> Map<K, G> reverseMap(Map<String, K> keyIdMap, Map<String, G> keyValueMap) {
        Map<K, G> ret = new HashMap<K, G>();
        for (Entry<String, G> entry : keyValueMap.entrySet()) {
            K id = keyIdMap.get(entry.getKey());
            ret.put(id, entry.getValue());
        }
        return ret;
    }

    /**
     * combine map to key value key value....
     */
    public static String[] combine(Map<String, String> keyValueMap) {
        if (CollectionUtils.emptyOrNull(keyValueMap)) {
            return new String[] {};
        }
        String[] ret = new String[keyValueMap.size() * 2];
        int i = 0;
        for (Entry<String, String> entry : keyValueMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            ret[i * 2] = key;
            ret[i * 2 + 1] = value;
            i++;
        }
        return ret;
    }

    public static <G> List<G> random(List<G> ids, int randomSize) {
        if (ids.size() <= randomSize) {
            return new ArrayList<G>(ids);
        }
        List<Integer> randomIndex = new ArrayList<Integer>();
        for (int i = 0; i < randomSize; i++) {
            int index = RandomUtils.nextInt(0, ids.size());
            if (!randomIndex.contains(index)) {
                randomIndex.add(index);
            } else {
                i = i - 1;
            }
        }
        List<G> ret = new ArrayList<G>();
        for (Integer index : randomIndex) {
            ret.add(ids.get(index));
        }
        return ret;
    }

    public static List<Integer> notExists(List<Integer> rootIds, List<Integer> findRootIds) {
        List<Integer> ret = new ArrayList<Integer>();
        for (Integer rootId : rootIds) {
            if (findRootIds.contains(rootId)) {
                continue;
            }
            ret.add(rootId);
        }
        return ret;
    }

    /**
     * 如果ignoreNull为true,则找出找出keyIdMap中Key不在keyCacheMap中的Id列表,
     * 
     * 如果ignoreNull为false,则还需再找keyIdMap存在keyCacheMap中但值为null的id列表
     * 
     * @param keyIdMap
     * @param keyCacheMap
     * @return
     */
    public static <M, G> List<M> notExists(Map<String, M> keyIdMap, Map<String, G> keyCacheMap, boolean ignoreNull) {
        List<M> dbIds = new ArrayList<M>();
        for (Entry<String, M> entry : keyIdMap.entrySet()) {
            G g = keyCacheMap.get(entry.getKey());
            if (g != null) {
                continue;
            }
            if (g == null && keyCacheMap.containsKey(entry.getKey()) && ignoreNull) {
                continue;
            }
            dbIds.add(entry.getValue());
        }
        return dbIds;
    }

    /**
     * 按照keyIdMap中value的序返回其对应的?数据，其中keyIdMap.key == keyValueCacheMap.key
     * keyIdMap.value = idValueArticles.key
     * 如果keyIdMap中的key在keyValueCacheMap中的key存在
     * ，则其不在dbArticles,否则其对应的值可能在idValueArticles的key中
     * 
     * 此三个map的key和value都不支持null
     * 
     * @param keyIdMap
     * @param keyValueCacheMap
     * @param idValueArticles
     * @return
     */
    public static <G> List<G> combineWithOrder(LinkedHashMap<String, Integer> keyIdMap,
            Map<String, G> keyValueCacheMap, Map<Integer, G> idValueArticles) {
        List<G> ret = new ArrayList<G>();
        for (Entry<String, Integer> keyIdMapEntry : keyIdMap.entrySet()) {
            String key = keyIdMapEntry.getKey();
            Integer id = keyIdMapEntry.getValue();
            G g = keyValueCacheMap.get(key);
            if (g == null) {
                g = idValueArticles.get(id);
            }
            if (g != null) {
                ret.add(g);
            }
        }
        return ret;
    }

    public static <G> List<G> reverseSort(List<Integer> adIds, List<G> listAdInfos, String propertyName) {
        List<G> result = new ArrayList<G>(listAdInfos.size());
        for (Integer adId : adIds) {
            for (G g : listAdInfos) {
                try {

                    int id = (int) PropertyUtils.getProperty(g, propertyName);
                    if (id == adId.intValue()) {
                        result.add(g);
                        break;
                    }
                } catch (Exception e) {
                    throw ServiceException
                            .getInternalException("Cannot reverse sort by adIds.Errmsg:" + e.getMessage());
                }
            }
        }
        return result;
    }
}
