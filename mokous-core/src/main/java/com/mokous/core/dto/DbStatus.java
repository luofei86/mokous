// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.core.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
public class DbStatus implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 2548596565241111669L;
    public static final int STATUS_ALL = StatusType.STATUS_ALL.getStatus();
    public static final int STATUS_OK = StatusType.STATUS_OK.getStatus();
    public static final int STATUS_DEL = StatusType.STATUS_DEL.getStatus();
    public static final String STATUS_COLUMN = "status";
    public static final String ID_COLUMN = "id";
    public static final String STATUS_ALL_DESC = "all";
    public static final Map<String, String> STATUS = new LinkedHashMap<String, String>() {
        /**
         * 
         */
        private static final long serialVersionUID = 8542698439523192232L;

        {
            for (StatusType type : StatusType.values()) {
                put(String.valueOf(type.getStatus()), type.getDesc());
            }
        }
    };
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

    public boolean illegalStatus() {
        return status != STATUS_DEL && status != STATUS_OK;
    }

    public boolean isDel() {
        return !isOk();
    }

    public boolean isOk() {
        return status == STATUS_OK;
    }

    @Override
    public String toString() {
        return "DbStatus [id=" + id + ", status=" + status + ", updateTime=" + updateTime + ", createTime="
                + createTime + "]";
    }
}
