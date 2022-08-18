//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.db.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mokous.base.utils.DateFormatPatternUtils;
import com.mokous.base.utils.HttpClientUtils;
import com.mokous.base.utils.NormalUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

/**
 * @author luofei
 * Generate 2020/2/2
 */
public class PasswordProvider {
    private static final Logger logger = LoggerFactory.getLogger(PasswordProvider.class);
    private String url;
    private String appId;
    private String safe;
    private String folder;
    private String key;
    private Random random = new Random();

    public PasswordProvider() {
    }

    public String getPassword(String name) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requestId", this.genRequestId());
        jsonObject.put("requestTime", this.genRequestTime());
        jsonObject.put("appId", appId);
        jsonObject.put("safe", safe);
        jsonObject.put("folder", folder);
        jsonObject.put("object", name);
        jsonObject.put("sign", this.genSign());
        String jsonStr = jsonObject.toJSONString();
        String resp = null;
        try {
            resp = HttpClientUtils.postJsonString(this.url, (Map) null, (Map) null, jsonStr, true);
            JSONObject result = JSON.parseObject(resp);
            return this.decrypt(result.getString("password"));
        } catch (Exception e) {
            logger.error("Get password from url: {} by {} failed. Error:{}, Resp:{}", url, jsonObject, e.getMessage(),
                    resp);
        }
        return null;
    }

    private String genSign() {
        StringBuilder sb = new StringBuilder(appId);
        sb.append("&").append(this.key);
        return DigestUtils.sha1Hex(sb.toString());
    }

    private String decrypt(String password) throws Exception {
        return NormalUtils.decrypt(password, this.key);
    }

    private String genRequestTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormatPatternUtils.YMDHMS_FORMAT);
        return sdf.format(new Date());
    }

    private String genRequestId() {
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormatPatternUtils.YMDHMS_FORMAT);
        StringBuilder sb = new StringBuilder("");
        sb.append(sdf.format(new Date()));
        String str = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < 6; i++) {
            int pos = this.random.nextInt(str.length());
            sb.append(str.charAt(pos));
        }
        return sb.toString();
    }

    public static Logger getLogger() {
        return logger;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSafe() {
        return safe;
    }

    public void setSafe(String safe) {
        this.safe = safe;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
