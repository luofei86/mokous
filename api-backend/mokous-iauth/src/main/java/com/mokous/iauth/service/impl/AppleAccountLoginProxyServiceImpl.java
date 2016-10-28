// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.iauth.service.impl;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import redis.clients.jedis.ShardedJedisPool;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mokous.core.cache.appleaccount.login.AccountInfo;
import com.mokous.core.cache.appleaccount.login.AuthActionResponse;
import com.mokous.core.cache.appleaccount.login.MachineInfo;
import com.mokous.core.dto.account.AppleAccount;
import com.mokous.core.dto.account.AppleAccountMachineInfo;
import com.mokous.core.service.account.AppleAccountMachineInfoService;
import com.mokous.core.service.account.AppleAccountService;
import com.mokous.core.utils.AppleAccountLoginUtils;
import com.mokous.core.utils.RedisCacheUtils;
import com.mokous.core.utils.ReturnDataHandleUtils;
import com.mokous.iauth.model.AppleIdAuthParameter;
import com.mokous.iauth.service.AppleAccountLoginProxyService;
import com.mokous.web.exception.ServiceException;
import com.mokous.web.model.ApiRespWrapper;
import com.mokous.web.model.ParametersHandle;
import com.mokous.web.model.ReturnDataHandle;
import com.mokous.web.utils.GsonUtils;
import com.mokous.web.utils.RemoteDataUtil;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月6日
 */
@Service("appleAccountLoginProxyService")
public class AppleAccountLoginProxyServiceImpl implements AppleAccountLoginProxyService {
    private static final Logger log = Logger.getLogger(AppleAccountLoginProxyServiceImpl.class);
    @Autowired
    private AppleAccountService appleAccountService;
    @Autowired
    private AppleAccountMachineInfoService appleAccountMachineInfoService;
    @Value("${ios.auth.apple.account.support.asynclogin}")
    private volatile boolean supportAsynclogin;
    @Autowired
    @Resource(name = "ios-auth-rediscacheshardedpool")
    private ShardedJedisPool shardedAppleAccountJedisPool;
    @Value("${ios.auth.authorizer.winexe.proxy.apple.account.login.api}")
    private String loginApi = "http://10.18.0.36:8080/ios-winexe-proxy/account/authorizer/login.json";

    private final String itunesAuthRespIdKey = "appleaccount:itunesauthresp:";
    private final String itunesAuthReqIdKey = "appleaccount:itunesauthreq:";
    private static final int APPLE_ACCOUNT_TIMEOUT = 35000;
    private static final long TRY_ACCOUNT_AUTH_LOCK_1MIN_MILLIS = 1000 * 60;
    private static final int ITUNES_AUTH_RESP_EXPIRED_TIME_2MIN = 120;
    private static final int ITUNES_AUTH_REQ_KEY_EXPIRED_TIME_3MIN = 180;

    private static final int NO_AUTH = 0;
    private static final int LOGIN_SUCCESS = 0;

    @Override
    public boolean verifyAppleAccount(String email, String passwd, String uid, Integer accountId, boolean appleSession,
            boolean needConfirmTerm) throws ServiceException {
        ApiRespWrapper<AccountInfo> accountInfoResp = login(email, passwd, uid, accountId, true, true, appleSession,
                needConfirmTerm);
        return accountInfoResp != null && accountInfoResp.getData() != null
                && !StringUtils.isEmpty(accountInfoResp.getData().getxDsid());
    }



    @Override
    public ApiRespWrapper<AccountInfo> login(String email, String passwd, String uid, Integer accountId,
            boolean refresh, boolean needAuth, boolean appleSeession, boolean needConfirmTerm) throws ServiceException {
        AppleAccount g = new AppleAccount();
        g.setEmail(email);
        g.setUid(uid);
        appleAccountService.addOrIgnoreData(g);
        g = appleAccountService.getAppleAccountByEmail(email);
        AppleAccountMachineInfo appleAccountMachineInfo = appleAccountMachineInfoService.getDirectFromDb(g.getId());
        String machine = "";
        // 不再进行机器验证了
        int auth = NO_AUTH;
        if (appleAccountMachineInfo != null) {
            machine = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(appleAccountMachineInfo);
        }
        // 2.2.46743版本号之后的的客户端的登录方式变化了
        AppleIdAuthParameter param = buildAppleIdAuthParameter(email, passwd, machine, auth, appleSeession);
        ApiRespWrapper<AuthActionResponse> itunesAuthResp = null;
        // 登录时间有的须要较长时间，此时客户端可能已经取消了此次请求，但是后台登录服务仍在尝试登录，如果后台登录服务成功后，会往cache会进行数据写入
        // 对于此块缓存的处理，由调用者负责
        if (supportAsynclogin) {
            itunesAuthResp = AppleAccountLoginUtils.getAppleAccountAsyncLoginResp(email, appleSeession,
                    shardedAppleAccountJedisPool);
        }
        log.info("Find the ituens auth reps is null:" + (itunesAuthResp == null));
        if (itunesAuthResp == null) {
            log.info("Login apple itunes.Email:" + email);
            itunesAuthResp = login(email, param, refresh);
            log.info("Finish login apple itunes.Email:" + email + ", Resp:" + itunesAuthResp);
            // confirm term 搞定由于苹果需要同意协议带来的无法下载的问题
            if (needConfirmTerm && itunesAuthResp != null && itunesAuthResp.getData() != null
                    && itunesAuthResp.getData().getAccountInfo() != null) {
                try {
                    log.info("Start confirm apple term.");
                    AppleAccountLoginUtils.confirmTerm(email, passwd, itunesAuthResp.getData().getAccountInfo());
                } catch (Exception ignore) {
                    String errmsg = "Confirm apple term failed. Errmsg:" + ignore.getMessage() + ", Email:" + email
                            + ", passwd" + passwd;
                    log.error(errmsg, ignore);
                }
            }
        }
        int loginStatus = 0;
        AccountInfo accountInfo = null;
        String message = null;
        if (itunesAuthResp == null) {
            loginStatus = AppleAccount.LOGIN_STATUS_ERROR_UNKNOWN;
        } else {
            loginStatus = itunesAuthResp.getStatus();
            message = itunesAuthResp.getMessage();
            if (itunesAuthResp.getData() != null) {
                accountInfo = itunesAuthResp.getData().getAccountInfo();
                if (itunesAuthResp.getData().getMachineInfo() != null && appleAccountMachineInfo == null) {
                    MachineInfo machineInfo = itunesAuthResp.getData().getMachineInfo();
                    appleAccountMachineInfo = new AppleAccountMachineInfo();
                    appleAccountMachineInfo.setId(g.getId());
                    appleAccountMachineInfo.setBiosInfo(machineInfo.getBiosInfo());
                    appleAccountMachineInfo.setComputerName(machineInfo.getComputerName());
                    appleAccountMachineInfo.setHwProfile(machineInfo.getHwProfile());
                    appleAccountMachineInfo.setMacAddress(machineInfo.getMacAddress());
                    appleAccountMachineInfo.setMachineGuid(machineInfo.getMachineGuid());
                    appleAccountMachineInfo.setProcessorName(machineInfo.getProcessorName());
                    appleAccountMachineInfo.setProductId(machineInfo.getProductId());
                    appleAccountMachineInfo.setVolumeSerialNumber(machineInfo.getVolumeSerialNumber());
                    appleAccountMachineInfo.setAuthTime(new Date());
                    appleAccountMachineInfoService.addData(appleAccountMachineInfo);
                }
            }
        }
        g.setLoginStatus(loginStatus);
        appleAccountService.afterLogin(g, accountId, accountId != null);

        return itunesAuthResp == null ? null : new ApiRespWrapper<AccountInfo>(loginStatus, message, accountInfo);
    }

    private ApiRespWrapper<AuthActionResponse> login(String email, AppleIdAuthParameter param, boolean refresh) {
        ApiRespWrapper<AuthActionResponse> itunesAuthResp = null;
        String accountAuthRequestLockKey = buildAuthReqLockKey(email);
        try {
            if (tryLockAccuntAuthRequest(accountAuthRequestLockKey, TRY_ACCOUNT_AUTH_LOCK_1MIN_MILLIS)) {
                try {
                    AuthActionResponse itunesAuthRespCacheData = null;
                    String respKey = buildItunesAuthRespKey(param);
                    if (refresh) {
                        RedisCacheUtils.del(respKey, shardedAppleAccountJedisPool);
                    } else {
                        itunesAuthRespCacheData = getAuthRespFromCache(respKey);
                    }
                    if (itunesAuthRespCacheData == null) {
                        // 因为登录服务是有状态的，在header中带上email进行负载均衡
                        Map<String, String> headerMap = new HashMap<String, String>();
                        headerMap.put("email", email);
                        itunesAuthResp = RemoteDataUtil.get(loginApi, param, ParametersHandle.PS_HANDLE,
                                ReturnDataHandleUtils.APPLEIDAUTH_RD_HANDLE, false, APPLE_ACCOUNT_TIMEOUT, headerMap);
                        if (itunesAuthResp != null && itunesAuthResp.getStatus() == LOGIN_SUCCESS) {
                            putAuthRespToCache(respKey, itunesAuthResp.getData(), ITUNES_AUTH_RESP_EXPIRED_TIME_2MIN);
                        }
                    } else {
                        itunesAuthResp = new ApiRespWrapper<AuthActionResponse>(itunesAuthRespCacheData);
                    }
                } catch (Exception e) {
                    log.error("Auth account failed.Param:" + param + ", errMsg:" + e.getMessage(), e);
                } finally {
                    unlockAccuntAuthRequest(accountAuthRequestLockKey);
                }
            }
        } catch (Exception e) {
            log.error("Try lock account auth request key failed. Key" + accountAuthRequestLockKey, e);
        }
        return itunesAuthResp;
    }

    public static final ReturnDataHandle<ApiRespWrapper<String>> STRING_RD_HANDLE = new ReturnDataHandle<ApiRespWrapper<String>>() {

        @Override
        public ApiRespWrapper<String> handle(String value) throws Exception {
            if (StringUtils.isEmpty(value)) {
                return null;
            }

            Type type = new TypeToken<ApiRespWrapper<String>>() {}.getType();
            return GsonUtils.convert(value, type);
        }
    };

    private String buildItunesAuthRespKey(AppleIdAuthParameter param) {
        return this.itunesAuthRespIdKey + getMd5(param);
    }

    private AuthActionResponse getAuthRespFromCache(String key) {
        return RedisCacheUtils.getObject(key, shardedAppleAccountJedisPool);
    }

    private void putAuthRespToCache(String key, AuthActionResponse value, int expireSec) {
        RedisCacheUtils.setexObject(key, value, expireSec, shardedAppleAccountJedisPool);
    }

    private String buildAuthReqLockKey(String email) {
        return this.itunesAuthReqIdKey + email.toLowerCase();
    }

    private static String getMd5(AppleIdAuthParameter param) {
        String temp = DigestUtils.md5Hex(param.getEmail().toLowerCase()) + ":" + DigestUtils.md5Hex(param.getPasswd())
                + ":" + param.getAuth() + ":" + param.getAppleSession();
        return DigestUtils.md5Hex(temp);
    }

    private boolean tryLockAccuntAuthRequest(String key, long timeOutTimeMillis) throws InterruptedException {
        long startTm = System.currentTimeMillis();
        long endTm = 0;
        do {
            boolean putSuccess = RedisCacheUtils.setnxThenExpire(key, "1", ITUNES_AUTH_REQ_KEY_EXPIRED_TIME_3MIN,
                    shardedAppleAccountJedisPool);
            if (putSuccess) {
                return true;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw e;
            }
            endTm = System.currentTimeMillis();
        } while (timeOutTimeMillis == 0 || endTm - startTm < timeOutTimeMillis);
        return false;
    }

    private void unlockAccuntAuthRequest(String key) {
        RedisCacheUtils.del(key, shardedAppleAccountJedisPool);
    }

    private static AppleIdAuthParameter buildAppleIdAuthParameter(String email, String passwd, String machine,
            int auth, boolean appleSession) { // 2.2.46743版本号之后的的客户端的登录方式变化了
        AppleIdAuthParameter param = null;
        if (appleSession) {
            param = new AppleIdAuthParameter(email, passwd, machine, auth,
                    AppleIdAuthParameter.USE_NEW_LOGALGORITHM_APPLESESSION_VALUE);
        } else {
            param = new AppleIdAuthParameter(email, passwd, machine, auth, null);
        }
        return param;
    }

}
