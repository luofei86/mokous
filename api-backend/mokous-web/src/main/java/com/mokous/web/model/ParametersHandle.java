// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.web.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mokous.web.utils.CollectionUtils;
import com.mokous.web.utils.GsonUtils;
import com.mokous.web.utils.UrlUtils;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月4日
 * 
 */
public interface ParametersHandle<T> {


    public static class EndSizeParameter {
        private int endId;
        private int size;

        public EndSizeParameter(int endId, int size) {
            this.endId = endId;
            this.size = size;
        }

        public int getEndId() {
            return endId;
        }

        public void setEndId(int endId) {
            this.endId = endId;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        @Override
        public String toString() {
            return "EndSizeParameter [endId=" + endId + ", size=" + size + "]";
        }
    }

    public String handle(T t);

    public static final ParametersHandle<Object> PS_POST_JSONMAP_HANDLE = new ParametersHandle<Object>() {
        @Override
        public String handle(Object t) {
            return GsonUtils.toJson(t);
        }
    };

    public static final ParametersHandle<Object> PS_DATA_JSONMAP_HANDLE = new ParametersHandle<Object>() {
        @Override
        public String handle(Object t) {
            return "?data=" + GsonUtils.toJson(t);
        }
    };

    public static final ParametersHandle<Integer> PS_ID_HANDLE = new ParametersHandle<Integer>() {
        @Override
        public String handle(Integer id) {
            return "?id=" + id;
        }
    };
    public static final ParametersHandle<Integer> PS_ROOTID_HANDLE = new ParametersHandle<Integer>() {
        @Override
        public String handle(Integer id) {
            return "?rootId=" + id;
        }
    };

    public static final ParametersHandle<Object> PS_HANDLE = new ParametersHandle<Object>() {
        @Override
        public String handle(Object t) {
            return UrlUtils.objectToUrlQuery(t);
        }
    };

    public static final ParametersHandle<Map<String, Object>> PS_MAP_HANDLE = new ParametersHandle<Map<String, Object>>() {
        @Override
        public String handle(Map<String, Object> map) {
            return UrlUtils.mapToUrlQuery(map);
        }
    };

    public static final ParametersHandle<List<Integer>> PS_IDS_HANDLE = new ParametersHandle<List<Integer>>() {
        @Override
        public String handle(List<Integer> params) {
            return "?ids=" + CollectionUtils.listToString(params, ",");
        }
    };

    public static final PostParametersHandle<Object> DATA_POST_HANDLE = new PostParametersHandle<Object>() {

        @Override
        public Map<String, Object> handle(Object value) {
            if (value == null) {
                return null;
            }
            Map<String, Object> map = new HashMap<String, Object>();
            if (value instanceof String) {
                map.put("data", value.toString());
            } else if (value instanceof List) {
                List<?> values = (List<?>) value;
                map.put("data", GsonUtils.listToJsonStr(values));
            } else {
                map.put("data", GsonUtils.toJsonStr(value));
            }
            return map;
        }
    };

    public static final ParametersHandle<List<Integer>> IDS_ARRAY_PD_HANDLE = new ParametersHandle<List<Integer>>() {
        @Override
        public String handle(List<Integer> array) {
            String paras = "";
            boolean first = true;
            for (Integer object : array) {
                if (object != null) {
                    if (first) {
                        paras += "?ids" + "=" + object.toString();
                        first = false;
                    } else {
                        paras += "&" + "ids=" + object.toString();
                    }
                }
            }
            return paras;
        }
    };

    public static final ParametersHandle<String> PS_BUNDLEID_HANDLE = new ParametersHandle<String>() {

        @Override
        public String handle(String value) {
            return "?bundleId=" + value;
        }
    };

}
