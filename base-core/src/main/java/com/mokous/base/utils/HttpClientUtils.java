//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.base.utils;

import com.mokous.base.exception.BizException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * @author luofei
 * Generate 2020/2/2
 */
public class HttpClientUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);
    private static final int NORMAL_TIMEOUT_MILLIS = 5000;

    public static String postJsonString(String url, Map<String, String> headers, Map<String, String> urlParams,
            String jsonStr, boolean isSSL) throws Exception {
        String resp = null;
        resp = (String) doPostJson(url, headers, urlParams, jsonStr, isSSL, new ResponseHandler<Object>() {
            @Override
            public Object handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
                return EntityUtils.toString(httpResponse.getEntity());
            }
        });
        return resp;
    }


    private static <T> T doPostJson(String url, Map<String, String> headers, Map<String, String> urlParams,
            String jsonStr, boolean isSSL, ResponseHandler<T> handler) throws Exception {
        Object resp = null;
        String requestUrl = combineUrlParameter(url, urlParams);
        CloseableHttpClient httpClient = isSSL ? getSSLClient() : getNoSSLClient();
        HttpPost httpPost = new HttpPost(requestUrl);
        addHeaders(httpPost, headers);
        if (StringUtils.isNotEmpty(jsonStr)) {
            HttpEntity httpEntity = EntityBuilder.create().setText(jsonStr).setContentType(ContentType.APPLICATION_JSON)
                    .setContentEncoding("UTF8").build();
            httpPost.setEntity(httpEntity);
        }
        try {
            resp = httpClient.execute(httpPost, handler);
            return (T) resp;
        } catch (IOException e) {
            logger.error("do {} post failed.", requestUrl, e);
            throw BizException.getNetException(e.getMessage());
        }
    }

    private static CloseableHttpClient getNoSSLClient() {
        return HttpClients.custom().setDefaultRequestConfig(
                RequestConfig.custom().setConnectionRequestTimeout(NORMAL_TIMEOUT_MILLIS)
                        .setConnectTimeout(NORMAL_TIMEOUT_MILLIS).setSocketTimeout(NORMAL_TIMEOUT_MILLIS * 6).build())
                .build();
    }

    private static CloseableHttpClient getSSLClient() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        X509TrustManager x509TrustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s){

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s){

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        sslContext.init(null, new TrustManager[] { x509TrustManager }, new SecureRandom());
        return HttpClients.custom().setDefaultRequestConfig(
                RequestConfig.custom().setConnectionRequestTimeout(NORMAL_TIMEOUT_MILLIS)
                        .setConnectTimeout(NORMAL_TIMEOUT_MILLIS).setSocketTimeout(NORMAL_TIMEOUT_MILLIS * 6).build())
                .setSSLContext(sslContext).build();
    }

    private static void addHeaders(final HttpMessage httpMessage, Map<String, String> headers) {
        if (CollectionUtils.isEmpty(headers)) {
            return;
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            httpMessage.addHeader(k, v);
        }
    }

    private static String combineUrlParameter(String url, Map<String, String> urlParams) {
        return NormalUtils.combineUrlParameter(url, urlParams);
    }
}
