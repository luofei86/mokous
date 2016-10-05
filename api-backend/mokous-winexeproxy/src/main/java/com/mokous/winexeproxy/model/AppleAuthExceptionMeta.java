// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.winexeproxy.model;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
public class AppleAuthExceptionMeta {
    public static final AppleAuthExceptionMeta IOS_APPLE_ACCOUNT_DISABLE = new AppleAuthExceptionMeta(
            AuthResultCode.IOS_EXCEPTION_AUTH_FAILED_ACCOUNT_DISABLE, "Your account is disabled.");
    public static final AppleAuthExceptionMeta IOS_APPLE_ACCOUNT_WRONG = new AppleAuthExceptionMeta(
            AuthResultCode.IOS_EXCEPTION_AUTH_FAILED_ACCOUNT_WRONG,
            "Your Apple ID or password was entered incorrectly.");
    public static final AppleAuthExceptionMeta IOS_APPLE_ACCOUNT_UNUSED = new AppleAuthExceptionMeta(
            AuthResultCode.IOS_EXCEPTION_AUTH_FAILED_UNUSERD_APPLEID,
            "Apple ID not used in iTunes Store. Check on iTunes");
    public static final AppleAuthExceptionMeta IOS_APPLE_ACCOUNT_UNVERIFIED = new AppleAuthExceptionMeta(
            AuthResultCode.IOS_EXCEPTION_AUTH_FAILED_UNVERIFY_APPLEID, "Not verify appleId");
    public static final AppleAuthExceptionMeta IOS_APPLE_UNKNOW_ERROR_FROM_APPLE = new AppleAuthExceptionMeta(
            AuthResultCode.IOS_EXCEPTION_AUTH_FAILED_APPLE_UNKNOW_FAILED, "Unknown error from apple");
    public static final AppleAuthExceptionMeta IOS_APPLE_UNCAPTURED_ERROR_FROM_APPLE = new AppleAuthExceptionMeta(
            AuthResultCode.IOS_EXCEPTION_AUTH_FAILED_UNCAPTURED_ERROR, "Some error from apple");

    public static final AppleAuthExceptionMeta IOS_APPLE_GET_SIGN_SAP_SETUP_CERT_ERROR = new AppleAuthExceptionMeta(
            AuthResultCode.IOS_EXCEPTION_GET_SIGN_SAP_SETUP_CERT_FAILED, "get sigSapSetupCert fail");
    public static final AppleAuthExceptionMeta IOS_APPLE_GET_SIGN_SAP_SETUP_BUFFER_ERROR = new AppleAuthExceptionMeta(
            AuthResultCode.IOS_EXCEPTION_GET_SIGN_SAP_SETUP_BUFFER_FAILED, "get sigSapSetupBuffer fail");
    public static final AppleAuthExceptionMeta IOS_APPLE_GET_SIGN_SAP_SETUP_ERROR = new AppleAuthExceptionMeta(
            AuthResultCode.IOS_EXCEPTION_GET_SIGN_SAP_SETUP_FAILED, "init iTunes, sigSapSetup fail");

    private int errorCode;
    private String message;

    public AppleAuthExceptionMeta(int errorCode, String message) {
        this.message = message;
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
