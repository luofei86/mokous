// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.core.dto;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
public class DbResp {
    private boolean insert;
    private boolean update;

    public DbResp(boolean insert, boolean update) {
        super();
        this.insert = insert;
        this.update = update;
    }

    public boolean isInsert() {
        return insert;
    }

    public void setInsert(boolean insert) {
        this.insert = insert;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    @Override
    public String toString() {
        return "DbResp [insert=" + insert + ", update=" + update + "]";
    }
}
