// Copyright 2016 ios.appchina.com Inc. All Rights Reserved.

package com.mokous.stock.info.service.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mokous.core.dao.CommonDao;
import com.mokous.stock.core.dto.info.StockInfo;
import com.mokous.stock.core.service.info.StockInfoService;
import com.mokous.stock.info.dao.StockInfoDao;
import com.mokous.web.exception.ServiceException;

/**
 * @author luofei@appchina.com create date: Nov 27, 2016
 *
 */
@Service("stockInfoService")
public class StockInfoServiceImpl extends StockInfoService {
    @Autowired
    private StockInfoDao stockInfoDao;

    @Override
    public CommonDao<StockInfo> getCommonDao() {
        return stockInfoDao;
    }

    @Override
    public List<StockInfo> getByLike(String name) throws ServiceException {
        StockInfo g = new StockInfo();
        g.setName(name);
        g.setNamePy(name);
        g.setCode(name);
        try {
            return stockInfoDao.queryListByLike(g, 0, 20);
        } catch (SQLException e) {
            throw ServiceException.getSQLException(e);
        }
    }
}
