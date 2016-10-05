// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.winexeproxy.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.dd.plist.NSData;
import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListFormatException;
import com.dd.plist.PropertyListParser;
import com.mokous.winexeproxy.exception.AppleAuthException;
import com.mokous.winexeproxy.model.AppleAuthExceptionMeta;
import com.mokous.winexeproxy.service.HttpResponseContentParser;


/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
public class SignSapSetupBufferParser implements HttpResponseContentParser<String> {

    private static final Logger logger = Logger.getLogger(SignSapSetupBufferParser.class);

    @Override
    public String parser(String content) {
        if (StringUtils.isBlank(content)) {
            throw new AppleAuthException(AppleAuthExceptionMeta.IOS_APPLE_GET_SIGN_SAP_SETUP_BUFFER_ERROR, content);
        }
        String result = null;
        try {
            result = parserByPlistParser(content);
        } catch (Exception e) {
            logger.error("SignSapSetupBufferParser.parser parserByPlistParser Exception, content: " + content, e);
        }
        if (StringUtils.isBlank(result)) {
            throw new AppleAuthException(AppleAuthExceptionMeta.IOS_APPLE_GET_SIGN_SAP_SETUP_BUFFER_ERROR, content);
        }
        return result;
    }

    private String parserByPlistParser(String content) throws ParserConfigurationException, ParseException,
            SAXException, PropertyListFormatException, IOException {
        NSDictionary plistDict = null;
        try {
            plistDict = (NSDictionary) PropertyListParser.parse(new ByteArrayInputStream(content.getBytes("UTF-8")));
        } catch (IOException e) {
            content = content.substring(content.indexOf("<plist"));
            plistDict = (NSDictionary) PropertyListParser.parse(new ByteArrayInputStream(content.getBytes("UTF-8")));
        }
        if (plistDict == null) {
            return null;
        }
        NSData nsString = (NSData) plistDict.get("sign-sap-setup-buffer");
        return nsString == null ? "" : nsString.getBase64EncodedData();
    }
}
