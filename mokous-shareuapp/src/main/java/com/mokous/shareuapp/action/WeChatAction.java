// Copyright 2016 https://mokous.com Inc. All Rights Reserved.

package com.mokous.shareuapp.action;

import java.util.Arrays;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mokous.web.action.BaseAction;


/**
 * @author mokous86@gmail.com create date: Dec 9, 2016
 *
 */
@Controller
@RequestMapping("/*")
public class WeChatAction extends BaseAction {
    private static final String TOKEN = "e858a30b53e5cc2cefe97d35581e4cf1";

    @RequestMapping(value = "/go.json", method = RequestMethod.GET)
    @ResponseBody
    protected String getGo(String signature, String timestamp, String nonce, String echostr) {
        String[] paras = new String[] { TOKEN, timestamp, nonce };
        Arrays.sort(paras);
        if (StringUtils.equals(signature, DigestUtils.sha1Hex(paras[0] + paras[1] + paras[2]))) {
            return echostr;
        } else {
            return "error";
        }
    }



    @RequestMapping(value = "/go.json", method = RequestMethod.POST)
    @ResponseBody
    protected String postGo(String signature, String timestamp, String nonce, String echostr) {
        String[] paras = new String[] { TOKEN, timestamp, nonce };
        Arrays.sort(paras);
        if (StringUtils.equals(signature, DigestUtils.sha1Hex(paras[0] + paras[1] + paras[2]))) {
            return echostr;
        } else {
            return "error";
        }
    }

}
