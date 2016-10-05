// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.winexeproxy.exception;

import com.mokous.winexeproxy.model.AppleAuthExceptionMeta;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
public class AppleAuthException extends RuntimeException {

    private static final long serialVersionUID = -3919035414542119929L;
    private int errorCode;
    private String content;

    public AppleAuthException(AppleAuthExceptionMeta meta, String content) {
        super(meta.getMessage());
        this.errorCode = meta.getErrorCode();
        this.content = content;
    }

    public AppleAuthException(int errorCode, String errorMsg, String content) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.content = content;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
