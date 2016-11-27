// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.web.model;

import java.io.Serializable;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月4日
 * 
 */
public class ApiRespWrapper<G> implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1681368930097874683L;
    private int status;
    private String message;
    private G data;

    public ApiRespWrapper(int status, String message, G data) {
        super();
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public ApiRespWrapper(int status, String message) {
        super();
        this.status = status;
        this.message = message;
        this.data = null;
    }

    public ApiRespWrapper(G data) {
        super();
        this.status = 0;
        this.message = "ok";
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public G getData() {
        return data;
    }

    public void setData(G data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ApiRespWrapper [status=" + status + ", message=" + message + ", data=" + data + "]";
    }
}
