// Copyright 2016 ios.appchina.com Inc. All Rights Reserved.

package com.mokous.stock.core.dto.info;

import com.mokous.core.dto.DbStatus;

/**
 * @author luofei@appchina.com create date: Nov 24, 2016
 *
 */
public class SecuritiesMarketInfo extends DbStatus {

    /**
     * 
     */
    private static final long serialVersionUID = -1909080654652535118L;
    private String name;
    private String symbol;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

}
