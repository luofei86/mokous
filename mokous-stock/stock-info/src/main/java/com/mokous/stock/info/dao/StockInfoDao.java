// Copyright 2016 ios.appchina.com Inc. All Rights Reserved.

package com.mokous.stock.info.dao;

import java.sql.SQLException;
import java.util.List;

import com.mokous.core.dao.AbsCommonDao;
import com.mokous.stock.core.dto.info.StockInfo;

/**
 * @author luofei@appchina.com create date: Nov 27, 2016
 *
 */
public abstract class StockInfoDao extends AbsCommonDao<StockInfo> {

    public abstract List<StockInfo> queryListByLike(StockInfo g, int start, int size) throws SQLException;

}
