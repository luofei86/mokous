// Copyright 2016 ios.appchina.com Inc. All Rights Reserved.

package com.mokous.stock.core.dto.user;

import com.mokous.core.dto.DbKey.PRIMARY_KEY;
import com.mokous.core.dto.DbKey.UNIQUE_KEY;
import com.mokous.core.dto.DbStatus;

/**
 * @author luofei@appchina.com create date: Nov 27, 2016
 *
 */
public class UserInfo extends DbStatus {

    /**
     * 
     */
    private static final long serialVersionUID = -8755851519159396392L;
    @PRIMARY_KEY
    private int id;
    @UNIQUE_KEY
    private String uid;
    private int osVersion;
    private int platform;
    private String idfa;
    private String idfv;
    private String resolution;
    private String clientVersion;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(int osVersion) {
        this.osVersion = osVersion;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public String getIdfa() {
        return idfa;
    }

    public void setIdfa(String idfa) {
        this.idfa = idfa;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    @Override
    public boolean isDel() {
        return false;
    }

    @Override
    public boolean isOk() {
        return true;
    }

    public String getIdfv() {
        return idfv;
    }

    public void setIdfv(String idfv) {
        this.idfv = idfv;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }
}
