// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.core.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;

import com.google.gson.Gson;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
@SuppressWarnings("deprecation")
public class HttpUtil {
    public static String getCookie(HttpContext httpContext, String cookieName) {
        CookieStore cookieStore = (CookieStore) httpContext.getAttribute("http.cookie-store");
        List<Cookie> cookies = cookieStore.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName) && cookie.getExpiryDate().after(new Date())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private static Gson gson = new Gson();
    public static boolean DEBUG = false;

    public static MyRequestBuilder url(String url) {
        MyRequestBuilder builder = new MyRequestBuilder();
        builder.url = url;
        return builder;
    }

    public static class MyRequestBuilder {
        private String url;
        private Map<String, String> headers = new LinkedHashMap<>();
        private Map<String, String> params = new LinkedHashMap<>();
        private HttpEntity entity;
        private HttpHost proxy = null;

        private MyRequestBuilder() {
        };

        public MyRequestBuilder headers(Map<String, String> headersMap) {
            headers.putAll(headersMap);
            return this;
        }

        public MyRequestBuilder header(String key, String value) {
            headers.put(key, value);
            return this;
        }

        public MyRequestBuilder params(Map<String, ? extends Object> paramsMap) {
            for (Entry<String, ? extends Object> entry : paramsMap.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                params.put(key, value.toString());
            }
            return this;
        }

        public MyRequestBuilder param(String key, Object value) {
            params.put(key, value.toString());
            return this;
        }

        public MyRequestBuilder entity(String entity) {
            try {
                this.entity = new StringEntity(entity);
            } catch (UnsupportedEncodingException e) {
            }
            return this;
        }

        public MyRequestBuilder entity(String entity, ContentType contentType) {
            this.entity = new StringEntity(entity, contentType);
            return this;
        }

        public MyRequestBuilder entity(byte[] data) {
            this.entity = new ByteArrayEntity(data);
            return this;
        }

        public MyRequestBuilder entity(HttpEntity entity) {
            this.entity = entity;
            return this;
        }

        public MyRequestBuilder proxy(HttpHost proxy) {
            this.proxy = proxy;
            return this;
        }

        public MyRequestBuilder proxy(String host, int port) {
            this.proxy = new HttpHost(host, port);
            return this;
        }

        private HttpGet prepareGet() {
            StringBuilder sb = new StringBuilder(url);
            if (params.size() > 0) {
                if (!url.contains("?")) {
                    sb.append("?");
                }
                boolean first = true;
                for (Entry<String, String> entry : params.entrySet()) {
                    if (first) {
                        first = false;
                    } else {
                        sb.append("&");
                    }
                    try {
                        sb.append(URLEncoder.encode(entry.getKey(), "UTF-8")).append("=")
                                .append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
                    } catch (Exception e) {
                    }
                }
            }
            HttpGet get = new HttpGet(sb.toString());
            for (Entry<String, String> entry : headers.entrySet()) {
                get.addHeader(entry.getKey(), entry.getValue());
            }
            return get;
        }

        private HttpPost preparePost() {
            HttpPost post = null;

            try {
                if (!params.isEmpty()) {
                    if (null == entity) {
                        System.out.println("url:" + url + "\nPARAM:" + params.toString());
                        List<NameValuePair> datas = new ArrayList<NameValuePair>();
                        for (Entry<String, String> entry : params.entrySet()) {
                            datas.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));

                        }
                        post = new HttpPost(url);
                        post.setEntity(new UrlEncodedFormEntity(datas, Consts.UTF_8));
                    } else {
                        StringBuilder sb = new StringBuilder(url);
                        if (params.size() > 0) {
                            if (!url.contains("?")) {
                                sb.append("?");
                            }
                            boolean first = true;
                            System.out.println("url:" + url + "\nPARAM:" + params.toString());
                            for (Entry<String, String> entry : params.entrySet()) {
                                if (first) {
                                    first = false;
                                } else {
                                    sb.append("&");
                                }
                                try {
                                    sb.append(URLEncoder.encode(entry.getKey(), "UTF-8")).append("=")
                                            .append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
                                } catch (Exception e) {
                                }
                            }
                        }
                        post = new HttpPost(sb.toString());
                    }
                } else {
                    post = new HttpPost(url);
                }
                if (null != entity) {
                    post.setEntity(entity);
                }
            } catch (Exception e) {
            }
            final HttpPost finalPost = post;
            for (Entry<String, String> entry : headers.entrySet()) {
                finalPost.addHeader(entry.getKey(), entry.getValue());
            }
            return finalPost;
        }

        private <T> T execute(HttpUriRequest request, RespParser<T> parser, HttpContext context) {
            CloseableHttpClient client = null;
            try {
                SSLContextBuilder builder = new SSLContextBuilder();
                builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());
                HttpClientBuilder clientBuilder = HttpClientBuilder.create();
                if (DEBUG) {
                    clientBuilder.addInterceptorLast(reqInterceptor).addInterceptorFirst(new HttpResponseInterceptor() {
                        @Override
                        public void process(HttpResponse response, HttpContext context) throws HttpException,
                                IOException {
                            System.out.println(System.currentTimeMillis());
                            System.out.println("***** Start Print Response *****");
                            Header[] headers = response.getAllHeaders();
                            for (Header header : headers) {
                                System.out.println(header.toString());
                            }
                            System.out.println(response.getStatusLine());
                            // IOUtils.readLines(response.getEntity().getContent())
                            // .forEach(line -> System.out.println(line));;
                            System.out.println("***** End Print Response *****");
                        }
                    });
                }
                if (null != proxy)
                    clientBuilder.setSSLSocketFactory(sslsf).setHostnameVerifier(new X509HostnameVerifier() {
                        @Override
                        public void verify(String host, SSLSocket ssl) throws IOException {
                            return;
                        }

                        @Override
                        public void verify(String host, X509Certificate cert) throws SSLException {
                            return;
                        }

                        @Override
                        public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
                            return;
                        }

                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    }).setProxy(proxy);

                client = clientBuilder.build();

                return client.execute(request, parser, context);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != client) {
                    try {
                        client.close();
                    } catch (Exception e) {
                    }
                }
            }
            return null;
        }

        public void doGet() {
            doGet(RespParser.VOID_PARSER);
        }

        public void doGet(HttpContext context) {
            doGet(RespParser.VOID_PARSER, context);
        }

        public <T> T doGet(Class<T> clazz) {
            return doGet(clazz, null);
        }

        public <T> T doGet(Class<T> clazz, HttpContext context) {
            return gson.fromJson(doGet(RespParser.STRING_PARSER, context), clazz);
        }

        public <T> T doGet(RespParser<T> parser) {
            return doGet(parser, null);
        }

        public <T> T doGet(RespParser<T> parser, HttpContext context) {
            HttpGet get = prepareGet();
            return execute(get, parser, context);
        }

        public void doPost() {
            doPost(RespParser.VOID_PARSER);
        }

        public <T> T doPost(Class<T> clazz) {
            return doPost(clazz, null);
        }

        public <T> T doPost(Class<T> clazz, HttpContext context) {
            return gson.fromJson(doPost(RespParser.STRING_PARSER, context), clazz);
        }

        public <T> T doPost(RespParser<T> parser) {
            return doPost(parser, null);
        }

        public <T> T doPost(RespParser<T> parser, HttpContext context) {
            HttpPost post = preparePost();
            return execute(post, parser, context);
        }

        public String getGetRespStr(HttpContext context) {
            return doGet(RespParser.STRING_PARSER, context);
        }

        public String getGetRespStr() {
            return getGetRespStr(null);
        }

        public Boolean saveGetRespFile(File file, HttpContext context) {
            return doGet(new RespFileParser(file), context);
        }

        public Boolean saveGetRespFile(File file) {
            return saveGetRespFile(file, null);
        }

        public String getPostRespStr(HttpContext context) {
            return doPost(RespParser.STRING_PARSER, context);
        }

        public String getPostRespStr() {
            return getPostRespStr(null);
        }

        public Boolean savePostRespFile(File file, HttpContext context) {
            return doPost(new RespFileParser(file), context);
        }

        public Boolean savePostRespFile(File file) {
            return savePostRespFile(file, null);
        }
    }

    private static HttpRequestInterceptor reqInterceptor = new HttpRequestInterceptor() {

        public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
            System.out.println(System.currentTimeMillis());
            // Start Debug
            System.out.println("*** Request headers ***");
            ProtocolVersion pversion = request.getProtocolVersion();
            System.out.println(pversion);
            Header[] requestHeaders = request.getAllHeaders();
            for (Header header : requestHeaders) {
                System.out.println(header.toString());
            }
            System.out.println("RequestLine:" + request.getRequestLine());
            System.out.println("***********************");
            // End Debug
        }

    };

}
