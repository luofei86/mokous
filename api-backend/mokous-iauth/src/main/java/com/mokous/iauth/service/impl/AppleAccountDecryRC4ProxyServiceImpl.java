// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.iauth.service.impl;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import redis.clients.jedis.ShardedJedisPool;

import com.mokous.core.cache.appleaccount.login.AccountInfo;
import com.mokous.core.cache.appleaccount.login.AuthActionResponse;
import com.mokous.core.service.AsyncService;
import com.mokous.core.utils.RedisCacheUtils;
import com.mokous.iauth.model.AppleIdAuthParameter;
import com.mokous.iauth.service.AppleAccountDecryRC4ProxyService;
import com.mokous.iauth.service.AppleAccountLoginPureProxyService;
import com.mokous.iauth.service.utils.RC4Utils;
import com.mokous.web.exception.ServiceException;
import com.mokous.web.model.ApiRespWrapper;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月6日
 */
@Service("appleAccountDecryRC4ProxyService")
public class AppleAccountDecryRC4ProxyServiceImpl implements AppleAccountDecryRC4ProxyService {
    @Autowired
    @Resource(name = "ios-auth-rediscacheshardedpool")
    private ShardedJedisPool shardedAppleAccountJedisPool;
    @Autowired
    private AppleAccountLoginPureProxyService appleAccountLoginPureProxyService;
    @Value("${ios.auth.apple.account.decry.key}")
    private String rc4Key = "secretkey";
    private String decryRc4LoginRespCacheInfoKey = "pc:apple:login:resp";
    @Autowired
    private AsyncService asyncService;


    @Override
    public ApiRespWrapper<AccountInfo> login(final String email, final String passwd, final String guid,
            boolean readFromCache) throws ServiceException {
        ApiRespWrapper<AccountInfo> resp = null;
        String respCacheKey = buildRespCacheKey(email, passwd);
        if (readFromCache) {
            resp = RedisCacheUtils.getObject(respCacheKey, shardedAppleAccountJedisPool);
        }
        if (resp == null) {
            AppleIdAuthParameter param = new AppleIdAuthParameter(email, passwd, null,
                    AppleIdAuthParameter.AUTH_NO_NEED, null);
            ApiRespWrapper<AuthActionResponse> loginResp = appleAccountLoginPureProxyService.login(param);
            if (loginResp != null && loginResp.getStatus() == AppleAccountLoginPureProxyService.LOGIN_SUCCESS) {
                if (loginResp.getData() != null) {
                    AccountInfo accountInfo = loginResp.getData().getAccountInfo();
                    int status = loginResp.getStatus();
                    String message = loginResp.getMessage();
                    resp = new ApiRespWrapper<AccountInfo>(status, message, accountInfo);
                    RedisCacheUtils.setObject(respCacheKey, resp, shardedAppleAccountJedisPool);
                }
            }
        }
        return resp;
    }

    private String buildRespCacheKey(String email, String passwd) {
        return RedisCacheUtils.buildKey(decryRc4LoginRespCacheInfoKey, email, passwd);
    }

    @Override
    public String decryAndDecode(String data) {
        try {
            return RC4Utils.decry_RC4(new String(Base64.decodeBase64(data)), this.rc4Key);
        } catch (Exception e) {
            throw ServiceException.getInternalException("Decry failed.");
        }
    }

}
