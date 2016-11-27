// Copyright 2016 ios.appchina.com Inc. All Rights Reserved.

package com.mokous.stock.core.dto.info;

import java.util.Date;

import com.mokous.stock.core.dto.DbStatus;

/**
 * @author luofei@appchina.com create date: Nov 23, 2016
 *
 */
public class StockInfo extends DbStatus {
    /**
     * 
     */
    private static final long serialVersionUID = 6413399634096504535L;
    private String code;
    private String name;
    private Date saleDate;
    private int securitiesMarketId;// 证券交易市场Id
    private float currentPrice;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public float getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(float currentPrice) {
        this.currentPrice = currentPrice;
    }

    public int getSecuritiesMarketId() {
        return securitiesMarketId;
    }

    public void setSecuritiesMarketId(int securitiesMarketId) {
        this.securitiesMarketId = securitiesMarketId;
    }
}
