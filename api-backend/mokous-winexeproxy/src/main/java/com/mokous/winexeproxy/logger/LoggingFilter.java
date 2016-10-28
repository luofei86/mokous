// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.winexeproxy.logger;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.mokous.web.model.ApiRespWrapper;
import com.mokous.web.model.logger.AbstractRequestLoggingFilterExt;
import com.mokous.web.model.logger.CopyResponseStreamWrapper;
import com.mokous.web.model.logger.HttpServletResponseLoggingWrapper;
import com.mokous.web.utils.GsonUtils;
import com.mokous.web.utils.IPUtil;



/**
 * @author luofei (Your Name Here)
 * @date 2016年10月4日
 * 
 */
public class LoggingFilter extends AbstractRequestLoggingFilterExt {
    private static final Logger l4jlogger = Logger.getLogger(LoggingFilter.class);
    private static final Logger requestLogger = Logger.getLogger("mokous_winexeproxy_request");
    public static ThreadLocal<Map<String, String>> threadLocalMap = new ThreadLocal<Map<String, String>>();

    private static final String REQUEST_LOG_FORMAT = "%s\tId:%s\ttype:request\tIp:%s\tMethod:%s\tUri:%s\tQueryStr:%s\tParameter:%s";
    private static final String REQUEST_ID_ATTR = "log_request_id";
    private static final String REQUEST_START_TIME = "log_request_start_time";

    private static final Map<String, Boolean> logRespMap = new HashMap<String, Boolean>();
    static {
        logRespMap.put("/mokous-winexeproxy/app/download.json", true);
        logRespMap.put("/app/download.json", true);
    }
    public static final Set<String> INGORE_LOG_REQUESTMAPS = new HashSet<String>() {
        /**
         * 
         */
        private static final long serialVersionUID = -4054907404786160027L;

        {
            add("/system/tunning.json");
            add("/ios-daemon/app/online");
        }
    };

    @Override
    protected void logRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (isIgnoreUri(uri)) {
            return;
        }
        request.setAttribute(REQUEST_START_TIME, System.currentTimeMillis());
        HttpSession session = request.getSession(false);
        String id = "";
        if (session != null) {
            id = session.getId();
        } else {
            id = UUID.randomUUID().toString();
        }
        request.setAttribute(REQUEST_ID_ATTR, id);
        String method = request.getMethod();
        String queryStr = request.getQueryString();
        String parameter = getRequestParams(request);
        String ip = IPUtil.getClientIP(request);
        Map<String, String> commonPara = new HashMap<String, String>();
        commonPara.put("ip", ip);
        commonPara.put("method", method);
        commonPara.put("queryStr", queryStr);
        commonPara.put("parameter", parameter);
        threadLocalMap.set(commonPara);
        // log all request
        l4jlogger.info(String.format(REQUEST_LOG_FORMAT, now(), id, ip, method, uri, queryStr, parameter));
    }

    private boolean isMultipart(HttpServletRequest request) {
        return request.getContentType() != null && request.getContentType().startsWith("multipart/form-data");
    }

    private boolean isJson(HttpServletResponse response) {
        return response.getContentType() != null && response.getContentType().startsWith("application/json");
    }

    private boolean isIgnoreUri(String uri) {
        for (String ignoreUrl : INGORE_LOG_REQUESTMAPS) {
            if (StringUtils.startsWithIgnoreCase(uri, ignoreUrl)) {
                return true;
            }

            if (INGORE_LOG_REQUESTMAPS.contains(uri)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void logResponse(HttpServletRequest request, HttpServletResponse response) {
        if (!(response instanceof HttpServletResponseLoggingWrapper) || !isJson(response)) {
            return;
        }
        String uri = request.getRequestURI();
        if (isExcludeUri(uri)) {
            return;
        }
        if (isIgnoreUri(uri)) {
            return;
        }
        long startTime = (long) request.getAttribute(REQUEST_START_TIME);
        long endTime = System.currentTimeMillis();
        long elapse = endTime - startTime;
        String id = (String) request.getAttribute(REQUEST_ID_ATTR);

        String status = "";
        String message = "";
        String respData = "";
        try {
            ServletOutputStream output = response.getOutputStream();
            // output.toString()
            if (output instanceof CopyResponseStreamWrapper) {
                String copy = ((CopyResponseStreamWrapper) output).getCopy();
                if (StringUtils.isNotBlank(copy)) {
                    try {
                        ApiRespWrapper<?> apiRespWrapper = GsonUtils.fromJsonStr(copy, ApiRespWrapper.class);
                        status = String.valueOf(apiRespWrapper.getStatus());
                        message = apiRespWrapper.getMessage();
                        if (needLogResponse(uri)) {
                            Object d = apiRespWrapper.getData();
                            respData = d == null ? "" : d.toString();
                        }
                    } catch (Exception e) {
                    }

                }
            }
        } catch (IOException e) {
        }

        Map<String, String> commonPara = threadLocalMap.get();
        String ip = commonPara == null ? IPUtil.getClientIP(request) : commonPara.get("ip");
        String method = commonPara == null ? request.getMethod() : commonPara.get("method");
        String queryStr = commonPara == null ? request.getQueryString() : commonPara.get("queryStr");
        queryStr = StringUtils.replace(queryStr, "\r\n", "");
        queryStr = StringUtils.replace(queryStr, "\n", "");
        String parameter = commonPara == null ? getRequestParams(request) : commonPara.get("parameter");
        parameter = StringUtils.replace(parameter, "\r\n", "");
        parameter = StringUtils.replace(parameter, "\n", "");
        String logInfo = now() + "\tId:" + id + "\ttype:response\tUri:" + uri + "\tIp:" + ip + "\tMethod:" + method
                + "\tQueryStr:" + queryStr + "\tParameter:" + parameter + "\tHttpStatus:" + response.getStatus()
                + "\tStatus:" + status + "\tMessage:" + message + "\telapseTime:" + elapse + "\tData:" + respData;
        requestLogger.info(logInfo);
        threadLocalMap.remove();
    }

    private boolean needLogResponse(String uri) {
        return logRespMap.containsKey(uri);
    }

    private String getRequestParams(HttpServletRequest request) {
        String parameter = "";
        if (request instanceof HttpServletRequest && !isMultipart(request)) {
            Map<String, String[]> parameters = request.getParameterMap();
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String[] values = entry.getValue();
                String key = entry.getKey();
                String keyValues = "";
                if (values == null || values.length == 0) {
                    continue;
                } else {
                    if (values.length == 1) {
                        keyValues = values[0];
                    } else {
                        keyValues = Arrays.toString(values);
                    }
                }
                parameter += "{" + key + ":" + keyValues + "}";
            }
        }
        return parameter;
    }

    private static String now() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        return time;
    }

    @Override
    protected boolean isExcludeUri(String uri) {
        if (uri.startsWith("/admin/")) {
            return true;
        }
        return false;
    }
}
