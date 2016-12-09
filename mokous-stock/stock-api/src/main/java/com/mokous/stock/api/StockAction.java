// Copyright 2016 ios.appchina.com Inc. All Rights Reserved.

package com.mokous.stock.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mokous.core.dto.DbStatus;
import com.mokous.stock.core.dto.info.StockInfo;
import com.mokous.stock.core.dto.notice.UserNoticeInfo;
import com.mokous.stock.core.service.info.StockInfoService;
import com.mokous.stock.core.service.notice.UserNoticeInfoService;
import com.mokous.stock.core.service.user.UserInfoService;
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
public class StockAction extends BaseAction {
    @Autowired
    private UserNoticeInfoService userNoticeInfoService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private StockInfoService stockInfoService;

    @RequestMapping(value = "/search.json")
    @ResponseBody
    protected ApiRespWrapper<ListWrapResp<StockInfo>> searchStockInfo(String keyword) {
        List<StockInfo> values = stockInfoService.getByLike(keyword);
        return new ApiRespWrapper<ListWrapResp<StockInfo>>(new ListWrapResp<StockInfo>(values));
    }

    @RequestMapping(value = "/notice/add.json")
    @ResponseBody
    protected ApiRespWrapper<ListWrapResp<StockInfo>> addNotice(String uid, int stockId, Integer id, Integer noticeId,
            Float noticeThreshold, Integer status) {
        int userId = userInfoService.getUserId(uid);
        if (userId <= 0) {
            // return 404;
        }

        int start = 0;
        int size = 10;
        UserNoticeInfo g = new UserNoticeInfo();
        g.setUserId(userId);
        g.setStockId(stockId);
        g.setNoticeId(noticeId);
        g.setNoticeThreshold(noticeThreshold);
        g.setId(id);
        if (status != null && status.intValue() == DbStatus.STATUS_DEL) {

        }
        userNoticeInfoService.addData(g);
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
