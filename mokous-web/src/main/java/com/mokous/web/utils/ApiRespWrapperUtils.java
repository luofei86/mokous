// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.web.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;

import com.mokous.web.model.ApiRespWrapper;
import com.mokous.web.model.ListWrapResp;
import com.mokous.web.model.StartSizeParameter;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月4日
 * 
 */
public class ApiRespWrapperUtils {
    public static ApiRespWrapper<Boolean> wrapBooleanResp(String errMsg) {
        if (StringUtils.isEmpty(errMsg) || StringUtils.equalsIgnoreCase("ok", errMsg)) {
            return new ApiRespWrapper<Boolean>(0, "", true);
        }
        return new ApiRespWrapper<Boolean>(-1, errMsg, false);
    }

    public static String handleBooleanResp(ApiRespWrapper<Boolean> resp) {
        String errMsg = "";
        if (resp == null) {
            errMsg = "访问远程服务失败。请问询开发人员";
        } else if (resp.getData() == null || !resp.getData().booleanValue()) {
            errMsg = "访问远程服务返回错误. Errmsg:" + resp.getMessage();
        }
        return errMsg;
    }

    public static <T> T handleValueResp(ApiRespWrapper<T> resp, StartSizeParameter para, Model model) {
        return handleValueResp("value", resp, para, model);
    }

    public static <T> T handleValueResp(String key, ApiRespWrapper<T> resp, StartSizeParameter para, Model model) {
        if (resp != null && resp.getData() != null) {
            model.addAttribute(key, resp.getData());
            model.addAttribute("para", para);
            return resp.getData();
        }
        return null;
    }


    public static <T> List<T> handleListResp(ApiRespWrapper<ListWrapResp<T>> resp, StartSizeParameter para, Model model) {
        return handleListResp("values", true, resp, para, model);
    }

    public static <V> Map<Integer, V> handleMapResp(String key, boolean setTotal, ApiRespWrapper<Map<Integer, V>> resp,
            StartSizeParameter para, Model model) {
        if (resp != null && CollectionUtils.notEmptyAndNull(resp.getData())) {
            if (setTotal) {
                para.getPager().setTotal(resp.getData().size());
            }
            Map<String, V> respMap = new HashMap<String, V>();
            if (resp != null && resp.getData() != null) {
                for (Entry<Integer, V> entry : resp.getData().entrySet()) {
                    respMap.put(entry.getKey().toString(), entry.getValue());
                }
            }
            model.addAttribute(key, respMap);
            model.addAttribute("para", para);
            return resp.getData();
        }
        return Collections.emptyMap();
    }

    public static <T> List<T> handleListResp(String key, boolean setTotal, ApiRespWrapper<ListWrapResp<T>> resp,
            StartSizeParameter para, Model model) {
        if (resp != null && resp.getData() != null) {
            if (setTotal) {
                para.getPager().setTotal(resp.getData().getTotalCount());
            }
            model.addAttribute(key, resp.getData().getResultList());
            model.addAttribute("para", para);
            return resp.getData().getResultList();
        }
        return Collections.emptyList();
    }

}
