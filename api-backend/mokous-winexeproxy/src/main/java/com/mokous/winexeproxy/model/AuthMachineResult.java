// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.winexeproxy.model;

import java.io.Serializable;


/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
public class AuthMachineResult implements Serializable {

    // public static final AuthMachineResult AUTH_MACHINE_SUCCESS = new
    // AuthMachineResult(AuthMachineResult.AUTH_MACHINE_SUCCESS_CODE);

    public static final AuthMachineResult AUTH_MACHINE_UNKNOWN_ERROR = new AuthMachineResult(
            AuthMachineResult.AUTH_MACHINE_FAILED_CODE, "授权机器未知错误");
    public static final AuthMachineResult AUTH_MACHINE_EMPTY_RESULT_TIMEOUT = new AuthMachineResult(
            AuthMachineResult.EMPTY_RESULT_TIMEOUT_CODE, "授权机器超时");
    public static final AuthMachineResult AUTH_MACHINE_FULL = new AuthMachineResult(
            AuthMachineResult.AUTH_MACHINE_ALREADY_AUTH_FIVE_CODE, "已经授权了5台机器，请取消不用的机器授权");
    public static final AuthMachineResult AUTH_MACHINE_NOACCOUNT_MESSAGE = new AuthMachineResult(
            AuthMachineResult.AUTH_MACHINE_NO_ACCOUNT_MESSAGE_CODE, "授权机器无账号信息");
    public static final AuthMachineResult AUTH_MACHINE_EXPIRED_PASSWORD_TOKEN = new AuthMachineResult(
            AuthMachineResult.AUTH_MACHINE_EXPIRED_PASSWORD_TOKEN_CODE, "登陆令牌超时");
    public static final AuthMachineResult AUTH_MACHINE_APPLE_500_ERROR = new AuthMachineResult(
            AuthMachineResult.AUTH_MACHINE_APPLE_500_ERROR_CODE, "请求导致不明错误");
    public static final AuthMachineResult AUTH_MACHINE_EXCEPTION = new AuthMachineResult(
            AuthMachineResult.AUTH_MACHINE_EXCEPTION_CODE, "服务器内部错误");
    private static final long serialVersionUID = -979992438000302689L;
    public static final int AUTH_MACHINE_SUCCESS_CODE = 0;
    private static final int AUTH_MACHINE_FAILED_CODE = 1;
    private static final int EMPTY_RESULT_TIMEOUT_CODE = 400;
    private static final int AUTH_MACHINE_ALREADY_AUTH_FIVE_CODE = 3002;
    private static final int AUTH_MACHINE_NO_ACCOUNT_MESSAGE_CODE = 1001;
    private static final int AUTH_MACHINE_EXPIRED_PASSWORD_TOKEN_CODE = 2034;
    private static final int AUTH_MACHINE_APPLE_500_ERROR_CODE = 1500;
    private static final int AUTH_MACHINE_EXCEPTION_CODE = 500;
    public static final int NOT_AUTH_MACHINE = -1;


    private int status;
    private String errorMsg;
    private String keyBag;
    private String diversitybag;

    public AuthMachineResult(int status) {
        this.status = status;
    }

    public AuthMachineResult(int status, String errorMsg) {
        this.status = status;
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AuthMachineResult{" + "status=" + status + ", errorMsg='" + errorMsg + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AuthMachineResult))
            return false;

        AuthMachineResult that = (AuthMachineResult) o;

        if (status != that.status)
            return false;
        if (!errorMsg.equals(that.errorMsg))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = status;
        result = 31 * result + errorMsg.hashCode();
        return result;
    }

    public void setKeyBag(String keyBag) {
        this.keyBag = keyBag;
    }

    public String getKeyBag() {
        return keyBag;
    }

    public void setDiversitybag(String diversitybag) {
        this.diversitybag = diversitybag;
    }

    public String getDiversitybag() {
        return diversitybag;
    }
}
