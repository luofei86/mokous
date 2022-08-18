// Copyright 2014 www.refanqie.com Inc. All Rights Reserved.

package com.mokous.core.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.mokous.core.dto.DbData;
import com.mokous.core.dto.StatusType;
import com.mokous.web.utils.GsonUtils;

/**
 * @author luofei@refanqie.com (Your Name Here)
 *
 */
public class ParameterUtils {
    private static final String MAIL_PATTERN = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";

    public static boolean isAllValue(Integer value) {
        return value != null && value == StatusType.STATUS_ALL.getStatus();
    }

    public static boolean isMail(String mail) {
        if (StringUtils.isEmpty(mail)) {
            return false;
        }
        Pattern regex = Pattern.compile(MAIL_PATTERN);
        Matcher matcher = regex.matcher(mail);
        return matcher.matches();
    }

    public static String genOsGuid(String osName, String email) {
        if (StringUtils.isEmpty(osName) || StringUtils.isEmpty(email)) {
            return "";
        }
        String osNameMd5 = toMd5(osName.getBytes(), false);
        String adapterInfoMd5 = toMd5(genAdapterInfo(email).getBytes(), true).substring(0, 8);
        String volumeMd5 = toMd5(genVolume(email).getBytes(), true).substring(0, 8);
        String boisMd5 = toMd5(genBios(email).getBytes(), true).substring(0, 8);
        String cpuMd5 = toMd5(genProcessName(email).getBytes(), true).substring(0, 8);
        String pidMd5 = toMd5(genPid().getBytes(), true).substring(0, 8);
        String computerNameMd5 = osNameMd5.toUpperCase().substring(0, 8);
        String hwProfileMd5 = toMd5(genHwProfile(osNameMd5).getBytes(), true).substring(0, 8);
        return adapterInfoMd5 + "." + volumeMd5 + "." + boisMd5 + "." + cpuMd5 + "." + pidMd5 + "." + computerNameMd5
                + "." + hwProfileMd5;
    }

    private static String genAdapterInfo(String osName) {
        try {
            return new String(new String(new String(osName.getBytes(), "utf-8").getBytes(), "gb2312").getBytes(),
                    "ascii");
        } catch (UnsupportedEncodingException e) {
        }
        return osName.substring(0, osName.hashCode());
    }

    private static String genHwProfile(String osNameMd5) {
        return "{" + osNameMd5.substring(0, 8) + "-" + osNameMd5.substring(8, 12) + "-" + osNameMd5.substring(12, 16)
                + "-" + osNameMd5.substring(16, 20) + "-" + osNameMd5.substring(20) + "}";
    }

    private static String genVolume(String osName) {
        return new StringBuilder(osName).reverse().toString();
    }

    private static String genBios(String osName) {
        return "ACRSYS-" + Math.abs(osName.hashCode());
    }

    private static String genPid() {
        return String.valueOf(new Random().nextInt(10000) + 1000);
    }

    private static String toMd5(byte[] paramArrayOfByte, boolean paramBoolean) {
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.reset();
            localMessageDigest.update(paramArrayOfByte);
            String str = toHexString(localMessageDigest.digest(), "", paramBoolean);
            return str;
        } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
            throw new RuntimeException(localNoSuchAlgorithmException);
        }
    }

    public static String toHexString(byte[] paramArrayOfByte, String paramString, boolean paramBoolean) {
        StringBuilder localStringBuilder = new StringBuilder();
        int i = paramArrayOfByte.length;
        for (int j = 0; j < i; j++) {
            String str = Integer.toHexString(0xFF & paramArrayOfByte[j]);
            if (paramBoolean)
                str = str.toUpperCase();
            if (str.length() == 1)
                localStringBuilder.append("0");
            localStringBuilder.append(str).append(paramString);
        }
        return localStringBuilder.toString();
    }

    private static String genProcessName(String osName) {
        int hashCode = Math.abs(osName.hashCode());
        // "Intel(R) Core(TM) i3-2120 CPU @ 3.30GHz";
        int index = hashCode % INTEL_CPUS.length;
        String cpu = INTEL_CPUS[index];
        index = hashCode % CPUS_HZS.length;
        String hz = CPUS_HZS[index];
        return "Intel(R) Core(TM) " + cpu + " CPU @ " + hz;
    }

    private static String[] INTEL_CPUS = new String[] { "i3-2130,i3-2140,i3-1120,i3-3120,i3-2170,i3-2180,i3-2190,"
            + "i5-2130,i5-2140,i5-1120,i5-3120,i5-2170,i5-2180,i5-2190"
            + "i7-2130,i7-2140,i7-1120,i7-3120,i7-2170,i7-2180,i7-2190" };
    private static String[] CPUS_HZS = new String[] { "2.30GHz", "2.40GHz", "2.50GHz", "2.60GHz", "2.70GHz", "2.80GHz",
            "2.9GHz", "3.00GHz", "3.10GHz", "3.20GHz", "3.30GHz", "3.40GHz", "3.50GHz", "3.60GHz", "3.70GHz",
            "3.80GHz", "3.90GHz" };


    public static int formatPositiveInt(Integer start, int defaultStart) {
        return start == null || start.intValue() < 0 ? defaultStart : start.intValue();
    }


    public static int formatStart(Integer start) {
        return formatPositiveInt(start, 0);
    }

    public static int formatSize(Integer size) {
        return formatPositiveInt(size, 10);
    }

    public static String idsStringToIdListGsonStr(String ids) {
        List<String> idList = Arrays.asList(StringUtils.split(ids, ","));
        idList.removeAll(Arrays.asList("", null));
        return GsonUtils.listToJsonStr(idList);
    }

    public static List<Integer> idstringToIdList(String ids) {
        String[] idArray = ids.split(",");
        List<Integer> ret = new ArrayList<Integer>(idArray.length);
        for (String id : idArray) {
            ret.add(Integer.valueOf(id));
        }
        return ret;
    }

    public static int formatStatus(Integer status) {
        return status == null ? StatusType.STATUS_ALL.getStatus() : status.intValue();
    }

    public static int formatEnumInt(Integer value) {
        if (value == null) {
            return StatusType.STATUS_ALL.getStatus();
        }
        return value.intValue();
    }

    /**
     * if null return 0 else return value;
     * 
     * @param value
     * @return
     */
    public static int formatIdInt(Integer value) {
        if (value == null) {
            return 0;
        }
        return value.intValue();
    }

    /**
     * if null reutrn -1
     * 
     * @param value
     * @return
     */
    public static int formatUnsignedEnumIdInt(Integer value) {
        if (value == null) {
            return -1;
        }
        return value.intValue();
    }

    public static String formatEnumString(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        if (StringUtils.equalsIgnoreCase(value, String.valueOf(DbData.STATUS_ALL))
                || (StringUtils.equalsIgnoreCase(DbData.STATUS_ALL_DESC, value))) {
            return null;
        }
        return value;
    }

    /**
     * learn more
     * https://msdn.microsoft.com/en-us/library/windows/desktop/dd318693
     * (v=vs.85).aspx
     * 
     * @param langId
     * @return
     */
    public static String toMsLangValue(int langId) {
        String hexString = Integer.toHexString(langId);
        if (hexString.length() == 1) {
            return "0x000" + hexString;
        }
        if (hexString.length() == 2) {
            return "0x00" + hexString;
        }
        if (hexString.length() == 3) {
            return "0x0" + hexString;
        }
        return "0x" + hexString;
    }
}
