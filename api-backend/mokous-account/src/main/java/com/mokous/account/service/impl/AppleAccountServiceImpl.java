// Copyright 2015 www.refanqie.com Inc. All Rights Reserved.

package com.mokous.account.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ShardedJedisPool;

import com.mokous.account.dao.AppleAccountDao;
import com.mokous.core.cache.model.CacheFilter.SizeCacheFilter;
import com.mokous.core.dao.CommonDao;
import com.mokous.core.dao.LoadDao;
import com.mokous.core.dto.account.AppleAccount;
import com.mokous.core.service.AsyncService;
import com.mokous.core.service.account.AppleAccountService;
import com.mokous.core.utils.RedisCacheUtils;
import com.mokous.web.exception.ServiceException;

/**
 * @author luofei@refanqie.com (Your Name Here)
 */
@Service("appleAccountService")
public class AppleAccountServiceImpl extends AppleAccountService {
    private static final Logger log = Logger.getLogger(AppleAccountServiceImpl.class);
    @Autowired
    @Resource(name = "appchina-account-redisdbpool")
    private JedisPool jedisDbPool;
    private String appleAccountIdsKey = "appleaccount:ids";
    private String accountIdBindAppleAccountIdKey = "accountid:bind:appleaccountid";
    private String accountIdEmailKey = "accountid:email:";
    @Autowired
    private AppleAccountDao appleAccountDao;
    // @Autowired
    // private AccountIdGenerator accountIdGenerator;
    @Autowired
    @Resource(name = "appchina-account-rediscacheshardedpool")
    private ShardedJedisPool shardedJedisPool;
    private final String appleAccountIdKey = "appleaccount:";
    private volatile boolean stop = false;
    @Autowired
    private AsyncService asyncService;

    // 此缓存与appchina-appleaccount服务的redis服务相同
    @Autowired
    @Resource(name = "appchina-account-rediscacheshardedpool")
    private ShardedJedisPool shardedAppleAccountJedisPool;

    @PreDestroy
    public void shutdown() {
        stop = true;
    }
//
//    @Override
//    public AppleAccount randomeAppleAccount(int accountId) throws ServiceException {
//        String ret = RedisCacheUtils.spop(appleAccountIdsKey, jedisDbPool);
//        if (ret == null) {
//            return null;
//        }
//        int id = Integer.parseInt(ret);
//        AppleAccount data = getData(id);
//        if (data == null) {
//            return null;
//        }
//        data.setBindAccountId(accountId);
//        try {
//            appleAccountDao.updateBindAccountId(data);
//        } catch (SQLException e) {
//            throw ServiceException.getSQLException(e);
//        }
//        afterModifyData(data);
//        afterAddData(data);
//        return data;
//    }
//
//    @Override
//    protected Logger getLogger() {
//        return log;
//    }
//
//    private String buildAccountIdBindAppleAccountIdKey(Integer bindAccountId) {
//        return this.accountIdBindAppleAccountIdKey + bindAccountId.toString();
//    }
//
//    private String buildAppleAccountIdEmailKey(String email) {
//        return this.accountIdEmailKey + email.toLowerCase();
//    }
//
//    @Override
//    protected LoadDao<AppleAccount> getLoadDao() {
//        return this.appleAccountDao;
//    }
//
//    @Override
//    protected boolean isStop() {
//        return stop;
//    }
//
//    @Override
//    public AppleAccount getAppleAccount(int bindAccountId) throws ServiceException {
//        String key = buildAccountIdBindAppleAccountIdKey(bindAccountId);
//        Integer bindAppleAccountId = RedisCacheUtils.getId(key, getJedisPool());
//        if (bindAppleAccountId == null || bindAppleAccountId.intValue() <= 0) {
//            return null;
//        }
//        return getData(bindAppleAccountId.intValue());
//    }
//
//    @Override
//    public List<AppleAccount> getAppleAccount(Integer source, Integer status, Boolean bind, int start, int size)
//            throws ServiceException {
//        List<AppleAccount> datas = null;
//        try {
//            datas = this.appleAccountDao.queryAppleAccount(source, status, bind, start, size);
//        } catch (SQLException e) {
//            throw ServiceException.getSQLException(e.getMessage());
//        }
//        return datas;
//    }
//
//    @Override
//    public long countAppleAccount(Integer source, Integer status, Boolean bind) throws ServiceException {
//        try {
//            return this.appleAccountDao.countAppleAccount(source, status, bind);
//        } catch (SQLException e) {
//            throw ServiceException.getSQLException(e.getMessage());
//        }
//    }
//
    @Override
    public AppleAccount getAppleAccountByEmail(String email) throws ServiceException {
        int id = getAppleAccountIdByEmail(email);
        if (id <= 0) {
            return null;
        }
        return getData(id);
    }
//
//    private int getAppleAccountIdByEmail(String email) {
//        String key = buildAppleAccountIdEmailKey(email);
//        Integer appleAccountId = RedisCacheUtils.getId(key, jedisDbPool);
//        return appleAccountId == null ? -1 : appleAccountId.intValue();
//    }
//
//
//    private void asyncModifyLoginStatus(final AppleAccount appleAccount) {
//        asyncService.async(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    appleAccountDao.updateLoginStatus(appleAccount);
//                } catch (SQLException e) {
//                    throw ServiceException.getSQLException(e.getMessage());
//                }
//            }
//        });
//    }
//
//    private void asyncModifyLoginStatusAndBindeAccountId(final AppleAccount appleAccount) {
//        asyncService.async(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    appleAccountDao.updateLoginStatusAndBindeAccountId(appleAccount);
//                } catch (SQLException e) {
//                    throw ServiceException.getSQLException(e.getMessage());
//                }
//            }
//        });
//    }
//
//    @Override
//    public AppleAccount generateAppleAccount(String email, String uid) throws ServiceException {
//        AppleAccount ret = getAppleAccountByEmail(email);
//        if (ret != null) {
//            if (StringUtils.isEmpty(uid) || StringUtils.equalsIgnoreCase(ret.getUid(), uid)) {
//                return ret;
//            } else {
//                ret.setUid(uid);
//                modifyUid(ret);
//                return ret;
//            }
//        }
//        ret = new AppleAccount();
//        ret.setEmail(email);
//        ret.setSource(AppleAccount.SOURCE_ACCOUNT_BIND);
//        ret.setBindAccountId(0);
//        ret.setUid(uid);
//        addData(ret);
//        return ret;
//    }
//
//    private void modifyUid(AppleAccount data) {
//        try {
//            appleAccountDao.updateAppleAccountUid(data);
//        } catch (SQLException e) {
//            throw ServiceException.getSQLException(e);
//        }
//    }
//
//    @Override
//    protected String buildSetKey(SizeCacheFilter filter) {
//        return appleAccountIdsKey;
//    }
//
//    @Override
//    protected JedisPool getJedisPool() {
//        return jedisDbPool;
//    }
//
//    @Override
//    protected SizeCacheFilter buildCacheFilter(AppleAccount value) {
//        return new SizeCacheFilter();
//    }
//
//    @Override
//    protected ShardedJedisPool getSharedJedisPool() {
//        return shardedAppleAccountJedisPool;
//    }
//
//    @Override
//    protected String buildDataInfoKey(int id) {
//        return this.appleAccountIdKey + id;
//    }
//
//    @Override
//    public CommonDao<AppleAccount> getCommonDao() {
//        return appleAccountDao;
//    }
//
//    @Override
//    protected boolean illegalValue(AppleAccount value) {
//        return value.isDel() || value.getBindAccountId() != null
//                || value.getSource() == AppleAccount.SOURCE_ACCOUNT_BIND;
//    }
//
//    @Override
//    protected void additionalPutToCacheDB(List<AppleAccount> values, long versionCode) {
//        Map<String, String> mailAppleAccountIdMap = new HashMap<String, String>();
//        Map<String, String> accountIdAppleAccountIdMap = new HashMap<String, String>();
//        List<String> accountIdAppleAccountIdKeys = new ArrayList<String>();
//        for (AppleAccount value : values) {
//            mailAppleAccountIdMap.put(buildAppleAccountIdEmailKey(value.getEmail()), String.valueOf(value.getId()));
//            if (value.getBindAccountId() != null) {
//                String key = buildAccountIdBindAppleAccountIdKey(value.getBindAccountId());
//                if (value.isDel()) {
//                    accountIdAppleAccountIdKeys.add(key);
//                } else {
//                    accountIdAppleAccountIdMap.put(key, String.valueOf(value.getId()));
//                }
//            }
//        }
//        RedisCacheUtils.mset(mailAppleAccountIdMap, getJedisPool());
//        RedisCacheUtils.mset(accountIdAppleAccountIdMap, getJedisPool());
//        RedisCacheUtils.del(accountIdAppleAccountIdKeys, getJedisPool());
//    }
//
//    @Override
//    protected AppleAccount beforToDb(AppleAccount g) {
//        int id = this.accountIdGenerator.generateAppleAccountId();
//        g.setId(id);
//        return g;
//    }
//
//    @Override
//    protected void afterModifyData(List<AppleAccount> gg) {
//        for (AppleAccount g : gg) {
//            if (g.getId() > 0) {
//                RedisCacheUtils.asyncDel(asyncService, buildDataInfoKey(g.getId()), shardedJedisPool);
//            }
//        }
//    }
//
//    @Override
//    protected void afterAddData(List<AppleAccount> gg) {
//        putToCacheDb(gg);
//    }
//
//    @Override
//    public void afterLogin(AppleAccount appleAccount, Integer accountId, boolean loginSuccess) {
//        if (loginSuccess && accountId != null && accountId.intValue() > 0) {
//            appleAccount.setBindAccountId(accountId);
//            asyncModifyLoginStatusAndBindeAccountId(appleAccount);
//        } else {
//            asyncModifyLoginStatus(appleAccount);
//        }
//    }
}
