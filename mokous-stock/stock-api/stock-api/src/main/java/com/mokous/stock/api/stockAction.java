// Copyright 2016 ios.appchina.com Inc. All Rights Reserved.

package com.mokous.stock.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mokous.stock.core.dto.info.StockInfo;
import com.mokous.web.action.BaseAction;
import com.mokous.web.model.ApiRespWrapper;
import com.mokous.web.model.ListWrapResp;

/**
 * 股价提醒应用API
 * 
 * @author luofei@appchina.com create date: Nov 23, 2016
 *
 */
@Controller
@RequestMapping("/stock/*")
public class stockAction extends BaseAction {
    @RequestMapping(value = "/search.json")
    @ResponseBody
    protected ApiRespWrapper<ListWrapResp<StockInfo>> searchStockInfo(String keyword) {
        int start = 0;
        int size = 10;
        // if (StringUtils.isEmpty(type)) {
        // type = AppleShareAccountType.OVERSEA.getType();
        // }
        // filter.setType(type);
        // filter.setStart(start);
        // filter.setSize(size);
        // return new
        // ApiRespWrapper<ListWrapResp<AppleShareAccountInfo>>(accountWrapService.listShareAccunt(filter,
        // AppLanguage.instance(language)));
        //
        return null;
    }

    @RequestMapping(value = "/notice/add.json")
    @ResponseBody
    protected ApiRespWrapper<ListWrapResp<StockInfo>> addNotice(String stockCode) {
        int start = 0;
        int size = 10;
        // if (StringUtils.isEmpty(type)) {
        // type = AppleShareAccountType.OVERSEA.getType();
        // }
        // filter.setType(type);
        // filter.setStart(start);
        // filter.setSize(size);
        // return new
        // ApiRespWrapper<ListWrapResp<AppleShareAccountInfo>>(accountWrapService.listShareAccunt(filter,
        // AppLanguage.instance(language)));
        //
        return null;
    }

}
