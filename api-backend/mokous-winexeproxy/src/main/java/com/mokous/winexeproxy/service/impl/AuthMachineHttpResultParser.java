// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.winexeproxy.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.dd.plist.NSData;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSNumber;
import com.dd.plist.NSString;
import com.dd.plist.PropertyListParser;
import com.mokous.winexeproxy.model.AuthMachineResult;
import com.mokous.winexeproxy.service.HttpResponseContentParser;


/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
public class AuthMachineHttpResultParser implements HttpResponseContentParser<AuthMachineResult> {
    private static final Logger logger = Logger.getLogger(AuthMachineHttpResultParser.class);

    private final static Map<String, AuthMachineResult> failureMap;
    static {
        failureMap = new HashMap<String, AuthMachineResult>();
        failureMap.put("3002", AuthMachineResult.AUTH_MACHINE_FULL);
        failureMap.put("1001", AuthMachineResult.AUTH_MACHINE_NOACCOUNT_MESSAGE);
        failureMap.put("2034", AuthMachineResult.AUTH_MACHINE_EXPIRED_PASSWORD_TOKEN);
    }

    @Override
    public AuthMachineResult parser(String content) {
        if (StringUtils.isBlank(content)) {
            return AuthMachineResult.AUTH_MACHINE_EMPTY_RESULT_TIMEOUT;
        }
        try {
            AuthMachineResult authMachineResult = parserByPlistParser(content);
            if (authMachineResult != null) {
                return authMachineResult;
            }
        } catch (Exception e) {
            logger.error("AuthMachineHttpResultParser.parser Exception, content:" + content, e);
        }
        return AuthMachineResult.AUTH_MACHINE_UNKNOWN_ERROR;
    }

    private AuthMachineResult parserByPlistParser(String content) throws Exception {
        NSDictionary plistDict = null;
        try {
            plistDict = (NSDictionary) PropertyListParser.parse(new ByteArrayInputStream(content.getBytes("utf8")));
        } catch (IOException e) {
            content = content.substring(content.indexOf("<plist"));
            plistDict = (NSDictionary) PropertyListParser.parse(new ByteArrayInputStream(content.getBytes("utf8")));
        }
        if (plistDict == null) {
            if (content
                    .contains("<html><head><title>Error</title></head><body>Your request produced an error.  <BR>[newNullResponse]</body></html>")) {
                logger.info("AuthMachineHttpResultParser.parserByPlistParser Apple 500， content:" + content);
                return AuthMachineResult.AUTH_MACHINE_APPLE_500_ERROR;
            }
            logger.info("AuthMachineHttpResultParser.parserByPlistParser authMachine failed unknown error, content:"
                    + content);
            return AuthMachineResult.AUTH_MACHINE_UNKNOWN_ERROR;
        }
        return buildAuthMachineResult(plistDict, content);
    }

    private AuthMachineResult buildAuthMachineResult(NSDictionary plistDict, String content) {
        NSString failureType = (NSString) plistDict.get("failureType");
        if (failureType == null) {
            NSNumber status = (NSNumber) plistDict.get("status");
            if (status == null) {
                logger.info("AuthMachineHttpResultParser.parserByPlistParser authMachine failed unknown error, content:"
                        + content);
                return AuthMachineResult.AUTH_MACHINE_UNKNOWN_ERROR;
            }
            NSData keybag = (NSData) plistDict.get("keybag");
            NSData diversitybag = (NSData) plistDict.get("diversitybag");
            AuthMachineResult authMachineResult = new AuthMachineResult(status.intValue());
            authMachineResult.setKeyBag(keybag.getBase64EncodedData());
            authMachineResult.setDiversitybag(diversitybag.getBase64EncodedData());
            return authMachineResult;
        }
        String failureCode = failureType.toString();
        AuthMachineResult authMachineResult = null;
        if (StringUtils.isNotBlank(failureCode)) {
            AuthMachineResult known = failureMap.get(failureCode);
            if (known != null) {
                authMachineResult = known;
            }
        }
        if (authMachineResult == null) {
            logger.info("AuthMachineHttpResultParser.parserByPlistParser authMachine failed unknown error, content:"
                    + content);
            return AuthMachineResult.AUTH_MACHINE_UNKNOWN_ERROR;
        }
        return authMachineResult;
    }

}
