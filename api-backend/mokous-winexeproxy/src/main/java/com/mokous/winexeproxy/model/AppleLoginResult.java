// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.winexeproxy.model;

import java.io.Serializable;


/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
public class AppleLoginResult implements Serializable {

    private static final long serialVersionUID = -5430779434435638852L;

    private String xDsid;
    private String xToken;
    private String creditDisplay;
    private String appleId;
    private String accountKind;

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

    public void setAppleId(String appleId) {
        this.appleId = appleId;
    }

    public String getAppleId() {
        return appleId;
    }

    public void setAccountKind(String accountKind) {
        this.accountKind = accountKind;
    }

    public String getAccountKind() {
        return accountKind;
    }


    @Override
    public String toString() {
        return "AppleLoginResult{" +
                "xDsid='" + xDsid + '\'' +
                ", xToken='" + xToken + '\'' +
                ", creditDisplay='" + creditDisplay + '\'' +
                ", appleId='" + appleId + '\'' +
                ", accountKind='" + accountKind + '\'' +
                '}';
    }
}
