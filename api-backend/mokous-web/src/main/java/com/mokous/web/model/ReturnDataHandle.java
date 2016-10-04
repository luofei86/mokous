// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.web.model;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.mokous.web.utils.GsonUtils;


/**
 * @author luofei (Your Name Here)
 * @date 2016年10月4日
 * 
 */
public interface ReturnDataHandle<T> {
    public T handle(String value) throws Exception;

    public static final ReturnDataHandle<ApiRespWrapper<Map<String, String>>> MAP_RD_HANDLE = new ReturnDataHandle<ApiRespWrapper<Map<String, String>>>() {

        @Override
        public ApiRespWrapper<Map<String, String>> handle(String value) throws Exception {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            Type type = new TypeToken<ApiRespWrapper<Map<String, String>>>() {}.getType();
            Gson gson = new Gson();
            return gson.fromJson(value, type);
        }
    };

    public static final ReturnDataHandle<ApiRespWrapper<Integer>> INTEGER_RD_HANDLE = new ReturnDataHandle<ApiRespWrapper<Integer>>() {


        @Override
        public ApiRespWrapper<Integer> handle(String value) throws Exception {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            Type type = new TypeToken<ApiRespWrapper<Integer>>() {}.getType();
            Gson gson = new Gson();
            return gson.fromJson(value, type);
        }
    };

    public static final ReturnDataHandle<ApiRespWrapper<Long>> LONG_RD_HANDLE = new ReturnDataHandle<ApiRespWrapper<Long>>() {


        @Override
        public ApiRespWrapper<Long> handle(String value) throws Exception {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            Type type = new TypeToken<ApiRespWrapper<Long>>() {}.getType();
            Gson gson = new Gson();
            return gson.fromJson(value, type);
        }
    };

    public static final ReturnDataHandle<ApiRespWrapper<Boolean>> BOOLEAN_RD_HANDLE = new ReturnDataHandle<ApiRespWrapper<Boolean>>() {


        @Override
        public ApiRespWrapper<Boolean> handle(String value) throws Exception {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            Type type = new TypeToken<ApiRespWrapper<Boolean>>() {}.getType();
            Gson gson = new Gson();
            return gson.fromJson(value, type);
        }
    };

    public static final ReturnDataHandle<ApiRespWrapper<String>> STRING_RD_HANDLE = new ReturnDataHandle<ApiRespWrapper<String>>() {


        @Override
        public ApiRespWrapper<String> handle(String value) throws Exception {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            Type type = new TypeToken<ApiRespWrapper<String>>() {}.getType();
            Gson gson = new Gson();
            return gson.fromJson(value, type);
        }
    };

    public static final ReturnDataHandle<String[]> ARRAY_HANDLE = new ReturnDataHandle<String[]>() {
        @Override
        public String[] handle(String value) throws Exception {
            if (StringUtils.isEmpty(value)) {
                return new String[] {};
            }
            Type type = new TypeToken<String[]>() {}.getType();
            Gson gson = new Gson();
            return gson.fromJson(value, type);
        }
    };

    public static final ReturnDataHandle<List<String>> LIST_HANDLE = new ReturnDataHandle<List<String>>() {
        @Override
        public List<String> handle(String value) throws Exception {
            if (StringUtils.isEmpty(value)) {
                return Collections.emptyList();
            }
            Type type = new TypeToken<List<String>>() {}.getType();
            Gson gson = new Gson();
            return gson.fromJson(value, type);
        }
    };

    public static final ReturnDataHandle<Boolean> CRAWER_HANDLE = new ReturnDataHandle<Boolean>() {
        class CrawerResult {
            boolean success;
        }

        @Override
        public Boolean handle(String value) throws Exception {
            if (StringUtils.isEmpty(value)) {
                return false;
            }
            return GsonUtils.convert(value, CrawerResult.class).success;
        }
    };

    public static final ReturnDataHandle<List<LinkedTreeMap<?, ?>>> MARKETUTIL_LINKEDTREEMAPLIST_HANDLE = new ReturnDataHandle<List<LinkedTreeMap<?, ?>>>() {

        @SuppressWarnings("unchecked")
        @Override
        public List<LinkedTreeMap<?, ?>> handle(String value) throws Exception {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            return GsonUtils.convert(value, List.class);
        }
    };

    public static final ReturnDataHandle<LinkedTreeMap<?, ?>> MARKETUTIL_DETAIL_ADAPTED_HANDLE = new ReturnDataHandle<LinkedTreeMap<?, ?>>() {
        @Override
        public LinkedTreeMap<?, ?> handle(String value) throws Exception {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            return GsonUtils.convert(value, LinkedTreeMap.class);
        }
    };

    public static final ReturnDataHandle<String> NORMAL_STRING_HANDLE = new ReturnDataHandle<String>() {
        @Override
        public String handle(String value) throws Exception {
            return value;
        }
    };
}
