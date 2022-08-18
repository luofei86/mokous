//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.base.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author luofei
 * Generate 2020/1/12
 */
public class NormalUtils {
    private static final int INDEX_NOT_FOUND = -1;

    public static <G> List<List<G>> splitList(List<G> values, int subSize) {
        List<List<G>> result = new ArrayList<>();
        if (values.size() <= subSize) {
            result.add(values);
            return result;
        }
        int listSize;
        if (values.size() % subSize == 0) {
            listSize = values.size() / subSize;
        } else {
            listSize = values.size() / subSize + 1;
        }
        for (int i = 0; i < listSize; i++) {
            int fromIndex = i * subSize;
            int toIndex = (i + 1) * subSize;
            if (toIndex > values.size()) {
                toIndex = values.size();
            }
            result.add(values.subList(fromIndex, toIndex));
        }
        return result;
    }

    public static boolean isNumberClass(Class<?> clazz) {
        return clazz == byte.class || clazz == Byte.class || clazz == short.class || clazz == Short.class
                || clazz == int.class || clazz == Integer.class || clazz == float.class || clazz == Float.class
                || clazz == double.class || clazz == Double.class || clazz == long.class || clazz == Long.class;
    }

    public static boolean isEmpty(Object[] keys) {
        return keys == null || keys.length <= 0;
    }

    private static final String ALGORITHM_TYPE = "AES";
    private static final String ALGORITHM_TYPES = "AES/ECB/PKCS5Padding";

    public static String encrypt(String s, String k) throws Exception {
        if (s != null && k != null) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(k.getBytes(), ALGORITHM_TYPE);
            Cipher cipher = Cipher.getInstance(ALGORITHM_TYPES);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] result = cipher.doFinal(s.getBytes());
            return byte2Hex(result);
        }
        throw new IllegalAccessException();
    }

    public static String decrypt(String s, String k) throws Exception {
        if (s != null && k != null) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(k.getBytes(), ALGORITHM_TYPE);
            Cipher cipher = Cipher.getInstance(ALGORITHM_TYPES);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] result = cipher.doFinal(hex2Byte(s));
            return new String(result);
        }
        return null;
    }

    private static byte[] hex2Byte(String s) throws IllegalAccessException {
        if (s == null) {
            return null;
        }
        byte[] bytes = s.getBytes();
        if (bytes.length % 2 != 0) {
            throw new IllegalAccessException();
        }
        byte[] result = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i += 2) {
            String item = new String(bytes, i, 2);
            result[i / 2] = (byte) Integer.parseInt(item, 16);
        }
        return result;
    }

    public static final char[] c = "0123456789ABCDEF".toCharArray();

    private static String byte2Hex(byte[] result) {
        if (result == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : result) {
            int v = b & 255;
            sb.append(c[v / 16]).append(c[v % 16]);
        }
        return sb.toString();
    }

    public static String combineUrlParameter(String url, Map<String, String> urlParams) {
        if (CollectionUtils.isEmpty(urlParams)) {
            return url;
        }
        StringBuilder sb = new StringBuilder(url);
        if (url.contains("?")) {
            urlParams.forEach((k, v) -> {
                if (k != null && v != null) {
                    sb.append("&").append(k).append("=").append(v);
                }
            });
            return sb.toString();
        } else {
            sb.append("?");
            urlParams.forEach((k, v) -> {
                if (k != null && v != null) {
                    sb.append("&").append(k).append("=").append(v);
                }
            });
            return StringUtils.removeEnd(sb.toString(), "&");
        }
    }

    public static String substringBetween(String sql, String s) {
        return substringBetween(sql, s, s);
    }

    public static String substringBetween(String str, final String open, final String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        final int start = str.indexOf(open);
        if (start == INDEX_NOT_FOUND) {
            return null;
        }
        final int end = str.indexOf(close, start + open.length());
        if (end == INDEX_NOT_FOUND) {
            return null;
        }
        return str.substring(start + open.length(), end);
    }

    public static String substringBetween(String str, final String open, final String close, boolean ignoreCase) {
        if (str == null || open == null || close == null) {
            return null;
        }
        int start;
        if (ignoreCase) {
            start = StringUtils.indexOfIgnoreCase(str, open);
        } else {
            start = str.indexOf(open);
        }

        if (start == INDEX_NOT_FOUND) {
            return null;
        }
        int end;
        if (ignoreCase) {
            end = StringUtils.indexOfIgnoreCase(str, close, start + open.length());
        } else {
            end = str.indexOf(close, start + open.length());
        }
        if (end == INDEX_NOT_FOUND) {
            return null;
        }
        return str.substring(start + open.length(), end);
    }

    public static int length(Object[] objects) {
        return objects == null ? 0 : objects.length;
    }

    public static boolean isSamelength(Object[]... objects) {
        if (objects == null || objects.length == 1) {
            return true;
        }
        for (int i = 1; i < objects.length; i++) {
            if (length(objects[i]) != length(objects[i - 1])) {
                return false;
            }
        }
        return true;
    }

}
