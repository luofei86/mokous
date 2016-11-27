// Copyright 2015 www.refanqie.com Inc. All Rights Reserved.

package com.mokous.apple.core.dto.account;

import java.util.HashMap;
import java.util.Map;

import com.mokous.core.dto.StatusSerializable;

/**
 * @author luofei@refanqie.com (Your Name Here)
 *
 */
public class AppleAccount extends StatusSerializable {
    /**
     * 
     */
    private static final long serialVersionUID = 3236262199973771828L;
    public static final int SOURCE_ACCOUNT_BIND = 1;
    private int id;
    // uid
    private String uid;
    //
    private String email;
    private String name;
    // 帐号密码
    private String applePassword;
    // 绑定帐号
    private String securityEmail;
    // 邮箱密码
    private String emailPassword;
    // 密码问题
    private String security;
    private int source;
    // 绑定帐号
    private Integer bindAccountId;
    private int loginStatus = IOS_AUTH_INIT;

    public static final int LOGIN_STATUS_ERROR_UNKNOWN = -999;

    public static final int IOS_AUTH_INIT = -1;
    // 登陆成功
    public static final int IOS_AUTH_SUCCESS = 0;
    // 超时
    public static final int IOS_EXCEPTION_AUTH_TIME_OUT = 54000;
    // 授权失败
    public static final int IOS_EXCEPTION_AUTH_FAILED = 51000;
    // 密码或者账号错误
    public static final int IOS_EXCEPTION_AUTH_FAILED_ACCOUNT_WRONG = 51001;
    // 账号被锁
    public static final int IOS_EXCEPTION_AUTH_FAILED_ACCOUNT_DISABLE = 51002;
    // 内部异常，原因不太明确
    public static final int IOS_EXCEPTION_INTERNAL_FAILED = 52000;
    // 内部异常，计算EVP_ENCODE 出错
    public static final int IOS_EXCEPTION_INTERNAL_EVP_ENCODE_FAILED = 52001;
    // 计算X_APPLE_ACTION_SIGNATURE失败
    public static final int IOS_EXCEPTION_INTERNAL_X_APPLE_ACTION_SIGNATURE_FAILED = 52002;
    // ITunes版本不支持
    public static final int IOS_EXCEPTION_INTERNAL_ITUNES_NOT_SUPPORT_ACTION_SIGNATURE = 52003;
    // ITunes初始化失败，HTTPS读取初始化全局变量时，通常是因为代理失效
    public static final int IOS_EXCEPTION_INTERNAL_SAPSETUP_INITIALIZE_FAILED = 52005;

    public static final Map<String, String> LOGINSTATUS_MAP = new HashMap<String, String>() {
        /**
         * 
         */
        private static final long serialVersionUID = -2740886655955423071L;

        {
            put(String.valueOf(LOGIN_STATUS_ERROR_UNKNOWN), "登录异常");
            put(String.valueOf(IOS_AUTH_INIT), "等待登录");
            put(String.valueOf(IOS_AUTH_SUCCESS), "登录成功");
            put(String.valueOf(IOS_EXCEPTION_AUTH_TIME_OUT), "登录超时");
            put(String.valueOf(IOS_EXCEPTION_AUTH_FAILED), "授权失败");
            put(String.valueOf(IOS_EXCEPTION_AUTH_FAILED_ACCOUNT_WRONG), "密码或者账号错误");
            put(String.valueOf(IOS_EXCEPTION_AUTH_FAILED_ACCOUNT_DISABLE), "账号被锁");
            put(String.valueOf(IOS_EXCEPTION_INTERNAL_FAILED), "内部异常，原因不太明确");
            put(String.valueOf(IOS_EXCEPTION_INTERNAL_EVP_ENCODE_FAILED), "内部异常，计算EVP_ENCODE 出错");
            put(String.valueOf(IOS_EXCEPTION_INTERNAL_X_APPLE_ACTION_SIGNATURE_FAILED), "计算X_APPLE_ACTION_SIGNATURE失败");
            put(String.valueOf(IOS_EXCEPTION_INTERNAL_ITUNES_NOT_SUPPORT_ACTION_SIGNATURE), "ITunes版本不支持");
            put(String.valueOf(IOS_EXCEPTION_INTERNAL_SAPSETUP_INITIALIZE_FAILED),
                    "ITunes初始化失败，HTTPS读取初始化全局变量时，通常是因为代理失效");
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    public Integer getBindAccountId() {
        return bindAccountId;
    }

    public void setBindAccountId(Integer bindAccountId) {
        this.bindAccountId = bindAccountId;
    }

    public String getApplePassword() {
        return applePassword;
    }

    public void setApplePassword(String applePassword) {
        this.applePassword = applePassword;
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
    }

    public String getSecurityEmail() {
        return securityEmail;
    }

    public void setSecurityEmail(String setSecurityEmail) {
        this.securityEmail = setSecurityEmail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(int loginStatus) {
        this.loginStatus = loginStatus;
    }

    @Override
    public String toString() {
        return "AppleAccount [id=" + id + ", uid=" + uid + ", email=" + email + ", name=" + name + ", applePassword="
                + applePassword + ", securityEmail=" + securityEmail + ", emailPassword=" + emailPassword
                + ", security=" + security + ", source=" + source + ", bindAccountId=" + bindAccountId
                + ", loginStatus=" + loginStatus + "]";
    }

}
