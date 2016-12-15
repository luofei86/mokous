// Copyright 2016 ios.appchina.com Inc. All Rights Reserved.

package com.mokous.stock.info.dao.impl;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.mokous.core.utils.SQLUtils;
import com.mokous.stock.core.dto.info.StockInfo;
import com.mokous.stock.info.dao.StockInfoDao;

/**
 * @author luofei@appchina.com create date: Nov 27, 2016
 *
 */
@Repository("stockInfoDao")
public class StockInfoDaoImpl extends StockInfoDao {
    @Resource(name = "mokous-stockinfo-sql-client")
    private SqlMapClient sqlMapClient;

    public SqlMapClient getSqlMapClient() {
        return sqlMapClient;
    }

    @Override
    public List<StockInfo> queryListByLike(StockInfo g, int start, int size) throws SQLException {
        return SQLUtils.queryList(getSqlMapClient(), "queryStockInfoListByLike", g, start, size);
    }

}
