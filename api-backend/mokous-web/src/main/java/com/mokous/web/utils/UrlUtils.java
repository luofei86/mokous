// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.web.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.mokous.web.exception.ServiceException;


/**
 * @author luofei (Your Name Here)
 * @date 2016年10月4日
 * 
 */
public class UrlUtils {
    private static final Logger log = Logger.getLogger(UrlUtils.class);
    public static final String URL_SEPARATOR = IOUtils.URL_SEPARATOR;

    public static final String getUrlLastSubAddress(String url) {
        if (StringUtils.isEmpty(url)) {
            return "";
        }
        int queryIndex = url.indexOf("?");
        int lastSepIndex = -1;
        if (queryIndex < 0) {
            lastSepIndex = url.lastIndexOf(URL_SEPARATOR);
        } else {
            lastSepIndex = url.substring(0, queryIndex).lastIndexOf(URL_SEPARATOR);
        }
        if (lastSepIndex >= 0) {
            if (queryIndex > 0) {
                return url.substring(lastSepIndex + 1, queryIndex);
            } else {
                return url.substring(lastSepIndex + 1);
            }
        }
        return url;
    }

    /**
     * 有一定的BUG，只是常规判断是否以http://或者https://开头
     *
     * @param url
     * @return
     */
    public static final boolean isUrl(String url) {
        return StringUtils.startsWith(url, IOUtils.HTTP_PREFIX) || StringUtils.startsWith(url, IOUtils.HTTPS_PREFIX);
    }

    public static final String spliceParameter(String url, String parameter) {
        if (StringUtils.isEmpty(parameter)) {
            return url;
        }
        if (StringUtils.isEmpty(url)) {
            throw ServiceException.getParameterException("未知的url地址");
        }
        if (url.contains("?")) {
            if (url.endsWith("&")) {
                if (parameter.startsWith("&")) {
                    return url + parameter.substring(1);
                } else {
                    return url + parameter;
                }
            } else {
                if (url.endsWith("?")) {
                    if (parameter.startsWith("&")) {
                        return url + parameter.substring(1);
                    } else {
                        return url + parameter;
                    }

                } else {
                    if (parameter.startsWith("&")) {
                        return url + parameter;
                    } else {
                        return url + "&" + parameter;
                    }
                }
            }
        } else {
            if (parameter.startsWith("?")) {
                return url + parameter;
            } else {
                if (parameter.startsWith("&")) {
                    return url + "?" + parameter.substring(1);
                } else {
                    return url + "?" + parameter;
                }
            }
        }
    }

    public static final String spliceUrl(String prefix, String relativePath) {
        return IOUtils.spliceUrlPath(prefix, relativePath);
    }

    public static Map<Object, Object> objectToMap(Object param) {
        if (param == null) {
            return Collections.emptyMap();
        }
        return new BeanMap(param);
    }

    public static String objectToUrlQuery(Object param) {
        String pattern = objectToUrlPatternStr(param);
        return StringUtils.isBlank(pattern) ? "" : "?" + pattern;
    }

    public static String objectToUrlPatternStr(Object param) {
        if (param == null) {
            return "";
        }
        String paraToUrlQuery = "";
        try {
            BeanInfo info = Introspector.getBeanInfo(param.getClass());
            for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                if (pd.getPropertyType() == Class.class) {
                    continue;
                }
                Method reader = pd.getReadMethod();
                String name = pd.getName();
                if (reader != null) {
                    Object putObject = reader.invoke(param);
                    if (putObject == null) {
                        continue;
                    }
                    if (putObject instanceof Date) {
                        putObject = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(putObject);
                    } else if (putObject.getClass().isPrimitive()
                            || ClassUtils.isPrimitiveWrapper(putObject.getClass())) {
                    } else if (putObject instanceof String) {
                    } else if (putObject instanceof List) {
                    } else if (putObject.getClass().isArray()) {
                    } else {
                        continue;
                    }
                    if (StringUtils.isEmpty(paraToUrlQuery)) {
                        paraToUrlQuery = formatUrlParam(name, putObject);
                    } else {
                        paraToUrlQuery += "&" + formatUrlParam(name, putObject);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("object to url failed.Object:" + param, e);
        }

        if (StringUtils.isEmpty(paraToUrlQuery)) {
            return "";
        }
        return paraToUrlQuery;
    }

    @SuppressWarnings("unchecked")
    private static <T> String formatUrlParam(String key, Object value) {
        try {
            if (value == null) {
                return key + "=";
            }
            if (value instanceof String) {
                return key + "=" + URLEncoder.encode(value.toString(), "utf-8");
            }
            if (value instanceof List) {
                List<?> datas = (List<?>) value;
                boolean first = true;
                String paras = "";
                for (Object object : datas) {
                    if (object != null) {
                        if (first) {
                            paras += key + "=" + URLEncoder.encode(object.toString(), "utf-8");
                            first = false;
                        } else {
                            paras += "&" + key + "=" + URLEncoder.encode(object.toString(), "utf-8");
                        }
                    }
                }
                return paras;
            }
            if (value.getClass().isArray()) {
                T[] array = (T[]) value;
                boolean first = true;
                String paras = "";
                for (T object : array) {
                    if (object != null) {
                        if (first) {
                            paras += key + "=" + URLEncoder.encode(object.toString(), "utf-8");
                            first = false;
                        } else {
                            paras += "&" + key + "=" + URLEncoder.encode(object.toString(), "utf-8");
                        }
                    }
                }
                return paras;
            }
            return key + "=" + value.toString();
        } catch (UnsupportedEncodingException e) {
            return value == null ? key + "=" : key + "=" + value.toString();
        }
    }

    public static String mapToUrlQuery(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        String paraToUrlQuery = "";
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (StringUtils.isEmpty(paraToUrlQuery)) {
                paraToUrlQuery = formatUrlParam(entry.getKey(), entry.getValue());
            } else {
                paraToUrlQuery += "&" + formatUrlParam(entry.getKey(), entry.getValue());
            }
        }
        if (StringUtils.isEmpty(paraToUrlQuery)) {
            return "";
        }
        return "?" + paraToUrlQuery;
    }

    /**
     * encode <e>http://www.hello2game.com/h5games/地球守卫/index/index.html</e> to <br/>
     * <e>
     * <p/>
     * http://www
     * .hello2game.com/h5games/%E5%9C%B0%E7%90%83%E5%AE%88%E5%8D%AB/index
     * /index.html</e>
     *
     * @param url
     * @return
     */
    public static String decodeUrlPath(String url) {
        try {
            if (url.length() != url.getBytes().length) {
                char[] chars = url.toCharArray();
                String encodeUrl = "";
                for (char c : chars) {
                    if ((int) c > 256) {
                        encodeUrl += URLEncoder.encode(String.valueOf(c), "utf-8");
                    } else {
                        encodeUrl += c;
                    }
                }
                return encodeUrl;
            } else {
                return url;
            }
        } catch (UnsupportedEncodingException e) {
        }
        return url;
    }

    public static String appendQueryStr(String url, String quetyString) {
        if (StringUtils.isEmpty(url)) {
            return url;
        }
        if (url.contains("?")) {
            return url + "&" + quetyString;
        }
        return url + "?" + quetyString;
    }
}
