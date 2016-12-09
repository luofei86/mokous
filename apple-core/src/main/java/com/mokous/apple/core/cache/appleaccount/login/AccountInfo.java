// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.apple.core.cache.appleaccount.login;

import com.mokous.core.dto.StatusSerializable;


/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
public class AccountInfo extends StatusSerializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1306300511986576844L;
    public static final int AUTH_OK = 1;
    public static final int AUTH_NO_OR_FAILED = 0;
    public String cookie;
    public String xAppleActionSignature;
    public String xAppleStoreFront;
    public String xDsid;
    public String xToken;
    public String creditDisplay;
    public String guid;
    public String kbsync;
    public String machineName;
    public int auth;
    public String authMsg;

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getxAppleActionSignature() {
        return xAppleActionSignature;
    }

    public void setxAppleActionSignature(String xAppleActionSignature) {
        this.xAppleActionSignature = xAppleActionSignature;
    }

    public String getxAppleStoreFront() {
        return xAppleStoreFront;
    }

    public void setxAppleStoreFront(String xAppleStoreFront) {
        this.xAppleStoreFront = xAppleStoreFront;
    }

    public String getxDsid() {
        return xDsid;
    }

    public void setxDsid(String xDsid) {
        this.xDsid = xDsid;
    }

    public String getxToken() {
        return xToken;
    }

    public void setxToken(String xToken) {
        this.xToken = xToken;
    }

    public String getCreditDisplay() {
        return creditDisplay;
    }

    public void setCreditDisplay(String creditDisplay) {
        this.creditDisplay = creditDisplay;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getKbsync() {
        return kbsync;
    }

    public void setKbsync(String kbsync) {
        this.kbsync = kbsync;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public int getAuth() {
        return auth;
    }

    public void setAuth(int auth) {
        this.auth = auth;
    }

    public String getAuthMsg() {
        return authMsg;
    }

    public void setAuthMsg(String authMsg) {
        this.authMsg = authMsg;
    }

    @Override
    public String toString() {
        return "AccountInfo [cookie=" + cookie + ", xAppleActionSignature=" + xAppleActionSignature
                + ", xAppleStoreFront=" + xAppleStoreFront + ", xDsid=" + xDsid + ", xToken=" + xToken
                + ", creditDisplay=" + creditDisplay + ", guid=" + guid + ", kbsync=" + kbsync + ", machineName="
                + machineName + ", auth=" + auth + ", authMsg=" + authMsg + "]";
    }
}
