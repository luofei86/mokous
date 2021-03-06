// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.apple.core.cache.appleaccount.login;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
public class MachineInfo implements Serializable {
    @SerializedName("volumeSerialNumber")
    @Expose
    private int volumeSerialNumber; // 981679892 or String: 3A83-3F14
    @SerializedName("macAddress")
    @Expose
    private String macAddress; // 78-2B-CB-9A-65-ED
    @SerializedName("processorName")
    @Expose
    private String processorName; // Intel(R) Core(TM) i5-2500 CPU @ 3.30GHz
    @SerializedName("biosInfo")
    @Expose
    private String biosInfo; // DELL - 6222004 
    @SerializedName("productId")
    @Expose
    private String productId; // 00426-OEM-8992662-00400 or empty
    @SerializedName("computerName")
    @Expose
    private String computerName; // YYH-PC
    @SerializedName("hwProfile")
    @Expose
    private String hwProfile; // guid for hardware profile
                              // {846ee340-7039-11de-9d20-806e6f6e6963}

    // calculate value
    @SerializedName("machineGuid")
    @Expose
    private String machineGuid;

    @SerializedName("kMachineIdA")
    @Expose
    private String kMachineIdA;
    @SerializedName("kMachineIdB")
    @Expose
    private String kMachineIdB;

    private static final long serialVersionUID = 8366549430935112994L;

    public int getVolumeSerialNumber() {
        return volumeSerialNumber;
    }

    public void setVolumeSerialNumber(int volumeSerialNumber) {
        machineGuid = null;
        this.volumeSerialNumber = volumeSerialNumber;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        machineGuid = null;
        this.macAddress = macAddress;
    }

    public String getProcessorName() {
        return processorName;
    }

    public void setProcessorName(String processorName) {
        machineGuid = null;
        this.processorName = processorName;
    }

    public String getBiosInfo() {
        return biosInfo;
    }

    public void setBiosInfo(String biosInfo) {
        machineGuid = null;
        this.biosInfo = biosInfo;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        machineGuid = null;
        this.productId = productId;
    }

    public String getComputerName() {
        return computerName;
    }

    public void setComputerName(String computerName) {
        machineGuid = null;
        this.computerName = computerName;
    }

    public String getHwProfile() {
        return hwProfile;
    }

    public void setHwProfile(String hwProfile) {
        machineGuid = null;
        this.hwProfile = hwProfile;
    }


    public String getkMachineIdA() {
        return kMachineIdA;
    }

    public void setkMachineIdA(String kMachineIdA) {
        this.kMachineIdA = kMachineIdA;
    }

    public String getkMachineIdB() {
        return kMachineIdB;
    }

    public void setkMachineIdB(String kMachineIdB) {
        this.kMachineIdB = kMachineIdB;
    }

    public String getMachineGuid() {
        return machineGuid;
    }

    public void setMachineGuid(String machineGuid) {
        this.machineGuid = machineGuid;
    }

    @Override
    public String toString() {
        return "MachineInfo{" + "volumeSerialNumber=" + volumeSerialNumber + ", macAddress='" + macAddress + '\''
                + ", processorName='" + processorName + '\'' + ", biosInfo='" + biosInfo + '\'' + ", productId='"
                + productId + '\'' + ", computerName='" + computerName + '\'' + ", hwProfile='" + hwProfile + '\''
                + ", machineGuid='" + machineGuid + '\'' + ", kMachineIdA='" + kMachineIdA + '\'' + ", kMachineIdB='"
                + kMachineIdB + '\'' + '}';
    }
}
