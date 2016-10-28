// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.iauth.action;


import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mokous.core.cache.appleaccount.login.AccountInfo;
import com.mokous.core.service.AsyncService;
import com.mokous.core.utils.AppleAccountLoginUtils;
import com.mokous.core.utils.ParameterUtils;
import com.mokous.iauth.service.AppleAccountDecryRC4ProxyService;
import com.mokous.iauth.service.AppleAccountLoginProxyService;
import com.mokous.web.action.IosBaseAction;
import com.mokous.web.model.ApiRespWrapper;


/**
 * @author luofei (Your Name Here)
 * @date 2016年10月6日
 */
@Controller
@RequestMapping("/account/*")
public class AccountAction extends IosBaseAction {
    private static final Logger log = Logger.getLogger(AccountAction.class);
    // 登陆成功
    protected static final int IOS_AUTH_SUCCESS = 0;
    // 超时
    protected static final int IOS_EXCEPTION_AUTH_TIME_OUT = 54000;
    // 授权失败
    protected static final int IOS_EXCEPTION_AUTH_FAILED = 51000;
    // 密码或者账号错误
    protected static final int IOS_EXCEPTION_AUTH_FAILED_ACCOUNT_WRONG = 51001;
    // 账号或密码为空
    public static final int ERR_CODE_ACCOUNT_OR_PASSWD_IS_EMPTY = 9002;
    // 账号被锁
    protected static final int IOS_EXCEPTION_AUTH_FAILED_ACCOUNT_DISABLE = 51002;
    // 内部异常，原因不太明确
    protected static final int IOS_EXCEPTION_INTERNAL_FAILED = 52000;
    // 内部异常，计算EVP_ENCODE 出错
    protected static final int IOS_EXCEPTION_INTERNAL_EVP_ENCODE_FAILED = 52001;
    // 计算X_APPLE_ACTION_SIGNATURE失败
    protected static final int IOS_EXCEPTION_INTERNAL_X_APPLE_ACTION_SIGNATURE_FAILED = 52002;
    // ITunes版本不支持
    protected static final int IOS_EXCEPTION_INTERNAL_ITUNES_NOT_SUPPORT_ACTION_SIGNATURE = 52003;
    // ITunes初始化失败，HTTPS读取初始化全局变量时，通常是因为代理失效
    protected static final int IOS_EXCEPTION_INTERNAL_SAPSETUP_INITIALIZE_FAILED = 52005;
    // @Autowired
    // private AppleAuthorizerAccountService appleAuthorizerAccountService;
    @Autowired
    private AppleAccountLoginProxyService appleAccountLoginProxyService;
    @Autowired
    private AppleAccountDecryRC4ProxyService appleAccountDecryRC4ProxyService;
    @Autowired
    private AsyncService asyncService;

    @RequestMapping(value = "/appleverify.json")
    @ResponseBody
    protected ApiRespWrapper<Boolean> appleverify(String email, String passwd, String uid, String clientVersion,
            Integer accountId, Integer session) {
        String errMsg = checkUserMail(email, passwd);
        if (!StringUtils.isEmpty(errMsg)) {
            return new ApiRespWrapper<Boolean>(-1, errMsg, false);
        }
        boolean needAppleSession = AppleAccountLoginUtils.needAppleSession(session, clientVersion);
        boolean needConfirmTerm = AppleAccountLoginUtils.needConfirmTerm(clientVersion);
        boolean ret = this.appleAccountLoginProxyService.verifyAppleAccount(email, passwd, uid, accountId,
                needAppleSession, needConfirmTerm);
        errMsg = ret ? "已绑定." : "邮箱或密码错误.";
        int status = ret ? 0 : -2;
        return new ApiRespWrapper<Boolean>(status, errMsg, ret);
    }

    @RequestMapping(value = "/encry/appleauth.json")
    @ResponseBody
    protected ApiRespWrapper<AccountInfo> encryAppleauth(String email, String passwd, Integer expire, String guid,
            String clientId, String pcsuiteVersion, String channel) {
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(passwd)) {
            return new ApiRespWrapper<AccountInfo>(-1, "Illegal parameter.", null);
        }
        String pwd = appleAccountDecryRC4ProxyService.decryAndDecode(passwd);
        if (StringUtils.isEmpty(pwd)) {
            return new ApiRespWrapper<AccountInfo>(-1, "Illegal parameter.", null);
        }
        boolean readFromCache = expire == null || expire.intValue() == 0;
        ApiRespWrapper<AccountInfo> value = appleAccountDecryRC4ProxyService.login(email, pwd, guid, readFromCache);
        return handleLoginResp(email, value);
    }

    /**
     * only will return error code 54000,51001, 51002
     * 
     * @param email
     * @param passwd
     * @param osName
     * @return
     */
    @RequestMapping(value = "/appleauth.json")
    @ResponseBody
    protected ApiRespWrapper<AccountInfo> appleauth(String email, String uid, Integer accountId, String clientVersion,
            String passwd, String osName, Integer refresh, Integer auth, Integer session) {
        String errMsg = checkUserMail(email, passwd);
        int status = -1;
        if (!StringUtils.isEmpty(errMsg)) {
            return new ApiRespWrapper<AccountInfo>(status, errMsg, null);
        }
        boolean needRefresh = AppleAccountLoginUtils.needRefresh(refresh);
        boolean needAuth = AppleAccountLoginUtils.needAuth(auth);
        boolean needAppleSession = AppleAccountLoginUtils.needAppleSession(session, clientVersion);
        boolean needConfirmTerm = AppleAccountLoginUtils.needConfirmTerm(clientVersion);
        ApiRespWrapper<AccountInfo> value = this.appleAccountLoginProxyService.login(email, passwd, uid, accountId,
                needRefresh, needAuth, needAppleSession, needConfirmTerm);
        return handleLoginResp(email, value);
    }

    private ApiRespWrapper<AccountInfo> handleLoginResp(String email, ApiRespWrapper<AccountInfo> value) {
        int status = 0;
        String errMsg = "";
        if (value != null) {
            status = value.getStatus();
            switch (status) {
                case IOS_EXCEPTION_AUTH_TIME_OUT:
                    errMsg = "超时";
                    log.warn("auth apple account failed.Errmsg:" + errMsg + ", Status:" + status);
                    break;
                case IOS_EXCEPTION_AUTH_FAILED_ACCOUNT_WRONG:
                case ERR_CODE_ACCOUNT_OR_PASSWD_IS_EMPTY:
                    errMsg = "密码或者账号错误";
                    log.warn("auth apple account failed.Errmsg:" + errMsg + ", Status:" + status);
                    break;
                case IOS_EXCEPTION_AUTH_FAILED_ACCOUNT_DISABLE:
                    errMsg = "账号被锁";
                    log.warn("auth apple account failed.Errmsg:" + errMsg + ", Status:" + status);
                    break;
                case IOS_AUTH_SUCCESS:
                    errMsg = "登陆成功";
                    break;
                default:
                    errMsg = "auth apple account failed.Email:" + email + ",Errmsg:" + (value.getMessage())
                            + ", Status:" + (value.getStatus());
                    log.warn(errMsg);
                    status = -2;
                    errMsg = "无法验证此用户信息";
                    break;
            }
            return new ApiRespWrapper<AccountInfo>(status, errMsg, value.getData());
        }
        errMsg = "auth apple account failed.Email:" + email + ",Errmsg:" + ("本地网络异常") + ", Status:" + ("-2");
        log.warn(errMsg);
        return new ApiRespWrapper<AccountInfo>(-2, "无法验证此用户信息", null);
    }

    private String checkUserMail(String email, String userpwd) {
        if (StringUtils.isEmpty(userpwd)) {
            return "用户密码不为能空";
        }
        if (StringUtils.isEmpty(email) || !ParameterUtils.isMail(email)) {
            return "邮箱不能为空或邮箱格式不正确.";
        }
        return null;
    }

}
