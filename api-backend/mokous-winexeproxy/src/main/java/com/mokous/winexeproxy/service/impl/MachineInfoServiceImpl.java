// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.winexeproxy.service.impl;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;
import com.mokous.core.cache.appleaccount.login.MachineInfo;
import com.mokous.web.exception.ServiceException;
import com.mokous.web.utils.GsonUtils;
import com.mokous.winexeproxy.service.MachineInfoService;
import com.mokous.winexeproxy.utils.NativeInformation;
import com.sun.deploy.util.WinRegistry;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;


/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
@SuppressWarnings("restriction")
@Service("machineInfoService")
public class MachineInfoServiceImpl implements MachineInfoService {
    private static final Logger logger = Logger.getLogger(MachineInfoServiceImpl.class);

    private static final String CPU_I5_NAME = "Intel(R) Core(TM) i5-2500 CPU @ 3.30GHz";
    private static final String DELL_BIOS_INFO_FORMAT = "DELL   - %d"; // "DELL
                                                                       // -
                                                                       // 6222004";
    private static final String PRODUCT_ID_FORMAT = "%1$05d-%2$03d-%3$07d-%4$05d";
    private static final int MIN_VOLUME_SERIAL_NUMBER = 268435456;
    private static final int MAX_VOLUME_SERIAL_NUMBER = 2147483647;
    private static final int VOLUME_SERIAL_NUMBER_RANGE = MAX_VOLUME_SERIAL_NUMBER - MIN_VOLUME_SERIAL_NUMBER + 1;

    private static final String MACHINE_KEY_CALC_HEAD = "cache-controlEthernet";
    private static final String ZEROS_MD5_8 = "00000000";

    @Override
    public MachineInfo generateLocalMachineInfo() {
        MachineInfo localMachineInfo = new MachineInfo();
        localMachineInfo.setComputerName(getLocalComputerName());
        localMachineInfo.setBiosInfo(getLocalSystemBiosVersion());
        localMachineInfo.setHwProfile(getLocalHwProfileGuid());// ("{846ee340-7039-11de-9d20-806e6f6e6963}");
        localMachineInfo.setMacAddress(getLocalMacAddress());
        localMachineInfo.setProcessorName(getLocalProcessString());
        localMachineInfo.setProductId(null);
        localMachineInfo.setVolumeSerialNumber(getLocalVolumeSerialNumber());
        completeKMachineId(localMachineInfo);
        String guid = new MachineInfoBuilder().calcMachineGuid(localMachineInfo);
        localMachineInfo.setMachineGuid(guid);
        return localMachineInfo;
    }

    @Override
    public MachineInfo generateMachineInfo() {
        MachineInfoBuilder machineInfoBuilder = new MachineInfoBuilder();
        machineInfoBuilder.generateBiosInfo().generateComputeName().generateHwProfile().generateMacAddress()
                .generateProcessorName().generateProductId().generateVolumeSerial().generateMachineGuid();
        MachineInfo machineInfo = machineInfoBuilder.toMachineInfo();
        completeKMachineId(machineInfo);
        return machineInfo;
    }

    private static void completeKMachineId(MachineInfo machineInfo) {
        try {
            machineInfo.setkMachineIdA(calcKMachineIdA_BASE64(machineInfo));
            machineInfo.setkMachineIdB(calcKMachineIdB_BASE64(machineInfo));
        } catch (UnsupportedEncodingException e) {
            logger.error("authServerInstanceService buildLauchServerCmd UnsupportedEncodingException", e);
            throw ServiceException.getInternalException(e.getMessage());
        }
    }

    @Override
    public void makeSureCompleteMachineInfo(MachineInfo machineInfo) {
        if (machineInfo == null) {
            return;
        }
        if (StringUtils.isBlank(machineInfo.getBiosInfo()) || StringUtils.isBlank(machineInfo.getComputerName())) {
            return;
        }
        if (StringUtils.isBlank(machineInfo.getkMachineIdB()) || StringUtils.isBlank(machineInfo.getkMachineIdB())) {
            completeKMachineId(machineInfo);
        }
        if (StringUtils.isBlank(machineInfo.getMachineGuid())) {
            String guid = new MachineInfoBuilder().calcMachineGuid(machineInfo);
            machineInfo.setMachineGuid(guid);
        }
        return;
    }

    /**
     * init: unsigned long kMachineIdA[6] =
     * {0x00000006,0x0B32EE8B,0x00004011,0x00000000};
     * 
     * @return
     * @throws UnsupportedEncodingException
     */
    private static String calcKMachineIdA_BASE64(MachineInfo machineInfo) throws UnsupportedEncodingException {
        byte[] appended = getAsciiBytes(MACHINE_KEY_CALC_HEAD);
        byte[] array = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                .putInt(machineInfo.getVolumeSerialNumber()).array();
        appended = appendInterMd5Bytes(appended, array);
        array = getAsciiBytes(machineInfo.getBiosInfo()); // biosInfo.getBytes("ASCII");
        appended = appendInterMd5Bytes(appended, array);
        array = getAsciiBytes(machineInfo.getProcessorName()); // processorName.getBytes("ASCII");
        array = ByteBuffer.allocate(array.length + 1).put(array).put((byte) 0x0).array();
        appended = appendInterMd5Bytes(appended, array);
        array = getUTF_16LEBytes(machineInfo.getProductId()); // "".getBytes("ASCII");
        appended = appendInterMd5Bytes(appended, array);

        byte[] ret = DigestUtils.md5(appended);
        byte[] machineA = ByteBuffer.allocate(24).order(ByteOrder.LITTLE_ENDIAN).putInt(0x00000006).putInt(0x0B32EE8B)
                .putInt(0x00004011).putInt(0x00000000).array();
        for (int i = 0; i < 6; i++) {
            machineA[i + 4] = ret[i];
        }
        return Base64.encode(machineA);
    }

    /**
     * init: unsigned long kMachineIdB[6] =
     * {0x00000006,0x3293AE72,0x0000D1C1,0x00000000};
     */
    private static String calcKMachineIdB_BASE64(MachineInfo machineInfo) throws UnsupportedEncodingException {
        byte[] appended = getAsciiBytes(MACHINE_KEY_CALC_HEAD);
        byte[] array = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                .putInt(machineInfo.getVolumeSerialNumber()).array();
        appended = ByteBuffer.allocate(appended.length + array.length).put(appended).put(array).array();
        array = getAsciiBytes(machineInfo.getBiosInfo());
        appended = ByteBuffer.allocate(appended.length + array.length).put(appended).put(array).array();
        array = getAsciiBytes(machineInfo.getProcessorName());
        appended = ByteBuffer.allocate(appended.length + array.length).put(appended).put(array).array();
        appended = ByteBuffer.allocate(appended.length + 1).put(appended).put((byte) 0x0).array();
        array = getUTF_16LEBytes(machineInfo.getProductId());
        appended = ByteBuffer.allocate(appended.length + array.length).put(appended).put(array).array();
        byte[] ret = DigestUtils.md5(appended);
        byte[] machineB = ByteBuffer.allocate(24).order(ByteOrder.LITTLE_ENDIAN).putInt(0x00000006).putInt(0x3293AE72)
                .putInt(0x0000D1C1).putInt(0x00000000).array();
        for (int i = 0; i < 6; i++) {
            machineB[i + 4] = ret[i];
        }
        return Base64.encode(machineB);
    }

    private static byte[] appendInterMd5Bytes(byte[] src, byte[] array) {
        if (array == null || array.length == 0) {
            return src;
        }
        byte[] bytes = DigestUtils.md5(array);
        byte[] array2 = Arrays.copyOfRange(bytes, 0, 4);
        return ByteBuffer.allocate(src.length + array2.length).put(src).put(array2).array();
    }

    private static byte[] getAsciiBytes(String value) {
        if (value == null) {
            value = "";
        }
        try {
            return value.getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            return new byte[0];
        }
    }

    private static byte[] getUTF_16LEBytes(String value) {
        if (value == null) {
            value = "";
        }
        try {
            return value.getBytes("UTF_16LE");
        } catch (UnsupportedEncodingException e) {
            return new byte[0];
        }
    }


    private static class MachineInfoBuilder {

        private MachineInfo machineInfo = null;

        MachineInfoBuilder() {
            machineInfo = new MachineInfo();
        }

        MachineInfo toMachineInfo() {
            return machineInfo;
        }

        private MachineInfoBuilder generateHwProfile() {
            machineInfo.setHwProfile("{" + UUID.randomUUID().toString() + "}");
            return this;
        }

        private MachineInfoBuilder generateProductId() {
            Random random = new Random();
            int no1 = random.nextInt(99999);
            int no2 = random.nextInt(999);
            int no3 = random.nextInt(9999999);
            int no4 = random.nextInt(99999);
            machineInfo.setProductId(String.format(PRODUCT_ID_FORMAT, no1, no2, no3, no4));
            return this;
        }

        private MachineInfoBuilder generateVolumeSerial() {
            Random rn = new Random();
            int i = rn.nextInt(VOLUME_SERIAL_NUMBER_RANGE);
            machineInfo.setVolumeSerialNumber(MIN_VOLUME_SERIAL_NUMBER + i);
            return this;
        }

        private MachineInfoBuilder generateMacAddress() {
            // 78-2B-CB-9A-65-ED
            Random random = new Random();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                int each = random.nextInt(256);
                sb.append(String.format("%02X%s", each, (i < 5) ? "-" : ""));
            }
            machineInfo.setMacAddress(sb.toString());
            return this;
        }

        private MachineInfoBuilder generateComputeName() {
            machineInfo.setComputerName("win-" + UUID.randomUUID().toString().substring(24));
            return this;
        }

        private MachineInfoBuilder generateBiosInfo() {
            Random rn = new Random();
            int i = rn.nextInt() % 5000000 + 1000000;
            machineInfo.setBiosInfo(String.format(DELL_BIOS_INFO_FORMAT, i));
            return this;
        }

        private MachineInfoBuilder generateProcessorName() {
            machineInfo.setProcessorName(CPU_I5_NAME);
            return this;
        }

        private static String getVolumeMd5_8(int volumeSerialNumber) {
            if (volumeSerialNumber <= 0) {
                return ZEROS_MD5_8;
            }
            byte[] array = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(volumeSerialNumber).array();
            String md5src = DigestUtils.md5Hex(array);
            return md5src.substring(0, 8).toUpperCase();
        }

        private static String getAdapterMd5_8(String macAddress) {
            if (macAddress == null) {
                return ZEROS_MD5_8;
            }
            String[] split = macAddress.split("-");
            int len = split.length;
            byte[] hardwareAddress = new byte[len];
            for (int i = 0; i < len; i++) {
                String eachByte = split[i];
                Integer byteValue = Integer.parseInt(eachByte, 16);
                byte[] array = ByteBuffer.allocate(1).put(byteValue.byteValue()).array();
                hardwareAddress[i] = array[0];
            }
            String md5src = DigestUtils.md5Hex(hardwareAddress);
            return md5src.substring(0, 8).toUpperCase();
        }

        private static String getProductIdMd5_8(String productId) {
            if (productId == null) {
                return ZEROS_MD5_8;
            }
            byte[] bytes = new byte[0];
            try {
                bytes = productId.getBytes("UTF_16LE");
            } catch (UnsupportedEncodingException e) {
                return ZEROS_MD5_8;
            }
            String md5src = DigestUtils.md5Hex(bytes);
            return md5src.substring(0, 8).toUpperCase();
        }

        private String getProcessorMd5_8(String processorName) {
            if (processorName == null) {
                return ZEROS_MD5_8;
            }

            byte[] bytes = processorName.getBytes();
            byte[] array = ByteBuffer.allocate(bytes.length + 1).put(bytes).put(bytes.length, (byte) 0x0).array();
            return DigestUtils.md5Hex(array).substring(0, 8).toUpperCase();


        }

        private String getBiosMd5_8(String biosInfo) {
            if (biosInfo == null) {
                return ZEROS_MD5_8;
            }
            return DigestUtils.md5Hex(biosInfo).substring(0, 8).toUpperCase();
        }

        private static String getComputerMd5_8(String computerName) {
            if (computerName == null) {
                return ZEROS_MD5_8;
            }
            byte[] bytes = new byte[0];
            try {
                bytes = computerName.getBytes("UTF_16LE");
            } catch (UnsupportedEncodingException e) {
                return ZEROS_MD5_8;
            }
            int len = computerName.length();
            byte[] newBytes = Arrays.copyOf(bytes, len);
            String md5src = DigestUtils.md5Hex(newBytes);
            return md5src.substring(0, 8).toUpperCase();
        }

        private static String getHwProfileMd5_8(String hwProfile) {
            if (hwProfile == null) {
                return ZEROS_MD5_8;
            }
            try {
                String md5src = DigestUtils.md5Hex(hwProfile.getBytes("UTF_16LE"));
                return md5src.substring(0, 8).toUpperCase();
            } catch (UnsupportedEncodingException e) {
                return ZEROS_MD5_8;
            }
        }

        private String calcMachineGuid(MachineInfo machineInfo) {
            String guid = getAdapterMd5_8(machineInfo.getMacAddress()) + "."
                    + getVolumeMd5_8(machineInfo.getVolumeSerialNumber()) + "."
                    + getProductIdMd5_8(machineInfo.getProductId()) + "."
                    + getProcessorMd5_8(machineInfo.getProcessorName()) + "." + getBiosMd5_8(machineInfo.getBiosInfo())
                    + "." + getComputerMd5_8(machineInfo.getComputerName()) + "."
                    + getHwProfileMd5_8(machineInfo.getHwProfile());
            return guid;
        }

        private void generateMachineGuid() {
            String guid = calcMachineGuid(machineInfo);
            machineInfo.setMachineGuid(guid);
        }
    }

    /*
     * # range A: get local machine information
     */

    private static int getLocalVolumeSerialNumber() {
        return NativeInformation.getVolumeSerialNumber("C:\\");
    }

    private static String getLocalSystemBiosVersion() {
        byte[] systemBiosVersion = (byte[]) WinRegistry.get(WinRegistry.HKEY_LOCAL_MACHINE, // HKEY
                "HARDWARE\\DESCRIPTION\\System", // Key
                "SystemBiosVersion");
        try {
            return new String(systemBiosVersion, "UTF_16LE");
        } catch (UnsupportedEncodingException e) {
            // TODO
            return null;
        }
    }

    private static String getLocalProcessString() {
        String regKey = "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0";
        return WinRegistry.getString(WinRegistry.HKEY_LOCAL_MACHINE, // HKEY
                regKey, "ProcessorNameString");
    }

    // private static String getLocalWinProductId() {
    // String regKey = "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion";
    // return WinRegistry.getString(WinRegistry.HKEY_LOCAL_MACHINE, // HKEY
    // regKey, "ProductId");
    // }

    private String getLocalHwProfileGuid() {
        String regKey = "SYSTEM\\ControlSet001\\Control\\IDConfigDB\\Hardware Profiles\\0001";
        return WinRegistry.getString(WinRegistry.HKEY_LOCAL_MACHINE, // HKEY
                regKey, "HwProfileGuid");
    }

    private static String getLocalMacAddress() {
        InetAddress IP = null;
        try {
            IP = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(IP);
            byte[] mac = network.getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            return sb.toString();
        } catch (UnknownHostException e) {
            return null;
        } catch (SocketException e) {
            return null;
        }
    }

    private static String getLocalComputerName() {
        try {
            return System.getenv("COMPUTERNAME");
        } catch (Exception e) {
            return null;
        }
    }

    /*
     * # end range A
     */

    @Override
    public MachineInfo generateMachineInfo(String machine) {
        MachineInfo machineInfo = null;
        if (!StringUtils.isEmpty(machine)) {
            Type type = new TypeToken<MachineInfo>() {}.getType();
            if (!StringUtils.isEmpty(machine)) {
                try {
                    machineInfo = GsonUtils.convert(machine, type);
                } catch (Exception e) {
                    // ignore e
                }
            }
        }
        if (StringUtils.isEmpty(machine) || machineInfo == null
                || StringUtils.isAnyEmpty(machineInfo.getComputerName(), machineInfo.getMachineGuid())) {
            machineInfo = generateMachineInfo();
        }
        completeKMachineId(machineInfo);
        return machineInfo;
    }
}
