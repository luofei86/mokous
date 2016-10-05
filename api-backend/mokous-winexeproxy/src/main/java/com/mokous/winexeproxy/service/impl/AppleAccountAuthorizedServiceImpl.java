// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.winexeproxy.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mokous.web.exception.ServiceException;
import com.mokous.web.utils.IOUtils;
import com.mokous.winexeproxy.service.AppleAccountAuthorizedService;
import com.mokous.winexeproxy.service.MachineInfoService;


/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
@Service("appleAccountAuthorizedService")
public class AppleAccountAuthorizedServiceImpl implements AppleAccountAuthorizedService {
    // private static final Logger log =
    // Logger.getLogger(AppleAccountAuthorizedServiceImpl.class);
    @Value("${mokous.winexeproxy.device.authorized.exe.path}")
    private String deviceAuthorizerExePath = "D:\\release-ok2\\release-ok2\\ios_mobile_operator.exe";
    @Value("${mokous.winexeproxy.pc.authorized.exe.path}")
    private String pcAuthorizerExePath = "D:\\release-ok2\\debug-05\\IosPCAuthorizer.exe";
    @Autowired
    private MachineInfoService machineInfoService;

    @PostConstruct
    public void init() {
        if (!new File(deviceAuthorizerExePath).exists()) {
            throw new RuntimeException("The exe path not found:" + deviceAuthorizerExePath);
        }
        if (!new File(pcAuthorizerExePath).exists()) {
            throw new RuntimeException("The exe path not found:" + pcAuthorizerExePath);
        }
    }

    private String buildIosMobileAuthorizerInfoCmd(String authJson, String appleId, String ikma, String ikmb) {
        if (StringUtils.isEmpty(ikma) || StringUtils.isEmpty(ikmb)) {
            return deviceAuthorizerExePath + " -op 2 -auth_info " + authJson + " -appleid " + appleId;
        } else {
            return deviceAuthorizerExePath + " -op 2 -auth_info " + authJson + " -appleid " + appleId + " -ikma "
                    + ikma + " -ikmb " + ikmb;
        }
    }

    // ios_mobile_operator.exe -op 6 -appleid liemi52@163.com -passwd Dd112211
    // -ikma BgAAAFYk8hDbswAAAAAAAAAAAAAAAAAA -ikmb
    // BgAAANa9To9boQAAAAAAAAAAAAAAAAAA -pcname win-18e164d0c34f -pcguid
    // 0EC938A3.EA545391.AB39FBE5.99BDDA0D.0C0C8134.F9C1AF31.8AB3DB63
    private String buildAuthorizerPcCmd(String appleId, String passwd) {
        return deviceAuthorizerExePath + " -op 6 -appleid " + appleId + " -passwd " + passwd;
    }

    private static String formatCmdJosnParameter(String authJson) {
        return authJson.replaceAll("\"", "\\\\\"");
    }

    // ios_mobile_operator.exe -op 2 -auth_info json字符串 -appleid liemi52@163.com
    // -ikma BgAAAFYk8hDbswAAAAAAAAAAAAAAAAAA -ikmb
    // BgAAANa9To9boQAAAAAAAAAAAAAAAAAA json字符串示例：
    // {"fairPlayCertificate":"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","fairPlayDeviceType":"82","fairPlayGUID":"ecd2ecc6244384dc71ad44fbeaafa18999e4608a","keyTypeSupportVersion":"492","keydata":"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"}
    @Override
    public String requestAuthroizedInfo(String appleId, String authJson, String ikma, String ikmb)
            throws ServiceException {
        if (StringUtils.isEmpty(authJson) || StringUtils.isEmpty(appleId)) {
            throw ServiceException.getParameterException("Apple account is not or auth json is illegal.");
        }
        authJson = formatCmdJosnParameter(authJson);
        String cmd = buildIosMobileAuthorizerInfoCmd(authJson, appleId, ikma, ikmb);
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            throw ServiceException.getInternalException(e.getMessage());
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String result;
        try {
            result = br.readLine();
        } catch (IOException e) {
            throw ServiceException.getInternalException(e.getMessage());
        } finally {
            IOUtils.close(br);
            try {
                proc.destroy();
            } catch (Exception e) {
            }
        }
        System.out.println(result);
        return result;
    }

    public static void main(String[] args) throws IOException {
        String appleId = "nue7913@163.com";
        String authJson = FileUtils.readFileToString(new File("D:\\auth.txt"));
        authJson = formatCmdJosnParameter(authJson);
        String cmd = "D:\\release-ok2\\release-ok2\\ios_mobile_operator.exe " + " -op 2 -auth_info " + authJson
                + " -appleid " + appleId;
        System.out.println(cmd);
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            throw ServiceException.getInternalException(e.getMessage());
        }
        try {
            System.out.println(org.apache.commons.io.IOUtils.readLines(proc.getInputStream()));
        } catch (IOException e1) {
        }
    }


    @Override
    public boolean authPcByExe(String appleId, String pwd, String ip, int port, boolean createSession)
            throws ServiceException {
        if (StringUtils.isEmpty(pwd) || StringUtils.isEmpty(appleId)) {
            throw ServiceException.getParameterException("Apple account is not or auth json is illegal.");
        }
        String cmd = buildAuthorizerPcCmd(appleId, pwd);
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            throw ServiceException.getInternalException(e.getMessage());
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String result;
        try {
            result = br.readLine();
        } catch (IOException e) {
            throw ServiceException.getInternalException(e.getMessage());
        } finally {
            IOUtils.close(br);
            try {
                proc.destroy();
            } catch (Exception e) {
            }
        }
        return StringUtils.contains(result, "\"success\":true");

    }
}
