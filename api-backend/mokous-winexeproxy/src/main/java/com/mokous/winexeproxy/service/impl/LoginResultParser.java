// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.winexeproxy.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSString;
import com.dd.plist.PropertyListParser;
import com.mokous.web.exception.ServiceException;
import com.mokous.winexeproxy.exception.AppleAuthException;
import com.mokous.winexeproxy.model.AppleAuthExceptionMeta;
import com.mokous.winexeproxy.model.AppleLoginResult;
import com.mokous.winexeproxy.model.AuthResultCode;
import com.mokous.winexeproxy.service.HttpResponseContentParser;



/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
public class LoginResultParser implements HttpResponseContentParser<AppleLoginResult> {
    private static final Logger logger = Logger.getLogger(LoginResultParser.class);

    private static final String failureTypeXpath = "/plist/dict/key[text() = 'failureType']";
    private static final String failureMessageXpath = "/plist/dict/dict/key[text() = 'message']";

    private final static Map<String, AppleAuthExceptionMeta> failureMap;
    static {
        failureMap = new HashMap<String, AppleAuthExceptionMeta>();
        failureMap.put("-5000", AppleAuthExceptionMeta.IOS_APPLE_ACCOUNT_WRONG);
        failureMap.put("5002", AppleAuthExceptionMeta.IOS_APPLE_UNKNOW_ERROR_FROM_APPLE);
        failureMap.put("5001", AppleAuthExceptionMeta.IOS_APPLE_ACCOUNT_UNUSED);

        failureMap.put("Your account is disabled.", AppleAuthExceptionMeta.IOS_APPLE_ACCOUNT_DISABLE);
        failureMap.put("您尚未验证自己的 Apple ID。", AppleAuthExceptionMeta.IOS_APPLE_ACCOUNT_UNVERIFIED);
        failureMap.put("You have not verified your Apple ID.", AppleAuthExceptionMeta.IOS_APPLE_ACCOUNT_UNVERIFIED);

    }

    @Override
    public AppleLoginResult parser(String content) {
        if (StringUtils.isBlank(content)) {
            throw new AppleAuthException(AuthResultCode.IOS_EXCEPTION_AUTH_TIME_OUT,
                    "Authenticate failed, maybe time out", content);
        }
        try {
            return parserByPlistParser(content);
        } catch (AppleAuthException e) {
            throw e;
        } catch (Exception e) {
            logger.error("LoginResultParser.parser parserByPlistParser Exception, content: " + content, e);
        }
        parserByString(content);
        return null;
    }

    private AppleLoginResult parserByPlistParser(String content) throws Exception {
        NSDictionary plistDict = null;
        try {
            plistDict = (NSDictionary) PropertyListParser.parse(new ByteArrayInputStream(content.getBytes("UTF-8")));
        } catch (IOException e) {
            content = content.substring(content.indexOf("<plist"));
            plistDict = (NSDictionary) PropertyListParser.parse(new ByteArrayInputStream(content.getBytes("UTF-8")));
        }
        if (plistDict == null) {
            logger.error("Login error not parsed, Some error from apple:" + content);
            throw new ServiceException(AuthResultCode.IOS_EXCEPTION_AUTH_FAILED_UNCAPTURED_ERROR,
                    "Some error from apple");
        }
        return buildAppleLoginResult(plistDict, content);
    }

    private AppleLoginResult buildAppleLoginResult(NSDictionary plistDict, final String content)
            throws ServiceException {
        NSDictionary accountInfo = (NSDictionary) plistDict.get("accountInfo");
        if (accountInfo == null) {
            NSString failure = (NSString) plistDict.get("failureType");
            if (failure == null) {
                throwFailure(null, content);
            }
            String failureType = failure.toString();
            if (!failureType.isEmpty()) {
                throwFailure(failureType, content);
            }
            NSDictionary dialog = (NSDictionary) plistDict.get("dialog");
            if (dialog != null) {
                NSString message = (NSString) dialog.get("message");
                if (message == null) {
                    throwFailure(null, content);
                }
                failureType = message.toString();
                throwFailure(failureType, content);
            }
            NSString customerMessage = (NSString) plistDict.get("customerMessage");
            if (customerMessage == null) {
                throwFailure(null, content);
            }
            String failureMessage = customerMessage.toString();
            throwFailure(null, failureMessage, content);
            return null;
        }
        AppleLoginResult loginResult = new AppleLoginResult();
        NSString passwordToken = (NSString) plistDict.get("passwordToken");
        loginResult.setxToken(passwordToken == null ? "" : passwordToken.toString());
        NSString dsPersonId = (NSString) plistDict.get("dsPersonId");
        loginResult.setxDsid(dsPersonId == null ? "" : dsPersonId.toString());
        NSString creditDisplay = (NSString) plistDict.get("creditDisplay");
        loginResult.setCreditDisplay(creditDisplay == null ? "" : creditDisplay.toString());
        NSString appleId = (NSString) accountInfo.get("appleId");
        loginResult.setAppleId(appleId == null ? "" : appleId.toString());
        NSString accountKind = (NSString) accountInfo.get("accountKind");
        loginResult.setAccountKind(accountKind == null ? "" : accountKind.toString());
        return loginResult;
    }

    @SuppressWarnings("unused")
    @Deprecated
    private Boolean parserByXpath(String content) throws ParserConfigurationException, SAXException,
            XPathExpressionException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        XPath xPath = XPathFactory.newInstance().newXPath();
        InputStream inputStream = null;
        Document xmlDocument = null;
        try {
            inputStream = new ByteArrayInputStream(content.getBytes("UTF-8"));
            xmlDocument = builder.parse(inputStream);
        } catch (UnsupportedEncodingException e) {
            logger.error("LoginResultErrorParser.parserByXpath UnsupportedEncodingException, content:" + content, e);
            return null;
        } catch (IOException e) {
            logger.error("LoginResultErrorParser.parserByXpath IOException, content:" + content, e);
            return null;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        String failureCode = parserXPath(xmlDocument, xPath, failureTypeXpath);
        if (null == failureCode) {
            return true;
        }
        if (!failureCode.isEmpty()) {
            throwFailure(failureCode, content);
        }
        String failureMsg = parserXPath(xmlDocument, xPath, failureMessageXpath);
        throwFailure(failureMsg, content);
        return false;
    }

    @Deprecated
    private String parserXPath(Document xmlDocument, XPath xPath, String xpathStr) throws XPathExpressionException {
        Node failureNode = (Node) xPath.compile(xpathStr).evaluate(xmlDocument, XPathConstants.NODE);
        if (failureNode == null) {
            return null;
        }
        Node failureValueNode = failureNode;
        String failureValueNodeContent = "";
        do {
            failureValueNode = failureValueNode.getNextSibling();
            failureValueNodeContent = failureValueNode.getNodeName();
        } while (failureValueNodeContent != null && failureValueNodeContent.startsWith("#text"));
        String failureCode = failureValueNode.getTextContent();
        if (failureCode == null) {
            return "";
        }
        return failureCode;
    }

    private void throwFailure(String failure, final String errorMsg, final String content) {
        if (failure == null && StringUtils.isNotBlank(errorMsg)) {
            throw new AppleAuthException(AuthResultCode.IOS_EXCEPTION_AUTH_FAILED_UNCAPTURED_ERROR, errorMsg, content);
        }
        AppleAuthExceptionMeta serviceExceptionMeta = failureMap.get(failure);
        if (serviceExceptionMeta == null) {
            throw new AppleAuthException(AppleAuthExceptionMeta.IOS_APPLE_UNCAPTURED_ERROR_FROM_APPLE, content);
        }
        throw new AppleAuthException(serviceExceptionMeta, content);
    }

    private void throwFailure(String failure, final String content) {
        throwFailure(failure, null, content);
    }

    private Boolean parserByString(String content) {
        if (!content.contains("<key>failureType</key>") && content.contains("<key>appleId</key>")) {
            return true;
        }
        if (content.contains("<key>message</key><string>Your account is disabled.</string>")) {
            throw new ServiceException(AuthResultCode.IOS_EXCEPTION_AUTH_FAILED_ACCOUNT_DISABLE,
                    "Your account is disabled.");
        } else if (content.contains("<key>failureType</key><string>-5000</string>")) {
            throw new AppleAuthException(failureMap.get("-5000"), content);
        } else if (content.contains("<key>failureType</key><string>5002</string>")) {
            throw new AppleAuthException(failureMap.get("5002"), content);
        } else if (content.contains("<key>failureType</key><string>5001</string>")) {
            throw new AppleAuthException(failureMap.get("5001"), content);
        } else if (content.contains("<key>message</key><string>您尚未验证自己的 Apple ID。</string>")
                || content.contains("You have not verified your Apple ID.")) {
            throw new ServiceException(AuthResultCode.IOS_EXCEPTION_AUTH_FAILED_UNVERIFY_APPLEID, "Not verify appleId");
        } else {
            logger.info("Login error not parsed, Some error from apple:" + content);
            throw new ServiceException(AuthResultCode.IOS_EXCEPTION_AUTH_FAILED_UNCAPTURED_ERROR,
                    "Some error from apple");
        }
    }

    public static void main(String[] args) {
        String content = "<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\"> \n"
                + "\n"
                + "  <plist version=\"1.0\">\n"
                + "    <dict>\n"
                + "       \n"
                + "  \n"
                + "    \n"
                + "    \n"
                + "    \n"
                + "         \n"
                + "    \n"
                + "    \n"
                + "        <key>accountInfo</key>\n"
                + "        <dict>\n"
                + "    <key>appleId</key><string>zx19870702@icloud.com</string>\n"
                + "    <key>accountKind</key><string>0</string>\n"
                + "    <key>address</key>\n"
                + "    <dict>\n"
                + "      <key>firstName</key><string>贤</string>\n"
                + "      <key>lastName</key><string>张</string>\n"
                + "    </dict>\n"
                + "  </dict>\n"
                + "        <key>passwordToken</key><string>AwIAAAECAAHUwQAAAABVOEJ3lYoGmLWB2SSu04KwiuC+jujZNZA=</string>\n"
                + "        <key>clearToken</key><string>30303030303031343239333338343934</string>\n"
                + "        <key>m-allowed</key><true/>\n"
                + "        \n"
                + "        \n"
                + "        \n"
                + "        <key>dsPersonId</key><string>8300808404</string>\n"
                + "<key>creditDisplay</key><string></string>\n"
                + "\n"
                + "<key>creditBalance</key><string>1311811</string>\n"
                + "<key>freeSongBalance</key><string>1311811</string>\n"
                + "\n"
                + "  <key>creditDisplayInternal</key><string>¥.00+0+0+0+0+0</string>  \n"
                + "\n"
                + "\n"
                + "        \n"
                + "        \n"
                + "        <key>action</key><dict><key>kind</key><string>SetUserAndReset</string></dict>\n"
                + "        \n"
                + "        \n"
                + "        <key>subscriptionStatus</key><dict>\n"
                + "    \n"
                + "    \n"
                + "</dict>\n"
                + "\n"
                + "    \n"
                + "    \n"
                + "    <key>status</key><integer>-128</integer>\n"
                + "    \n"
                + "    \n"
                + "    <key>download-queue-info</key>\n"
                + "    <dict>\n"
                + "        <key>download-queue-item-count</key><integer>0</integer>\n"
                + "        <key>dsid</key><integer>8300808404</integer>\n"
                + "        <key>is-auto-download-machine</key><false/>\n"
                + "    </dict>\n"
                + "    \n"
                + "    \n"
                + "    \n"
                + "       \n"
                + "       \n"
                + "    \n"
                + "      <key>dialog</key>\n"
                + "      <dict>\n"
                + "    \n"
                + "    \n"
                + "    <key>m-allowed</key><false/>\n"
                + "      \n"
                + "    <key>message</key><string>此 Apple ID 只能在 iTunes Store 中国店面购物。您将被转至该店面。</string>\n"
                + "    <key>explanation</key><string> </string>\n"
                + "    <key>defaultButton</key><string>ok</string>\n"
                + "\n"
                + "    \n"
                + "    <key>okButtonString</key><string>好</string>\n"
                + "    <key>okButtonAction</key><dict>\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "    <key>kind</key><string>Goto</string>\n"
                + "    <key>url</key><string>https://itunes.apple.com/WebObjects/MZStore.woa/wa/storeFront</string>\n"
                + "    \n"
                + "    \n"
                + "    \n"
                + "    \n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "</dict>\n"
                + "\n"
                + "\n"
                + "    \n"
                + "    \n"
                + "\n"
                + "    \n"
                + "    \n"
                + "\n"
                + "    \n"
                + "    \n"
                + "    \n"
                + "    \n"
                + "</dict>\n"
                + "\n"
                + "    \n"
                + "    \n"
                + "    \n"
                + "    \n"
                + "    \n"
                + "    \n"
                + "    \n"
                + "    \n"
                + "    \n"
                + "    \n"
                + "    \n"
                + "    \n"
                + "    \n"
                + "    \n"
                + "  \n"
                + "\n"
                + "\n"
                + "    \n"
                + "    </dict>\n"
                + "  </plist>";

        try {
            AppleLoginResult appleLoginResult = new LoginResultParser().parserByPlistParser(content);
            System.out.println(appleLoginResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
