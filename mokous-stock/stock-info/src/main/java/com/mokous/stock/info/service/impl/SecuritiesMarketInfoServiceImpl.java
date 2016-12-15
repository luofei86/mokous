// Copyright 2016 ios.appchina.com Inc. All Rights Reserved.

package com.mokous.stock.info.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mokous.core.dao.CommonDao;
import com.mokous.stock.core.dto.info.SecuritiesMarketInfo;
import com.mokous.stock.core.service.info.SecuritiesMarketInfoService;
import com.mokous.stock.info.dao.SecuritiesMarketInfoDao;

/**
 * @author luofei@appchina.com create date: Nov 27, 2016
 *
 */
@Service("securitiesMarketInfoService")
public class SecuritiesMarketInfoServiceImpl extends SecuritiesMarketInfoService {
    @Autowired
    private SecuritiesMarketInfoDao securitiesMarketInfoDao;

    @Override
    public CommonDao<SecuritiesMarketInfo> getCommonDao() {
        return securitiesMarketInfoDao;
    }

}
