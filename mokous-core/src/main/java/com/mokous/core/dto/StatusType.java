// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.core.dto;


/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
public enum StatusType {
    STATUS_ALL(-9999, "全部"), STATUS_OK(0, "正常"), STATUS_DEL(-1, "删除");
    @EnumHtmlSelectType.KEY
    private int status;
    @EnumHtmlSelectType.VALUE
    private String desc;

    private StatusType(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
