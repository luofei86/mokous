// Copyright 2016 ios.appchina.com Inc. All Rights Reserved.

package com.mokous.stock.core.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * @author luofei@appchina.com create date: Nov 24, 2016
 *
 */
public class DbStatus implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -657319283691428479L;
    private int id;
    private int status;
    private Date updateTime;
    private Date createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
