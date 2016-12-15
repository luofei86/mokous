// Copyright 2016 ios.appchina.com Inc. All Rights Reserved.

package com.mokous.stock.core.dto.notice;

import com.mokous.core.dto.DbStatus;

/**
 * @author luofei@appchina.com create date: Nov 27, 2016
 *
 */
public class UserNoticeInfo extends DbStatus {

    /**
     * 
     */
    private static final long serialVersionUID = -4310746016631554895L;
    private int id;
    private int userId;
    private int stockId;
    // 要提醒的消息id
    private int noticeId;
    // 提醒的阈值
    private float noticeThreshold;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public int getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(int noticeId) {
        this.noticeId = noticeId;
    }

    public float getNoticeThreshold() {
        return noticeThreshold;
    }

    public void setNoticeThreshold(float noticeThreshold) {
        this.noticeThreshold = noticeThreshold;
    }
}
