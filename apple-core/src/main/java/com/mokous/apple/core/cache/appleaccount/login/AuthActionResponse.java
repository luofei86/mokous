// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.apple.core.cache.appleaccount.login;

import java.io.Serializable;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
public class AuthActionResponse implements Serializable {

    public AuthActionResponse(AccountInfo accountInfo, MachineInfo machineInfo) {
        super();
        this.accountInfo = accountInfo;
        this.machineInfo = machineInfo;
    }

    private static final long serialVersionUID = -701813704073748134L;

    private AccountInfo accountInfo;
    private MachineInfo machineInfo;

    public AccountInfo getAccountInfo() {
        return accountInfo;
    }

    public void setAccountInfo(AccountInfo accountInfo) {
        this.accountInfo = accountInfo;
    }

    public MachineInfo getMachineInfo() {
        return machineInfo;
    }

    @Override
    public String toString() {
        return "AuthActionResponse [accountInfo=" + accountInfo + ", machineInfo=" + machineInfo + "]";
    }

    public void setMachineInfo(MachineInfo machineInfo) {
        this.machineInfo = machineInfo;
    }
}
