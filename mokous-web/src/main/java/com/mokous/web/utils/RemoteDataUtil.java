// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.web.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import com.mokous.web.exception.ServiceException;
import com.mokous.web.model.ApiRespWrapper;
import com.mokous.web.model.ParametersHandle;
import com.mokous.web.model.PostParametersHandle;
import com.mokous.web.model.ReturnDataHandle;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月4日
 * 
 */
public class RemoteDataUtil {
    private static final Logger log = Logger.getLogger(RemoteDataUtil.class);
    private static final int DEFAULT_SO_TIMEOUT_MILLISECONDS = 5000;
    public static final int DEFAULT_SO_LONG_TIMEOUT_MILLISECONDS = 120000;

    public static <V> V postFile(String url, Map<String, String> paras, String fileParaName, String fileName,
            byte[] files, ReturnDataHandle<V> rdHandle) throws Exception {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        for (Entry<String, String> entry : paras.entrySet()) {
            builder.addTextBody(entry.getKey(), entry.getValue(),
                    ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), Consts.UTF_8));
        }
        builder.addBinaryBody(fileParaName, files, ContentType.APPLICATION_OCTET_STREAM, fileName);
        HttpEntity entity = builder.build();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        int tryTimes = 5;
        while (tryTimes > 0) {
            HttpPost filePost = new HttpPost(url);
            filePost.setEntity(entity);
            CloseableHttpResponse response = null;
            try {
                response = httpclient.execute(filePost);
                String ret = IOUtils.isToString(response.getEntity().getContent());
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    break;
                }
                if (rdHandle == null || StringUtils.isEmpty(ret)) {
                    return null;
                }
                return rdHandle.handle(ret);
            } catch (HttpException e) {
            } catch (IOException e) {
            } finally {
                IOUtils.close(response);
            }
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
            }
            --tryTimes;
        }
        return null;
    }

    public static String getRemote(String url, int so_timeout_milliseconds, int try_times) throws Exception {
        return getRemote(url, so_timeout_milliseconds, try_times, new Header[0]);
    }

    public static String getRemote(String url, int so_timeout_milliseconds, int try_times, List<Header> headers,
            List<Header> additionalHeaders) throws Exception {
        if (headers == null && additionalHeaders == null) {
            return getRemote(url, so_timeout_milliseconds, try_times);
        }
        if (headers == null) {
            return getRemote(url, so_timeout_milliseconds, try_times, additionalHeaders);
        }
        if (additionalHeaders == null) {
            return getRemote(url, so_timeout_milliseconds, try_times, headers);
        }
        List<Header> allHeaders = new ArrayList<Header>(headers);
        allHeaders.addAll(additionalHeaders);
        return getRemote(url, so_timeout_milliseconds, try_times, allHeaders);
    }

    public static String getRemote(String url, int so_timeout_milliseconds, int try_times, List<Header> headers)
            throws Exception {
        Header[] innerHeaders = null;
        if (CollectionUtils.notEmptyAndNull(headers)) {
            innerHeaders = new Header[headers.size()];
            innerHeaders = headers.toArray(innerHeaders);
        }
        return getRemote(url, so_timeout_milliseconds, try_times, innerHeaders);
    }

    public static String getRemote(String url, int so_timeout_milliseconds, int try_times, Header... headers)
            throws Exception {
        RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(so_timeout_milliseconds)
                .setConnectTimeout(so_timeout_milliseconds).setSocketTimeout(so_timeout_milliseconds).build();
        do {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            try {
                HttpGet httpget = new HttpGet(url);
                httpget.setConfig(config);
                if (headers != null) {
                    for (Header header : headers) {
                        httpget.addHeader(header);
                    }
                }

                CloseableHttpResponse response = httpclient.execute(httpget);
                try {
                    StatusLine statusLine = response.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        HttpEntity entity = response.getEntity();
                        if (entity != null) {
                            InputStream instream = entity.getContent();
                            try {
                                return IOUtils.isToString(instream);
                            } catch (IOException ex) {
                                throw ex;
                            } finally {
                                instream.close();
                            }
                        }
                    }
                } finally {
                    response.close();
                }
            } finally {
                httpclient.close();
            }
        } while (--try_times > 0);
        return null;
    }

    public static <K, V> V get(String url, K k, PostParametersHandle<K> psHandle, ReturnDataHandle<V> rdHandle,
            boolean print) throws Exception {
        return post(url, k, psHandle, rdHandle, print, DEFAULT_SO_TIMEOUT_MILLISECONDS);
    }

    public static <K, V> V post(String marketUrl, K k, PostParametersHandle<K> psHandle, ReturnDataHandle<V> rdHandle,
            boolean print) throws Exception {
        return post(marketUrl, k, psHandle, rdHandle, print, DEFAULT_SO_TIMEOUT_MILLISECONDS);
    }

    public static <K, V> V post(String marketUrl, K k, PostParametersHandle<K> psHandle, ReturnDataHandle<V> rdHandle,
            boolean print, int so_timeout_milliseconds) throws Exception {
        Map<String, Object> param = null;
        if (psHandle == null || k == null) {
            param = Collections.emptyMap();
        } else {
            param = psHandle.handle(k);
        }
        if (print) {
            log.info("Send data to url: " + marketUrl + " with param:" + param);
        }
        String result = postRemoteData(marketUrl, param, so_timeout_milliseconds, 3);
        if (print) {
            log.info("Send data to url: " + marketUrl + " with param:" + param + ", the return data is :"
                    + ((result != null && result.length() > 1000) ? result.substring(0, 1000) : result));
        }
        if (rdHandle == null) {
            return null;
        }
        return rdHandle.handle(result);
    }

    public static String postRemoteData(String marketUrl, Map<String, Object> param) throws Exception {
        return postRemoteData(marketUrl, param, DEFAULT_SO_TIMEOUT_MILLISECONDS, 3);
    }

    public static String postRemoteData(String marketUrl, Map<String, Object> param, int so_timeout_milliseconds,
            int try_times) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        do {
            // PostMethod method = new PostMethod(marketUrl);
            HttpPost httpPost = new HttpPost(marketUrl);
            RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(so_timeout_milliseconds)
                    .setConnectTimeout(so_timeout_milliseconds).setSocketTimeout(so_timeout_milliseconds).build();
            httpPost.setConfig(config);
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            if (param != null) {
                for (Entry<String, Object> entry : param.entrySet()) {
                    if (entry.getValue() != null) {
                        if (entry.getValue() instanceof List) {
                            List<?> listValue = (List<?>) entry.getValue();
                            String key = entry.getKey();
                            for (Object object : listValue) {
                                nvps.add(new BasicNameValuePair(key, object == null ? null : object.toString()));
                            }
                        } else {
                            nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
                        }
                    }
                }
            }

            httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
            CloseableHttpResponse response = null;
            int statusCode = 0;
            try {
                response = httpclient.execute(httpPost);
                statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != HttpStatus.SC_OK) {
                    break;
                }
                return IOUtils.isToString(response.getEntity().getContent());
            } catch (Exception e) {
                log.warn("Send data to url with param failed. Url:" + marketUrl + ", param:" + param, e);
                Thread.sleep(2000);
            } finally {
                IOUtils.close(response);
            }
        } while (--try_times > 0);
        return null;
    }

    public static <K, V> V get(String url, K para, ReturnDataHandle<V> returnDataHandle) {
        return get(url, para, ParametersHandle.PS_HANDLE, returnDataHandle);
    }

    public static <K, V> V get(String url, List<Integer> ids, ReturnDataHandle<V> returnDataHandle) {
        return get(url, ids, ParametersHandle.IDS_ARRAY_PD_HANDLE, returnDataHandle);
    }

    public static <K> ApiRespWrapper<Boolean> postJsonFormatDataBooleanValue(String url, K para) {
        try {
            return post(url, para, ParametersHandle.DATA_POST_HANDLE, ReturnDataHandle.BOOLEAN_RD_HANDLE, false);
        } catch (Exception e) {
            String errmsg = "Post data failed.. Url:" + url + ", errmsg:" + e.getMessage();
            log.error(errmsg, e);
            throw ServiceException.getInternalException(e.getMessage());
        }
    }

    public static <K> ApiRespWrapper<Boolean> getBoolean(String url, K para) {
        return get(url, para, ParametersHandle.PS_HANDLE, ReturnDataHandle.BOOLEAN_RD_HANDLE);
    }

    public static <K, V> V get(String url, K para, ParametersHandle<K> parametersHandle,
            ReturnDataHandle<V> returnDataHandle) {
        try {
            return get(url, para, parametersHandle, returnDataHandle, false);
        } catch (Exception e) {
            String errmsg = "Get data failed.. Url:" + url + ", errmsg:" + e.getMessage();
            log.error(errmsg, e);
            throw ServiceException.getInternalException(e.getMessage());
        }
    }


    public static <K, V> V get(String url, K k, ParametersHandle<K> psHandle, ReturnDataHandle<V> rdHandle,
            boolean print) throws Exception {
        return get(url, k, psHandle, rdHandle, print, DEFAULT_SO_TIMEOUT_MILLISECONDS);
    }

    public static <K, V> V get(String url, K k, ParametersHandle<K> psHandle, ReturnDataHandle<V> rdHandle,
            boolean print, int so_timeout_milliseconds) throws Exception {
        return get(url, k, psHandle, rdHandle, print, so_timeout_milliseconds, null);
    }

    public static <K, V> V get(String url, K k, ParametersHandle<K> psHandle, ReturnDataHandle<V> rdHandle,
            boolean print, int so_timeout_milliseconds, Map<String, String> headValue) throws Exception {
        if (psHandle != null && k != null) {
            String addPara = psHandle.handle(k);
            if (!StringUtils.isEmpty(addPara)) {
                url = UrlUtils.spliceParameter(url, addPara);
            }
        } else {
            // K as queryStr
            if (k != null && k instanceof String) {
                url = UrlUtils.spliceParameter(url, k.toString());
            }
        }
        if (print) {
            log.info("Start get data from url: " + url);
        }
        Header[] headers = null;
        if (!CollectionUtils.emptyOrNull(headValue)) {
            headers = new Header[headValue.size()];
            int i = 0;
            for (Entry<String, String> entry : headValue.entrySet()) {
                Header header = new BasicHeader(entry.getKey(), entry.getValue());
                headers[i] = header;
                ++i;
            }
        }
        String result = getRemote(url, so_timeout_milliseconds, 3, headers);
        if (print) {
            log.info("Finish get data from url: " + url + ", data:"
                    + ((result != null && result.length() > 1000) ? result.substring(0, 1000) : result));
        }
        if (rdHandle == null) {
            return null;
        }
        if (result == null) {
            return null;
        }
        return rdHandle.handle(result);
    }

    public static void main(String[] args) {
        get("http://third.miaozhuandaqian.com/www/channel/disctinct_new.3w?adid=2933&idfa=DF89F1DA-FB88-4DEA-8209-73B746E5176D&sign=d1b3b9d05c2be9b1ba19b0ddf0e3f945&channel=31318",
                null, null);
    }
}
