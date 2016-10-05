// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.winexeproxy.action;


import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import redis.clients.jedis.ShardedJedisPool;

import com.mokous.core.cache.appleaccount.login.AccountInfo;
import com.mokous.core.cache.appleaccount.login.AuthActionResponse;
import com.mokous.core.cache.appleaccount.login.MachineInfo;
import com.mokous.core.service.AsyncService;
import com.mokous.web.action.IosBaseAction;
import com.mokous.web.exception.ServiceException;
import com.mokous.web.model.ApiRespWrapper;
import com.mokous.winexeproxy.service.AppleAccountAuthorizedService;
import com.mokous.winexeproxy.service.AppleAccountLoginService;
import com.mokous.winexeproxy.service.MachineInfoService;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
@Controller
@RequestMapping("/account/authorizer/*")
public class AuthorizedAction extends IosBaseAction {
    private static final Logger log = Logger.getLogger(AuthorizedAction.class);
    @Autowired
    private AppleAccountAuthorizedService appleAccountAuthorizedService;
    @Autowired
    private MachineInfoService machineInfoService;
    @Autowired
    private AppleAccountLoginService appleAccountLoginService;
    @Resource(name = "ios-winexe-proxy-rediscacheshardedpool")
    private ShardedJedisPool shardedJedisPool;
    @Autowired
    private AsyncService asyncService;

    @RequestMapping(value = "/login.json")
    @ResponseBody
    protected ApiRespWrapper<AuthActionResponse> login(String email, String passwd, String machine, String ip,
            Integer port, Integer auth, Integer appleSession) {
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(passwd)) {
            return new ApiRespWrapper<AuthActionResponse>(-1, "Apple account is not or apple account pwd is illegal.",
                    null);
        }
        boolean createSeesion = appleSession != null && appleSession == 1;
        MachineInfo machineInfo = machineInfoService.generateMachineInfo(machine);
        AccountInfo accountInfo = null;
        int status = -1;
        String message = "";
        try {
            accountInfo = appleAccountLoginService.login(email, passwd, ip, port, machineInfo, createSeesion);
            if (accountInfo != null) {
                status = 0;
            }
        } catch (ServiceException e) {
            message = e.getMessage();
            status = e.getErrorCode();
        } catch (Exception e) {
            message = e.getMessage();
        }
        ApiRespWrapper<AuthActionResponse> authActionResponseApiRespWrapper = new ApiRespWrapper<AuthActionResponse>(
                status, message, new AuthActionResponse(accountInfo, machineInfo));
        if (accountInfo != null) {
            AppleAccountLoginUtils.asyncPut(email, createSeesion, shardedJedisPool, asyncService,
                    authActionResponseApiRespWrapper);
        }
        return authActionResponseApiRespWrapper;
    }

    @RequestMapping(value = "/request.json")
    @ResponseBody
    protected ApiRespWrapper<String> request(String authJson, String appleId, String imei, String udid, String guid,
            String ikma, String ikmb) {
        int retStatus = 0;
        String errMsg = "";
        String result = "";
        try {
            result = appleAccountAuthorizedService.requestAuthroizedInfo(appleId, authJson, ikma, ikmb);
        } catch (Exception e) {
            log.error("Request authorized info failed. guid:" + guid + ", imei:" + imei + ", unique device id:" + udid
                    + ", AppleId:" + appleId + ",Errmsg:" + e.getMessage(), e);
            retStatus = -1;
            errMsg = e.getMessage();
        }
        return new ApiRespWrapper<String>(retStatus, errMsg, result);
    }

    @RequestMapping(value = "/pc/info.json")
    @ResponseBody
    protected ApiRespWrapper<MachineInfo> pcInfo() {
        MachineInfo machineInfo = machineInfoService.generateLocalMachineInfo();
        return new ApiRespWrapper<MachineInfo>(0, "", machineInfo);
    }

    @RequestMapping(value = "/pc/byexe.json")
    @ResponseBody
    protected ApiRespWrapper<Boolean> authorizerPcExe(String appleId, String pwd, String ip, Integer port,
            Boolean createSession) {
        boolean ret = false;
        int retStatus = 0;
        String errMsg = "";
        try {
            port = port == null ? -1 : port;
            createSession = createSession == null ? false : createSession.booleanValue();
            ret = appleAccountAuthorizedService.authPcByExe(appleId, pwd, ip, port, createSession);
        } catch (ServiceException e) {
            log.error("Authorized the apple id to pc failed. AppleId:" + appleId + ",Errmsg:" + e.getMessage(), e);
            retStatus = e.getErrorCode();
            errMsg = e.getMessage();
        } catch (Exception e) {
            throw ServiceException.getInternalException(e.getMessage());
        }
        return new ApiRespWrapper<Boolean>(retStatus, errMsg, ret);
    }
}
