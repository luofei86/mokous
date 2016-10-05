// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.winexeproxy.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import com.dd.plist.NSData;
import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;
import com.mokous.core.cache.appleaccount.login.AccountInfo;
import com.mokous.web.exception.ServiceException;
import com.mokous.winexeproxy.exception.AppleAuthException;
import com.mokous.winexeproxy.model.AppleAuthExceptionMeta;
import com.mokous.winexeproxy.model.AppleLoginResult;
import com.mokous.winexeproxy.model.AuthMachineResult;
import com.mokous.winexeproxy.model.AuthResultCode;
import com.mokous.winexeproxy.service.HttpResponseContentParser;


/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
@SuppressWarnings("deprecation")
public class ItunesNetworkUtils {
    private static final Logger logger = Logger.getLogger(ItunesNetworkUtils.class);

    private static final String INIT_SIGN_SAP_SETUP_CERT_URL = "https://init.itunes.apple.com/WebObjects/MZInit.woa/wa/signSapSetupCert";
    private static final String SIGN_SAP_SETUP_URL = "https://play.itunes.apple.com/WebObjects/MZPlay.woa/wa/signSapSetup";

    public static final String ITUNES_USER_AGENT = "iTunes/12.0.1 (Windows; Microsoft Windows 7 x64 Ultimate Edition Service Pack 1 (Build 7601)) AppleWebKit/7600.1017.0.24";
    public static final String PLIST_CONTENT_TYPE = "application/x-apple-plist";
    public static final RedirectStrategy ITUNES_HTTP_REDIRECT_STRATEGY = new DefaultRedirectStrategy() {
        @Override
        protected boolean isRedirectable(final String method) {
            return true;
        }

        @Override
        public HttpUriRequest getRedirect(final HttpRequest request, final HttpResponse response,
                final HttpContext context) throws ProtocolException {
            final URI uri = getLocationURI(request, response, context);
            final String method = request.getRequestLine().getMethod();
            if (method.equalsIgnoreCase(HttpHead.METHOD_NAME)) {
                return new HttpHead(uri);
            } else if (method.equalsIgnoreCase(HttpGet.METHOD_NAME)) {
                return new HttpGet(uri);
            } else {
                final int status = response.getStatusLine().getStatusCode();
                if (status == HttpStatus.SC_TEMPORARY_REDIRECT) {
                    request.removeHeaders(HTTP.CONTENT_LEN);
                    return RequestBuilder.copy(request).setUri(uri).build();
                } else {
                    return new HttpGet(uri);
                }
            }
        }

    };
    public static final int HTTP_UNLIMIT_TIMEOUT = 0;
    public static final int HTTP_TIMEOUT_2MIN = 120000;
    public static final int HTTP_TIMEOUT_3MIN = 180000;

    private static Integer DEFAULTHTTPTIMEOUT = 45000;

    @Value("${ios.appleaccount.ios_auth_server.close.cmd}")
    private String closeServerCmd = "close_server";


    public static Pair<String, HttpContext> getSignSapSetupCert(String ip, int port) {
        CloseableHttpClient httpClient = HttpClients.custom().setRedirectStrategy(ITUNES_HTTP_REDIRECT_STRATEGY)
                .build();
        HttpGet httpGet = new HttpGet(INIT_SIGN_SAP_SETUP_CERT_URL);
        httpGet.setHeader("User-Agent", ITUNES_USER_AGENT);
        httpGet.setHeader("Accept-Language", "zh-cn, zh;q=0.75, en-us;q=0.50, en;q=0.25");
        httpGet.setHeader("X-Apple-Tz", "28800");
        httpGet.setHeader("X-Apple-Store-Front", "143465-19,28");
        httpGet.setHeader("Accept-Encoding", "gzip");
        httpGet.setHeader("Connection", "close");
        httpGet.setHeader("Proxy-Connection", "close");

        int defaultTimeOut = DEFAULTHTTPTIMEOUT;
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom().setConnectTimeout(defaultTimeOut)
                .setConnectionRequestTimeout(defaultTimeOut).setSocketTimeout(defaultTimeOut);
        if (!StringUtils.isEmpty(ip)) {
            HttpHost proxy = new HttpHost(ip, port, "http");
            requestConfigBuilder.setProxy(proxy);
        }
        httpGet.setConfig(requestConfigBuilder.build());
        HttpContext httpContext = new HttpClientContext();
        String responseContent = null;
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet, httpContext);
            responseContent = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            throw new AppleAuthException(AppleAuthExceptionMeta.IOS_APPLE_GET_SIGN_SAP_SETUP_CERT_ERROR,
                    responseContent);
        }
        String cert = HttpResponseContentParser.signSapSetupCertParser.parser(responseContent);
        // logger.info(String.format("requestId:%s, signSapCert:%s",
        // ThreadGlobalUtil.getThreadRequestId(), cert));
        return Pair.of(cert, httpContext);
    }

    public static Pair<String, HttpContext> getSignSapSetup(String localSignBuffer, String ip, int port,
            HttpContext httpContext) {
        CloseableHttpClient httpClient = HttpClients.custom().setRedirectStrategy(ITUNES_HTTP_REDIRECT_STRATEGY)
                .setHostnameVerifier(AllowAllHostnameVerifier.INSTANCE).build();
        HttpPost httpPost = new HttpPost(SIGN_SAP_SETUP_URL);
        httpPost.setHeader("User-Agent", ITUNES_USER_AGENT);
        httpPost.setHeader("Referer", "http://itunes.apple.com");
        httpPost.setHeader("Accept-Language", "zh-cn, zh;q=0.75, en-us;q=0.50, en;q=0.25");
        httpPost.setHeader("X-Apple-Tz", "28800");
        httpPost.setHeader("X-Apple-Store-Front", "143465-19,28");
        httpPost.setHeader("Accept-Encoding", "gzip");
        httpPost.setHeader("Connection", "close");
        httpPost.setHeader("Proxy-Connection", "close");

        String postData = null;
        try {
            postData = buildPostSignSapSetupBuffer(localSignBuffer);
        } catch (IOException e) {
            throw ServiceException.getInternalException("build post signSapSetupBuffer failed");
        }
        httpPost.setEntity(new StringEntity(postData, ContentType.create(PLIST_CONTENT_TYPE)));

        int defaultTimeOut = DEFAULTHTTPTIMEOUT;
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom().setConnectTimeout(defaultTimeOut)
                .setConnectionRequestTimeout(defaultTimeOut).setSocketTimeout(defaultTimeOut);
        if (!StringUtils.isEmpty(ip)) {
            HttpHost proxy = new HttpHost(ip, port, "http");
            requestConfigBuilder.setProxy(proxy);
        }
        httpPost.setConfig(requestConfigBuilder.build());
        String responseContent = null;
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost, httpContext);
            responseContent = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            logger.error("post signSapSetupBuffer IOException", e);
            throw new AppleAuthException(AppleAuthExceptionMeta.IOS_APPLE_GET_SIGN_SAP_SETUP_BUFFER_ERROR,
                    responseContent);
        }
        String sapBuffer = HttpResponseContentParser.signSapSetupBufferParser.parser(responseContent);
        // logger.info(String.format("requestId:%s, signSapBuffer:%s",
        // ThreadGlobalUtil.getThreadRequestId(), sapBuffer));
        return Pair.of(sapBuffer, httpContext);
    }

    private static String buildPostSignSapSetupBuffer(String localSignBuffer) throws IOException {
        NSDictionary nsDictionary = new NSDictionary();
        NSData nsData = new NSData(localSignBuffer);
        nsDictionary.put("sign-sap-setup-buffer", nsData);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PropertyListParser.saveAsXML(nsDictionary, byteArrayOutputStream);
        return byteArrayOutputStream.toString();
    }

    private static final String AUTHENTICATE_URL = "https://buy.itunes.apple.com/WebObjects/MZFinance.woa/wa/authenticate";
    private static final Pattern xDsidPattern = Pattern.compile("<key>dsPersonId</key><string>([0-9]*)</string>");
    private static final Pattern xTokenPattern = Pattern
            .compile("<key>passwordToken</key><string>([a-zA-Z0-9=/\\+]*)</string>");
    private static final Pattern creditDisplayPattern = Pattern
            .compile("<key>creditDisplay</key><string>([楼\\$\\.0-9]*)</string>");


    public static class LoginResult {
        public HttpContext context;
        public String data;
    }

    public static Pair<AppleLoginResult, HttpContext> authenticate(String loginText, String actionSignatrue, String ip,
            Integer port) throws IOException {
        CloseableHttpClient httpClient = HttpClients.custom().setRedirectStrategy(ITUNES_HTTP_REDIRECT_STRATEGY)
                .build();
        HttpPost httpPost = new HttpPost(AUTHENTICATE_URL);
        httpPost.setHeader("Accept-Language", "zh-cn, zh;q=0.75, en-us;q=0.50, en;q=0.25");
        httpPost.setHeader("X-Apple-Tz", "28800");
        httpPost.setHeader("Connection", "close");
        httpPost.setHeader("Proxy-Connection", "close");
        httpPost.setHeader("User-Agent", ITUNES_USER_AGENT);
        httpPost.setHeader("X-Apple-ActionSignature", actionSignatrue);
        // Date: Mon, 30 Mar 2015 03:22:36 GMT
        // X-Apple-Store-Front: 143465-19,28
        httpPost.setHeader("Accept-Encoding", "gzip");
        // httpPost.setHeader("Host", host);

        String postData = loginText;
        httpPost.setEntity(new StringEntity(postData, ContentType.create(PLIST_CONTENT_TYPE)));

        int defaultTimeOut = DEFAULTHTTPTIMEOUT;
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom().setConnectTimeout(defaultTimeOut)
                .setConnectionRequestTimeout(defaultTimeOut).setSocketTimeout(defaultTimeOut);
        if (!StringUtils.isEmpty(ip) && port != null && port > 0 && port < (2 << 15)) {
            HttpHost proxy = new HttpHost(ip, port, "http");
            requestConfigBuilder.setProxy(proxy);
        }
        httpPost.setConfig(requestConfigBuilder.build());
        HttpContext httpContext = new HttpClientContext();
        LoginResult loginResult = new LoginResult();

        try {
            CloseableHttpResponse response = httpClient.execute(httpPost, httpContext);
            String responseContent = EntityUtils.toString(response.getEntity());
            loginResult.data = responseContent;
        } catch (ConnectTimeoutException ex) {
            loginResult.data = null;
        } catch (SocketTimeoutException ex) {
            loginResult.data = null;
        } catch (IOException ex) {
            loginResult.data = null;
        } finally {
            loginResult.context = httpContext;
        }
        AppleLoginResult appleLoginResult = HttpResponseContentParser.loginResultParse.parser(loginResult.data);
        if (appleLoginResult == null) {
            appleLoginResult = getAppleLoginResultFromLoginResult(loginResult);
        }
        return Pair.of(appleLoginResult, httpContext);
    }

    private static AppleLoginResult getAppleLoginResultFromLoginResult(LoginResult loginResult) throws IOException {
        AppleLoginResult appleLoginResult = new AppleLoginResult();

        Matcher matcher = xDsidPattern.matcher(loginResult.data);
        boolean find = matcher.find();
        String xDsid = null;
        if (find) {
            xDsid = matcher.group(1);
        }
        if (StringUtils.isBlank(xDsid)) {
            logger.error("AppleAccountAuthServerService.setAccountInfoFromLogin xDsid is empty, loginResult:"
                    + loginResult.data);
            throw new ServiceException(AuthResultCode.IOS_EXCEPTION_AUTH_FAILED_UNCAPTURED_ERROR,
                    "Some error from apple");
        }
        appleLoginResult.setxDsid(xDsid);

        Matcher xTokenMatcher = xTokenPattern.matcher(loginResult.data);
        find = xTokenMatcher.find();
        String xToken = null;
        if (find) {
            xToken = xTokenMatcher.group(1);
        }
        if (StringUtils.isBlank(xToken)) {
            logger.error("AppleAccountAuthServerService.setAccountInfoFromLogin xToken is empty, loginResult:"
                    + loginResult.data);
            throw new ServiceException(AuthResultCode.IOS_EXCEPTION_AUTH_FAILED_UNCAPTURED_ERROR,
                    "Some error from apple");
        }
        appleLoginResult.setxToken(xToken);

        Matcher creditMatcher = creditDisplayPattern.matcher(loginResult.data);
        find = creditMatcher.find();
        String creditDisplay = null;
        if (find) {
            creditDisplay = creditMatcher.group(1);
        }
        appleLoginResult.setCreditDisplay(creditDisplay);

        return appleLoginResult;
    }

    private static final String AUTH_MACHINE_URL = "https://buy.itunes.apple.com/WebObjects/MZFinance.woa/wa/authorizeMachine";

    private static final String AUTH_MACHINE_POST_DATA = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<plist version=\"1.0\">" + "<dict>" + "<key>guid</key>" + "<string>%s</string>" + "<key>kbsync</key>"
            + "<data>%s</data>" + "<key>machineName</key>" + "<string>%s</string>" + "<key>needDiv</key>"
            + "<string>1</string>" + "</dict>" + "</plist>";

    private static String getCookie(HttpContext httpContext, String cookieName) {
        CookieStore cookieStore = (CookieStore) httpContext.getAttribute("http.cookie-store");
        List<Cookie> cookies = cookieStore.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName) && cookie.getExpiryDate().after(new Date())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static AuthMachineResult authMachine(AccountInfo accountInfo, String ip, int port, HttpContext httpContext)
            throws IOException {
        CloseableHttpClient httpClient = HttpClients.custom().setRedirectStrategy(ITUNES_HTTP_REDIRECT_STRATEGY)
                .build();
        String itspod = getCookie(httpContext, "itspod");
        String authMachineUrl = AUTH_MACHINE_URL;
        if (StringUtils.isNoneBlank(itspod)) {
            authMachineUrl = String
                    .format("https://p%s-buy.itunes.apple.com/WebObjects/MZFinance.woa/wa/authorizeMachine", itspod);
        }
        HttpPost httpPost = new HttpPost(authMachineUrl);
        String postData = String.format(AUTH_MACHINE_POST_DATA, accountInfo.getGuid(), accountInfo.getKbsync(),
                accountInfo.getMachineName());
        httpPost.setEntity(new StringEntity(postData, ContentType.create(PLIST_CONTENT_TYPE)));
        HttpContext authMachineContext = new HttpClientContext();

        authMachineContext.setAttribute(HttpClientContext.COOKIE_STORE, httpContext.getAttribute("http.cookie-store"));

        httpPost.setHeader("X-Token", accountInfo.getxToken());
        httpPost.setHeader("X-Apple-Tz", "28800");
        httpPost.setHeader("User-Agent", ITUNES_USER_AGENT);
        httpPost.setHeader("X-Dsid", accountInfo.getxDsid());
        httpPost.setHeader("X-Apple-Store-Front", accountInfo.getxAppleStoreFront()); // "143465-19,28"
        // httpPost.setHeader("Referer", String.format(REFERER_FORMAT_STR,
        // machineGuid));
        // httpPost.setHeader("Date", DateUtil.nowGmtStr());

        int defaultTimeOut = DEFAULTHTTPTIMEOUT;
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom().setConnectTimeout(defaultTimeOut)
                .setConnectionRequestTimeout(defaultTimeOut).setSocketTimeout(defaultTimeOut);
        if (!StringUtils.isEmpty(ip)) {
            HttpHost proxy = new HttpHost(ip, port, "http");
            requestConfigBuilder.setProxy(proxy);
        }
        httpPost.setConfig(requestConfigBuilder.build());

        try {
            HttpResponse httpResponse = httpClient.execute(httpPost, authMachineContext);
            String output = EntityUtils.toString(httpResponse.getEntity());
            httpContext.setAttribute(HttpClientContext.COOKIE_STORE,
                    authMachineContext.getAttribute("http.cookie-store"));
            return HttpResponseContentParser.authMachineHttpResultParser.parser(output);
        } catch (IOException e) {
            logger.error("AuthMachineService.authMachine http IOException", e);
            return AuthMachineResult.AUTH_MACHINE_EXCEPTION;
        }

    }

    public static void setAccountInfoFromContext(HttpContext httpContext, AccountInfo accountInfo) {
        List<Cookie> cookies = ((CookieStore) httpContext.getAttribute("http.cookie-store")).getCookies();

        String cookieString = "";
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookieString += cookie.getName() + "=" + cookie.getValue() + "; ";
            }
        }
        accountInfo.setCookie(cookieString.trim());
    }
}
