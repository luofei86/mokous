//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.base.web.model;

/**
 * @author luofei
 * Generate 2020/2/4
 */
public class ResultVO<T> {
    private int code = ResultConstantUtils.SUCCESS_CODE;
    private String msg = ResultConstantUtils.SUCCESS_MSG;
    private T data;

    public ResultVO() {
    }

    public ResultVO(T data) {
        this.data = data;
    }

    private ResultVO(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static final ResultVO SERVICE_EXCEPTION_RESULT_VO = new ResultVO(ResultConstantUtils.FAILED_CODE,
            ResultConstantUtils.FAILED_MSG);

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
