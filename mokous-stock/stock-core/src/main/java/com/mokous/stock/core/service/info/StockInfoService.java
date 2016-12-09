// Copyright 2016 ios.appchina.com Inc. All Rights Reserved.

package com.mokous.stock.core.service.info;

import java.util.List;

import com.mokous.core.service.AbsCommonDataService;
import com.mokous.stock.core.dto.info.StockInfo;
import com.mokous.web.exception.ServiceException;

/**
 * 股票信息
 * 
 * @author luofei@appchina.com create date: Nov 25, 2016
 *
 */
public abstract class StockInfoService extends AbsCommonDataService<StockInfo> {
    public abstract List<StockInfo> getByLike(String name) throws ServiceException;
}
