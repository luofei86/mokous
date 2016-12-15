// Copyright 2016 ios.appchina.com Inc. All Rights Reserved.

package com.mokous.stock.info.dao.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.mokous.stock.info.dao.SecuritiesMarketInfoDao;

/**
 * @author luofei@appchina.com create date: Nov 27, 2016
 *
 */
@Repository("securitiesMarketInfoDao")
public class SecuritiesMarketInfoDaoImpl extends SecuritiesMarketInfoDao {
    @Resource(name = "mokous-stockinfo-sql-client")
    private SqlMapClient sqlMapClient;

    public SqlMapClient getSqlMapClient() {
        return sqlMapClient;
    }

}
