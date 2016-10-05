// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.winexeproxy.service;

import com.mokous.winexeproxy.service.impl.AuthMachineHttpResultParser;
import com.mokous.winexeproxy.service.impl.LoginResultParser;
import com.mokous.winexeproxy.service.impl.SignSapSetupBufferParser;
import com.mokous.winexeproxy.service.impl.SignSapSetupCertParser;


/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
public interface HttpResponseContentParser<G> {
    public static final LoginResultParser loginResultParse = new LoginResultParser();
    public static final AuthMachineHttpResultParser authMachineHttpResultParser = new AuthMachineHttpResultParser();
    public static final SignSapSetupCertParser signSapSetupCertParser = new SignSapSetupCertParser();
    public static final SignSapSetupBufferParser signSapSetupBufferParser = new SignSapSetupBufferParser();

    G parser(String content);
}
