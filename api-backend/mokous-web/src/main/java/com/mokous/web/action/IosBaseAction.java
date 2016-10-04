// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.web.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mokous.web.exception.ServiceException;
import com.mokous.web.model.ApiRespWrapper;
import com.mokous.web.utils.GsonUtils;
import com.mokous.web.utils.IPUtil;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月4日
 * 
 */
public class IosBaseAction {
    private static final Logger log = Logger.getLogger(IosBaseAction.class);

    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public ApiRespWrapper<Object> handleServiceException(ServiceException e, HttpServletRequest request) {
        log.error("Handle action failed.Ip:" + IPUtil.getClientIP(request), e);
        return new ApiRespWrapper<Object>(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ApiRespWrapper<Object> handleException(Exception e, HttpServletRequest request) {
        if (e.getClass().getName().contains("ClientAbortException")) {
            log.error("Handle action " + e.getClass().getName(), e);
            return new ApiRespWrapper<Object>(ServiceException.ERR_CODE_UNKNOWN_ERROR, e.getMessage());
        }
        String urlMapping = request.getRequestURL().toString();
        log.error("Handle action " + urlMapping + " failed.Ip:" + IPUtil.getClientIP(request) + ",Paras:"
                + mapToString(request.getParameterMap()), e);
        return new ApiRespWrapper<Object>(ServiceException.ERR_CODE_UNKNOWN_ERROR, e.getMessage());
    }

    private static String mapToString(Map<String, ?> map) {
        return GsonUtils.toJson(map);
    }

    protected ApiRespWrapper<Boolean> returnBooleanResp(String errMsg) {
        if (StringUtils.isEmpty(errMsg)) {
            return new ApiRespWrapper<Boolean>(true);
        } else {
            return new ApiRespWrapper<Boolean>(-1, errMsg, false);
        }
    }

}
