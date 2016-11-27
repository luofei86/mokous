// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.web.utils;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月4日
 * 
 */
public class GsonUtils {
    @SuppressWarnings("rawtypes")
    private static final Comparator<? super List<? extends Comparable>> LIST_COMPARABLE_COMPATOR = new Comparator<List<? extends Comparable>>() {
        @SuppressWarnings("unchecked")
        @Override
        public int compare(List<? extends Comparable> left, List<? extends Comparable> right) {
            if (left == null) {
                if (right == null) {
                    return 0;
                } else {
                    return -1;
                }
            } else if (right == null) {
                return 1;
            } else {
                Iterator<? extends Comparable> leftIter = left.iterator();
                Iterator<? extends Comparable> rightIter = right.iterator();
                while (leftIter.hasNext() && rightIter.hasNext()) {
                    Comparable leftEle = leftIter.next();
                    Comparable rightEle = rightIter.next();
                    int ret = leftEle.compareTo(rightEle);
                    if (ret != 0) {
                        return ret;
                    }
                }
                if (leftIter.hasNext()) {
                    return 1;
                } else if (rightIter.hasNext()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }
    };

    public static <G> String toJsonStr(G counters) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        return gson.toJson(counters);
    }

    public static <G extends Comparable<? super G>> String orderListsAndToJsonStr(List<List<G>> counterLists) {
        if (counterLists == null || counterLists.isEmpty()) {
            return null;
        }
        Collections.sort(counterLists, LIST_COMPARABLE_COMPATOR);
        for (List<G> counterList : counterLists) {
            Collections.sort(counterList);
        }
        Type type = new TypeToken<List<G>>() {}.getType();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        return gson.toJson(counterLists, type);
    }

    // public static <G extends Comparable<? super G>> String
    // orderListAndToJsonStr(List<G> counters) {
    // if (counters == null || counters.isEmpty()) {
    // return null;
    // }
    // Collections.sort(counters);
    // Type type = new TypeToken<List<G>>() {
    // }.getType();
    // Gson gson = new
    // GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    // return gson.toJson(counters, type);
    // }

    public static <G> String listToJsonStr(List<G> counters) {
        if (counters == null || counters.isEmpty()) {
            return null;
        }
        Type type = new TypeToken<List<G>>() {}.getType();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        return gson.toJson(counters, type);
    }

    public static <K, V> String toJsonStr(Map<K, V> counters) {
        Type type = new TypeToken<Map<K, V>>() {}.getType();
        Gson gson = new Gson();
        return gson.toJson(counters, type);
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromJsonStr(String value, Class<T> clazz) throws Exception {
        if (value == null) {
            return null;
        }
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        JsonReader reader = null;
        try {
            reader = new JsonReader(new StringReader(value));
            reader.setLenient(true);
            return (T) gson.fromJson(reader, clazz);
        } catch (Exception e) {
            throw e;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public static <G> G fromJsonStr(String value, Type type) throws Exception {
        if (value == null) {
            return null;
        }
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        JsonReader reader = null;
        try {
            reader = new JsonReader(new StringReader(value));
            reader.setLenient(true);
            return gson.fromJson(reader, type);
        } catch (Exception e) {
            throw e;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public static List<String> fromJsonStrToStrList(String value) throws Exception {
        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> ret = GsonUtils.fromJsonStr(value, type);
        if (ret == null) {
            return Collections.emptyList();
        }
        return ret;
    }

    public static String[] fromJsonStrToStrArray(String value) throws Exception {
        Type type = new TypeToken<String[]>() {}.getType();
        String[] ret = GsonUtils.fromJsonStr(value, type);
        if (ret == null) {
            return new String[0];
        }
        return ret;
    }

    public static String toJson(Object o) {
        return new Gson().toJson(o);
    }

    public static String toJson(Object o, boolean excludeExposeAnnotation) {
        if (excludeExposeAnnotation) {
            return buildExcludeExposeAnnotationGson().toJson(o);
        } else {
            return toJson(o);
        }
    }

    private static Gson buildExcludeExposeAnnotationGson() {
        return new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                final Expose expose = fieldAttributes.getAnnotation(Expose.class);
                return expose != null && !expose.serialize();
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return false;
            }
        }).addDeserializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                final Expose expose = fieldAttributes.getAnnotation(Expose.class);
                return expose != null && !expose.deserialize();
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return false;
            }
        }).create();
    }

    public static <T> T convert(String value, Type type) {
        if (value == null) {
            return null;
        }
        return new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().fromJson(value, type);
    }

    @SuppressWarnings("unchecked")
    public static <T> T convert(String value, Class<T> clazz) throws Exception {
        if (value == null) {
            return null;
        }
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        JsonReader reader = null;
        try {
            reader = new JsonReader(new StringReader(value));
            reader.setLenient(true);
            return (T) gson.fromJson(reader, clazz);
        } catch (Exception e) {
            throw e;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private static JsonSerializer<Date> ser = new JsonSerializer<Date>() {
        @Override
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            return src == null ? null : new JsonPrimitive(src.getTime());
        }
    };

    private static JsonDeserializer<Date> deser = new JsonDeserializer<Date>() {
        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return json == null ? null : new Date(json.getAsLong());
        }
    };
    private static Gson longDateGson = new GsonBuilder().registerTypeAdapter(Date.class, ser)
            .registerTypeAdapter(Date.class, deser).create();

    public static <G> G fromLongDateJsonStr(String value, Type type) throws Exception {
        return longDateGson.fromJson(value, type);
    }
}
