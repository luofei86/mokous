//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.base.exception;

/**
 * @author luofei
 * Generate 2020/1/12
 */
public class BizException extends Exception {
    private int errorCode;
    public static final int OK = 0;
    public static final int ERR_CODE_UNKNOWN_ERROR = 999;
    public static final String ERR_DESC_UNKNOWN_ERROR = "未知异常";
    public static final int ERR_CODE_SQL_ERROR = 333;
    public static final String ERR_DESC_SQL_ERROR = "数据库异常";
    public static final int ERR_CODE_PARAMETER_ERROR = 600;
    public static final String ERR_DESC_PARAMETER_ERROR = "参数异常";
    public static final int ERR_CODE_CACHE_ERROR = 700;
    public static final String ERR_DESC_CACHE_ERROR = "缓存异常";
    public static final int ERR_CODE_NET_ERROR = 800;
    public static final String ERR_DESC_NET_ERROR = "网络异常";

    public BizException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BizException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BizException() {
    }

    public static BizException getSqlException() {
        return new BizException(ERR_CODE_SQL_ERROR, ERR_DESC_SQL_ERROR);
    }

    public static BizException getSqlException(String msg) {
        return new BizException(ERR_CODE_SQL_ERROR, msg);
    }

    public static BizException getParameterException() {
        return new BizException(ERR_CODE_PARAMETER_ERROR, ERR_DESC_PARAMETER_ERROR);
    }

    public static BizException getParameterException(String msg) {
        return new BizException(ERR_CODE_PARAMETER_ERROR, msg);
    }

    public static BizException getCacheException() {
        return new BizException(ERR_CODE_CACHE_ERROR, ERR_DESC_CACHE_ERROR);
    }

    public static BizException getCacheException(String msg) {
        return new BizException(ERR_CODE_CACHE_ERROR, msg);
    }

    public static BizException getNetException() {
        return new BizException(ERR_CODE_NET_ERROR, ERR_DESC_NET_ERROR);
    }

    public static BizException getNetException(String msg) {
        return new BizException(ERR_CODE_NET_ERROR, msg);
    }

    public static BizException getInternalException(String s) {
        return new BizException(ERR_CODE_UNKNOWN_ERROR, s);
    }
}
