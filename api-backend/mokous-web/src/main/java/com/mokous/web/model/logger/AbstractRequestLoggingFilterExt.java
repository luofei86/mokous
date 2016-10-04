// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.web.model.logger;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.AbstractRequestLoggingFilter;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月4日
 * 
 */
public abstract class AbstractRequestLoggingFilterExt extends AbstractRequestLoggingFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, final HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (isExcludeUri(uri) || !isJsonRequest(uri)) {
            super.doFilterInternal(request, response, filterChain);
            afterRequest(request, response);
        } else {
            final HttpServletResponseLoggingWrapper httpServletResponseLoggingWrapper = new HttpServletResponseLoggingWrapper(
                    response);
            super.doFilterInternal(request, httpServletResponseLoggingWrapper, filterChain);
            afterRequest(request, httpServletResponseLoggingWrapper);
        }
    }

    protected void afterRequest(HttpServletRequest request, HttpServletResponse response) {
        logResponse(request, response);
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        // doNothing()
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        logRequest(request);
    }

    protected abstract void logRequest(HttpServletRequest request);

    protected abstract void logResponse(HttpServletRequest request, HttpServletResponse response);

    protected abstract boolean isExcludeUri(String uri);

    protected boolean isJsonRequest(String uri) {
        return uri.endsWith(".json");
    }

}
