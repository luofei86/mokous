// Copyright 2015 www.refanqie.com Inc. All Rights Reserved.

package com.mokous.core.dto.account;

import java.io.Serializable;
import java.util.Date;

import com.google.gson.annotations.Expose;
import com.mokous.core.dto.DbStatus;

/**
 * @author luofei@refanqie.com (Your Name Here)
 */
public class AppleAccountMachineInfo extends DbStatus implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -5904993203423358190L;
    public static final int AUTH_MACHINE_INIT = -1;
    public static final int AUTH_MACHINE_ALREADY_FIVE = 3002;
    public static final int AUTH_MACHINE_SUCCESS = 0;
    public static final int AUTH_MACHINE_FAILED = 1;


    private int id;// reference AppleAccount.id
    // use new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create() to
    // json will use this field
    @Expose
    private long volumeSerialNumber;
    @Expose
    private String macAddress;
    @Expose
    private String processorName;
    @Expose
    private String biosInfo;
    @Expose
    private String productId;
    @Expose
    private String computerName;
    @Expose
    private String hwProfile;
    @Expose
    private String machineGuid;
    private int authStatus;
    private Date authTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getVolumeSerialNumber() {
        return volumeSerialNumber;
    }

    public void setVolumeSerialNumber(long volumeSerialNumber) {
        this.volumeSerialNumber = volumeSerialNumber;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getProcessorName() {
        return processorName;
    }

    public void setProcessorName(String processorName) {
        this.processorName = processorName;
    }

    public String getBiosInfo() {
        return biosInfo;
    }

    public void setBiosInfo(String biosInfo) {
        this.biosInfo = biosInfo;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getComputerName() {
        return computerName;
    }

    public void setComputerName(String computerName) {
        this.computerName = computerName;
    }

    public String getHwProfile() {
        return hwProfile;
    }

    public void setHwProfile(String hwProfile) {
        this.hwProfile = hwProfile;
    }

    public String getMachineGuid() {
        return machineGuid;
    }

    public void setMachineGuid(String machineGuid) {
        this.machineGuid = machineGuid;
    }

    public int getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(int authStatus) {
        this.authStatus = authStatus;
    }

    public Date getAuthTime() {
        return authTime;
    }

    public void setAuthTime(Date authTime) {
        this.authTime = authTime;
    }

    @Override
    public String toString() {
        return "AppleAccountMachineInfo[" +
                "id=" + id +
                ", volumeSerialNumber=" + volumeSerialNumber +
                ", macAddress='" + macAddress + '\'' +
                ", processorName='" + processorName + '\'' +
                ", biosInfo='" + biosInfo + '\'' +
                ", productId='" + productId + '\'' +
                ", computerName='" + computerName + '\'' +
                ", hwProfile='" + hwProfile + '\'' +
                ", machineGuid='" + machineGuid + '\'' +
                ", authStatus=" + authStatus +
                ", authTime=" + authTime +
                ']';
    }


    public void addInfo(AppleAccountMachineInfo appleAccountMachineInfo) {
        // TODO Auto-generated method stub

    }


}
