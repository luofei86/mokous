// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.apple.core.utils;

import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;

import redis.clients.jedis.ShardedJedisPool;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSNumber;
import com.dd.plist.NSObject;
import com.dd.plist.NSString;
import com.dd.plist.PropertyListParser;
import com.mokous.apple.core.cache.appleaccount.login.AccountInfo;
import com.mokous.apple.core.cache.appleaccount.login.AuthActionResponse;
import com.mokous.core.service.AsyncService;
import com.mokous.core.utils.HttpUtil;
import com.mokous.core.utils.RedisCacheUtils;
import com.mokous.core.utils.RespParser;
import com.mokous.core.utils.RespParserWithHeaders;
import com.mokous.core.utils.RespParser.FullResp;
import com.mokous.web.model.ApiRespWrapper;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
public class AppleAccountLoginUtils {
    
    private static final String appleLoginServerItunesAuthRespIdKey = "itunesresp:";
    private static final String appleLoginServerItunesAuthRespIdV1Key = "itunesresp:v1:";

    // use apple login cache
    private static final int APPLE_LOGIN_RESULT_EXPIRE_ONE_SEC = 1;

    public static boolean needAuth(Integer auth) {
        return auth != null && auth.intValue() == 0;
    }

    public static boolean needRefresh(Integer refresh) {
        return refresh == null || refresh == 1;
    }

    /**
     * 不再使用apple session
     * 
     * @param session
     * @param clientVersion
     * @return
     */
    public static boolean needAppleSession(Integer session, String clientVersion) {
        return false;
    }

    public static boolean needConfirmTerm(String clientVersion) {
        // TODO
        return false;
    }


    /**
     * 此key的生成方式与appleaccount的方式完全一样
     */
    public static String buildAppleLoginServerAuthRespKey(String email, boolean appleSession) {
        if (appleSession) {
            return appleLoginServerItunesAuthRespIdV1Key + email.toLowerCase();
        } else {
            return appleLoginServerItunesAuthRespIdKey + email.toLowerCase();
        }
    }


    public static void asyncPut(String email, boolean createAppleSession, ShardedJedisPool shardedJedisPool,
            AsyncService asyncService, final ApiRespWrapper<AuthActionResponse> itunesAuthResp) {
        final String key = buildAppleLoginServerAuthRespKey(email, createAppleSession);
        RedisCacheUtils.asyncSetexObject(asyncService, key, itunesAuthResp, shardedJedisPool);
    }


    public static ApiRespWrapper<AuthActionResponse> getAppleAccountAsyncLoginResp(String email, boolean param,
            ShardedJedisPool shardedAppleAccountJedisPool) {
        String key = AppleAccountLoginUtils.buildAppleLoginServerAuthRespKey(email, param);
        ApiRespWrapper<AuthActionResponse> itunesAuthResp = RedisCacheUtils.getObject(key,
                APPLE_LOGIN_RESULT_EXPIRE_ONE_SEC, shardedAppleAccountJedisPool);
        RedisCacheUtils.del(key, shardedAppleAccountJedisPool);
        return itunesAuthResp;
    }


    private static final Logger log = Logger.getLogger(AppleAccountLoginUtils.class);

    private static final String ITUNES_USER_AGENT = "iTunes/12.0.1 (Windows; Microsoft Windows 7 x64 Ultimate Edition Service Pack 1 (Build 7601)) AppleWebKit/7600.1017.0.24";
    private static final Map<String, String> COMMON_HEADS = new HashMap<String, String>() {
        /**
         * 
         */
        private static final long serialVersionUID = 5029820951511074294L;

        {
            put("Accept-Language", "zh-cn, zh;q=0.75, en-us;q=0.50, en;q=0.25");
            put("X-Apple-Tz", "28800");
            put("Connection", "close");
            put("Proxy-Connection", "close");
            put("User-Agent", ITUNES_USER_AGENT);
            put("Accept-Encoding", "gzip");
        }
    };

    private static Map<String, String> parseMap(String content) {
        Map<String, String> result = new HashMap<String, String>();
        String[] splits = content.split(";");
        for (String split : splits) {
            split = split.trim();
            int idx = split.indexOf('=');
            if (idx > 0) {
                String key = split.substring(0, idx);
                String val = split.substring(idx + 1);
                result.put(key, val);
            }
        }
        return result;
    }

    private static FullResp authenticate(HttpContext context, Map<String, String> headers, String itspod,
            String appleID, String passWD, String xAppleStoreFront, String xAppleActionSignature, String guid,
            String machineName) {
        String body = getAuthenticateBody(appleID, passWD, guid, machineName);
        String url = null;
        if (null != itspod)
            url = "https://p" + itspod + "-buy.itunes.apple.com/WebObjects/MZFinance.woa/wa/authenticate";
        else
            url = "https://buy.itunes.apple.com/WebObjects/MZFinance.woa/wa/authenticate";

        FullResp resp = HttpUtil.url(url).headers(headers).header("Content-Type", "application/x-apple-plist")
                .header("X-Apple-Store-Front", xAppleStoreFront)
                .header("X-Apple-ActionSignature", xAppleActionSignature).entity(body)
                .doPost(new RespParserWithHeaders(), context);
        if (resp.status == 307) {
            url = resp.headers.get("location").get(0);
            resp = HttpUtil.url(url).headers(headers).header("X-Apple-Store-Front", xAppleStoreFront)
                    .header("X-Apple-ActionSignature", xAppleActionSignature).entity(body)
                    // .proxy(new HttpHost("127.0.0.1", 8888))
                    .doPost(new RespParserWithHeaders(), context);
        }
        return resp;
    }

    private static String getAuthenticateBody(String appleId, String password, String guid, String machineName) {
        StringBuilder plist = new StringBuilder();
        plist.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">"
                + "<plist version=\"1.0\">" + "<dict>" + "<key>appleId</key>" + "<string>" + appleId + "</string>"
                + "<key>attempt</key><integer>1</integer>" + "<key>guid</key><string>" + guid + "</string>"
                + "<key>machineName</key><string>" + machineName + "</string>" + "<key>password</key><string>"
                + password + "</string>" + "<key>why</key><string>purchase</string>" + "</dict>" + "</plist>");
        return plist.toString();
    }

    private static NSObject parseContent(String content) {
        try {
            return PropertyListParser.parse(new ByteArrayInputStream(content.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static FullResp buyProduct(HttpContext context, String itspod, Map<String, String> headers, String appId,
            String xToken, String xDsid, String xAppleStoreFront, String creditDisplay, String guid, String kbsync,
            String machineName) {
        String url = null;
        if (null != itspod) {
            url = "https://p" + itspod + "-buy.itunes.apple.com/WebObjects/MZBuy.woa/wa/buyProduct";
        } else {
            url = "https://buy.itunes.apple.com/WebObjects/MZBuy.woa/wa/buyProduct";
        }
        return HttpUtil.url(url).headers(headers).header("X-Token", xToken).header("X-Dsid", xDsid)
                .header("X-Apple-Store-Front", xAppleStoreFront)
                .header("Content-Type", "application/x-apple-plist;charset=UTF-8")
                .header("Referer", "http://itunes.apple.com/cn/")
                .entity(getBuyProductBody(appId, creditDisplay, guid, kbsync, machineName))
                // .proxy(new HttpHost("127.0.0.1", 8888))
                .doPost(new RespParserWithHeaders(), context);
    }

    private static String getBuyProductBody(String appId, String credit, String guid, String kbsync, String machineName) {
        StringBuilder body = new StringBuilder();
        body.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        body.append("<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">");
        body.append("<plist version=\"1.0\"><dict>");
        body.append("<key>buyAndSkipHarvesting</key><string>true</string>");
        body.append("<key>buyWithoutAuthorization</key><string>true</string>");
        body.append("<key>creditDisplay</key><string>" + credit + "</string>");
        body.append("<key>guid</key><string>" + guid + "</string>");
        body.append("<key>hasAskedToFulfillPreorder</key><string>true</string>");
        body.append("<key>hasDoneAgeCheck</key><string>true</string>");
        body.append("<key>kbsync</key><data>" + kbsync + "</data>");
        body.append("<key>machineName</key><string>" + machineName + "</string>");
        body.append("<key>needDiv</key><string>1</string>");
        body.append("<key>origPage</key><string>Software</string>");
        body.append("<key>origPage2</key><string>Genre</string>");
        body.append("<key>origPageCh</key><string>Software Pages</string>");
        body.append("<key>origPageLocation</key><string>Buy</string>");
        body.append("<key>price</key><string>0</string>");
        body.append("<key>pricingParameters</key><string>STDQ</string>");
        body.append("<key>productType</key><string>C</string>");
        body.append("<key>salableAdamId</key><string>" + appId + "</string>");
        body.append("<key>wasWarnedAboutFirstTimeBuy</key><string>true</string>");
        body.append("</dict></plist>");
        return body.toString();

    }

    public static void confirmTerm(String appleId, String passwd, AccountInfo data) {
        String xAppleStoreFront = data.getxAppleStoreFront();
        String xAppleActionSignature = data.getxAppleActionSignature();
        String guid = data.getGuid();
        String machineName = data.getMachineName();

        HttpContext context;
        BasicCookieStore cookieStore = new BasicCookieStore();

        context = new BasicHttpContext();

        context.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);

        String itspod = parseMap(data.cookie).get("itspod");
        FullResp fulResp = authenticate(context, COMMON_HEADS, itspod, appleId, passwd, xAppleStoreFront,
                xAppleActionSignature, guid, machineName);
        updateCookie(fulResp, cookieStore);

        NSDictionary authDict = (NSDictionary) parseContent(fulResp.content);
        if (authDict.containsKey("subscriptionStatus")) {
            NSDictionary subscriptionStatus = (NSDictionary) authDict.get("subscriptionStatus");
            NSArray termsList = (NSArray) subscriptionStatus.get("terms");
            NSDictionary termDict = (NSDictionary) termsList.objectAtIndex(0);
            String latestTerms = parsePlistNumber("latestTerms", termDict);
            String agreedToTerms = parsePlistNumber("agreedToTerms", termDict);
            if (StringUtils.equals(latestTerms, agreedToTerms)) {
                log.info("The user agreed all terms.");
                return;
            } else {
                log.info("Do confirm term. AppleId:" + appleId);
                doConfirmTerm(authDict, data, context, cookieStore);
            }
        }

    }

    private static void doConfirmTerm(NSDictionary authDict, AccountInfo data, HttpContext context,
            BasicCookieStore cookieStore) {
        String itspod = parseMap(data.cookie).get("itspod");
        String creditDisplay = parsePlistString("creditDisplay", authDict);
        FullResp buyProductRes = buyProduct(context, itspod, COMMON_HEADS, "350962117", data.getxToken(),
                data.getxDsid(), data.getxAppleStoreFront(), creditDisplay, data.getGuid(), data.getKbsync(),
                data.getMachineName());
        updateCookie(buyProductRes, cookieStore);
        NSDictionary buyDict = (NSDictionary) parseContent(buyProductRes.content);
        String jingleDocType = parsePlistString("jingleDocType", buyDict);
        if ("buyProductFailure".equals(jingleDocType)) {
            NSDictionary actionDict = (NSDictionary) buyDict.get("action");
            String url = parsePlistString("url", actionDict);
            FullResp confirmTermResp = getConfirmTermResp(buyDict, data, context);
            updateCookie(confirmTermResp, cookieStore);
            String termHtml = confirmTermResp.content;
            postTermConfirm(termHtml, itspod, data, url, context);
            log.info("Finish confirm term the result.");
        } else {
            log.warn("Infact when u see this, it always means a error occur.Buy product res:" + buyProductRes.content);
        }
    }

    private static String postTermConfirm(String termHtml, String itspod, AccountInfo data, String refererUrl,
            HttpContext context) {
        String reqPart = getString(termHtml, 0, "7_11_3\" method=\"post\" action=\"", "\"");
        String reqUrl = "https://p" + itspod + "-buy.itunes.apple.com" + reqPart;
        String form1 = getString(termHtml, 0,
                "<input class=\"checkbox after\" id=\"iagree\" type=\"checkbox\" name=\"", "\"");
        String form2 = getString(termHtml, 0,
                "<input class=\"submit\" id=\"continue\" type=\"submit\" value=\"同意\" name=\"", "\"");
        String body = form1 + "=" + form1 + "&" + form2 + "=%E5%90%8C%E6%84%8F";
        FullResp acceptTermResp = HttpUtil.url(reqUrl).headers(COMMON_HEADS).header("X-Token", data.getxToken())
                .header("X-Dsid", data.getxDsid()).header("Referer", refererUrl)
                .header("X-Apple-Store-Front", data.getxAppleStoreFront()).header("X-Apple-Tz", "28800")
                .entity(body, ContentType.APPLICATION_FORM_URLENCODED).doPost(new RespParserWithHeaders(), context);
        return acceptTermResp.content;
    }

    private static FullResp getConfirmTermResp(NSDictionary buyDict, AccountInfo data, HttpContext context) {
        NSDictionary actionDict = (NSDictionary) buyDict.get("action");
        String url = parsePlistString("url", actionDict);
        return HttpUtil.url(url).headers(COMMON_HEADS).header("Referer", url).header("X-Token", data.getxToken())
                .header("X-Dsid", data.getxDsid()).header("X-Apple-Tz", "28800")
                .header("X-Apple-Store-Front", data.getxAppleStoreFront()).doPost(new RespParserWithHeaders(), context);
    }


    private static void updateCookie(FullResp resp, BasicCookieStore cookieStore) {
        List<String> setCookies = resp.headers.get("set-cookie");
        for (String setCookie : setCookies) {
            CookieJudger judger = parseCookie(setCookie);
            boolean banned = false;
            for (String bannedName : bannedNames) {
                if (bannedName.equals(judger.cookie.getName())) {
                    banned = true;
                }
            }
            if (banned)
                continue;
            if (!judger.delete) {
                boolean has = false;
                List<Cookie> cookies = cookieStore.getCookies();
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(judger.cookie.getName())) {
                        has = true;
                        break;
                    }
                }
                if (!has)
                    cookieStore.addCookie(judger.cookie);
            }
        }
    }

    public static class CookieJudger {
        public BasicClientCookie cookie;
        public boolean delete;
    }

    static String[] bannedNames = new String[] { "hasPreorders", "suppressPreorderCheck" };
    public static SimpleDateFormat APPLE_DATE_SDF = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss zzz",
            Locale.ENGLISH);
    static {
        APPLE_DATE_SDF.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private static CookieJudger parseCookie(String setCookieStr) {
        String[] splits = setCookieStr.split("; ");
        String key = null;
        String value = null;
        String version = null;
        String expires = null;
        String path = null;
        String comment = null;
        String domain = null;
        boolean secure = false;

        boolean shouldDelete = false;
        for (String str : splits) {
            str = str.trim();
            int idx = str.indexOf('=');
            if (idx > 0) {
                String pre = str.substring(0, idx).trim();
                String after = str.substring(idx + 1).trim();
                switch (pre) {
                    case "version":
                        version = after;
                        break;
                    case "expires":
                        expires = after;
                        if (expires.equals("Thu, 01-Jan-1970 00:00:00 GMT"))
                            shouldDelete = true;
                        break;
                    case "path":
                        path = after;
                        break;
                    case "domain":
                        domain = after;
                        break;
                    case "Max-Age":
                        break;
                    default:
                        key = pre;
                        value = after;
                        break;
                }
            } else {
                switch (str) {
                    case "secure":
                        secure = true;
                        break;
                    default:
                        comment = str;
                        break;
                }
            }
        }


        BasicClientCookie cookie = new BasicClientCookie(key, value);
        if (null != path)
            cookie.setPath(path);
        if (null != domain)
            cookie.setDomain(domain);
        if (null != expires) {
            try {
                cookie.setExpiryDate(APPLE_DATE_SDF.parse(expires));
            } catch (ParseException e) {
                // e.printStackTrace();
            }
        }
        if (null != version) {
            version = version.replace("\"", "");
            cookie.setVersion(Integer.parseInt(version));
        } else {
            cookie.setVersion(0);
        }
        if (null != comment)
            cookie.setComment(comment);
        cookie.setSecure(secure);
        if (!shouldDelete) {
            Date expireDate = cookie.getExpiryDate();
            if (null != expireDate)
                shouldDelete = expireDate.before(new Date());
        }

        CookieJudger judger = new CookieJudger();

        judger.cookie = cookie;
        judger.delete = shouldDelete;
        return judger;
    }

    private static String parsePlistString(String key, NSDictionary dict) {
        NSString val = (NSString) dict.get(key);
        if (null != val) {
            return val.getContent();
        } else
            return null;
    }

    private static String parsePlistNumber(String key, NSDictionary dict) {
        NSNumber val = (NSNumber) dict.get(key);
        if (null != val) {
            return val.toString();
        } else
            return null;
    }

    private static String getString(String content, int idx, String prefix, String endix) {
        String val = null;
        int startIdx = content.indexOf(prefix, idx);
        if (startIdx > 0) {
            int endIdx = content.indexOf(endix, startIdx + prefix.length());
            if (endIdx > 0) {
                val = content.substring(startIdx + prefix.length(), endIdx);
            }
        }
        return val;
    }

}
