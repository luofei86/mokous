// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.winexeproxy.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mokous.core.cache.appleaccount.login.AccountInfo;
import com.mokous.core.cache.appleaccount.login.MachineInfo;
import com.mokous.web.exception.ServiceException;
import com.mokous.web.utils.IOUtils;
import com.mokous.winexeproxy.model.AppleLoginResult;
import com.mokous.winexeproxy.model.AuthResultCode;
import com.mokous.winexeproxy.service.AppleAccountLoginService;
import com.mokous.winexeproxy.service.MachineInfoService;
import com.mokous.winexeproxy.utils.AppleAuthUtils;
import com.mokous.winexeproxy.utils.ItunesNetworkUtils;


/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
@Service("appleAccountLoginService")
public class AppleAccountLoginServiceImpl implements AppleAccountLoginService {
    private static final Logger log = Logger.getLogger(AppleAccountLoginServiceImpl.class);
    @Autowired
    private MachineInfoService machineInfoService;
    @Value("${ios.appchina.winexe.proxy.pc.authorized.exe.path}")
    private String pcAuthorizerExePath = "D:\\release-ok2\\debug-05\\IosPCAuthorizer.exe";

    private Process startExe(String appleId, String kMachineIdA, String kMachineIdB) {
        String cmd = AppleAuthUtils.buildLaunchCmdParameter(pcAuthorizerExePath, appleId, kMachineIdA, kMachineIdB);
        return exeLauchServerCmd(cmd);
    }

    private Process exeLauchServerCmd(String cmd) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            throw ServiceException.getInternalException(e.getMessage());
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        try {
            String cmdResult = br.readLine();
            if (StringUtils.equals("0", cmdResult)) {
                return process;
            }
            IOUtils.close(br);
            destroyProcess(process);
        } catch (IOException e) {
            e.printStackTrace();
            IOUtils.close(br);
            destroyProcess(process);
        }
        return null;
    }


    private void destroyProcess(Process process) {
        if (process != null) {
            try {
                process.destroy();
            } catch (Exception e) {
            }
        }
    }

    private String calcLocalSignSapSetupBuffer(String signSapCert, Process process) {
        try {
            return executeCmdByLocalProcess(process, "cal_sap " + signSapCert);
        } catch (IOException e) {
            log.error("ITunesInitialService.calcLocalSignSapSetupBuffer IOException", e);
            throw ServiceException.getInternalException(e.getMessage());
        }
    }

    private boolean completeLocalSignSapSetup(String signSapBuffer, Process process) {
        try {
            String output = executeCmdByLocalProcess(process, "fin_sap " + signSapBuffer);
            return "0".equals(output);
        } catch (IOException e) {
            log.error("ITunesInitialService.calcLocalSignSapSetupBuffer IOException", e);
            throw ServiceException.getInternalException(e.getMessage());
        }
    }

    private static String executeCmdByLocalProcess(Process process, String cmd) throws IOException {
        return executeCmdByLocalProcess(process.getInputStream(), process.getOutputStream(), cmd);
    }

    private static String executeCmdByLocalProcess(InputStream processIs, OutputStream processOs, String cmd)
            throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(processOs));
        bufferedWriter.write(cmd);
        bufferedWriter.newLine();
        bufferedWriter.flush();
        BufferedReader br = new BufferedReader(new InputStreamReader(processIs));
        return br.readLine();
    }

    private String calcSignature(Process serverProcess, String loginText) throws IOException {
        return executeCmdByLocalProcess(serverProcess, "cal_signx " + loginText);
    }


    private String calcKbSync(Process serverProcess, String xDsid) throws IOException {
        return executeCmdByLocalProcess(serverProcess, "cal_kbsync " + xDsid);
    }


    private static final String LOGIN_TEXT_FORMAT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">"
            + "<plist version=\"1.0\">"
            + "<dict>"
            + "<key>appleId</key>"
            + "<string>%s</string>"
            + "<key>attempt</key>"
            + "<integer>1</integer>"
            + "<key>createSession</key>"
            + "<string>true</string>"
            + "<key>guid</key>"
            + "<string>%s</string>"
            + "<key>machineName</key>"
            + "<string>%s</string>"
            + "<key>password</key>"
            + "<string>%s</string>"
            + "<key>why</key>"
            + "<string>purchase</string>"
            + "</dict>" + "</plist>";

    private static final String LOGIN_TEXT_FORMAT_BASIC = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">"
            + "<plist version=\"1.0\">"
            + "<dict>"
            + "<key>appleId</key>"
            + "<string>%s</string>"
            + "<key>attempt</key>"
            + "<integer>1</integer>"
            + "<key>guid</key>"
            + "<string>%s</string>"
            + "<key>machineName</key>"
            + "<string>%s</string>"
            + "<key>password</key>"
            + "<string>%s</string>"
            + "<key>why</key>" + "<string>purchase</string>" + "</dict>" + "</plist>";

    private String buildLoginText(String acccount, String password, String machineGuid, String machineName,
            boolean createSession) {
        if (createSession) {
            return String.format(LOGIN_TEXT_FORMAT, acccount, machineGuid, machineName, password);
        }
        return String.format(LOGIN_TEXT_FORMAT_BASIC, acccount, machineGuid, machineName, password);
    }


    private void quitExe(Process process) {
        if (process == null) {
            return;
        }
        try {
            executeCmdByLocalProcess(process.getInputStream(), process.getOutputStream(), "close_server");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            process.destroy();
        }
    }



    @Override
    public AccountInfo login(String appleId, String pwd, String ip, Integer port, MachineInfo machineInfo,
            boolean createSession) {
        if (StringUtils.isEmpty(appleId) || StringUtils.isEmpty(pwd)) {
            throw ServiceException.getParameterException("Apple account is not or apple account pwd is illegal.");
        }
        String osGuid;
        String osName;
        String kMachineIdA;
        String kMachineIdB;
        // machine data init
        osGuid = machineInfo.getMachineGuid();
        osName = machineInfo.getComputerName();
        kMachineIdA = machineInfo.getkMachineIdA();
        kMachineIdB = machineInfo.getkMachineIdB();
        // step1: 启动exe，传入appleId和机器信息，并保持exe的IO通信。 IosPCAuthorizer -appleid
        Process process = null;
        try {
            process = startExe(appleId, kMachineIdA, kMachineIdB);
        } catch (Exception e) {
            throw new ServiceException(AuthResultCode.IOS_LAUNCH_EXE_FAILED, e.getMessage());
        }
        if (process == null) {
            // launch failed
            throw new ServiceException(AuthResultCode.IOS_LAUNCH_EXE_FAILED, "Start auth exe failed.ExePath:"
                    + pcAuthorizerExePath);
        }
        // LOGIN
        AccountInfo accountInfo = new AccountInfo();
        try {
            // step2: 计算SignSapSetupBuffer，根据苹果服务器返回的SignSapSetupCert。 cal_sap
            // XXXX(SignSapSetupCert)
            Pair<String, HttpContext> signSapSetupCert = ItunesNetworkUtils.getSignSapSetupCert(null, 0);
            String cert = signSapSetupCert.getLeft();
            String localSignBuffer = calcLocalSignSapSetupBuffer(cert, process);
            // step3: 完成SignSapSetup的过程，根据苹果服务器返回的signSapBuffer。 fin_sap
            // XXXX(SignSapBuffer)
            Pair<String, HttpContext> signSapSetup = ItunesNetworkUtils.getSignSapSetup(localSignBuffer, null, 0,
                    signSapSetupCert.getRight());
            boolean success = completeLocalSignSapSetup(signSapSetup.getLeft(), process);
            if (!success) {
                throw new ServiceException(AuthResultCode.IOS_EXCEPTION_FIN_SAP, "");
            }
            String loginText = buildLoginText(appleId, pwd, osGuid, osName, createSession);

            // step4: 计算苹果账号登陆plist对应的XAppleSignature。 cal_signx XXXX(login
            // plist)
            String actionSignatrue;
            try {
                actionSignatrue = calcSignature(process, loginText);
            } catch (IOException e) {
                throw new ServiceException(AuthResultCode.IOS_EXCEPTION_INTERNAL_X_APPLE_ACTION_SIGNATURE_FAILED,
                        "internal exception: X-Apple-ActionSignature error。 Errmsg:" + e.getMessage());
            }
            if (StringUtils.isBlank(actionSignatrue)) {
                throw new ServiceException(AuthResultCode.IOS_EXCEPTION_INTERNAL_X_APPLE_ACTION_SIGNATURE_FAILED,
                        "internal exception: X-Apple-ActionSignature error");
            }
            accountInfo.setGuid(osGuid);
            accountInfo.setMachineName(osName);
            Pair<AppleLoginResult, HttpContext> loginResultPair;
            try {
                loginResultPair = ItunesNetworkUtils.authenticate(loginText, actionSignatrue, ip, port);
            } catch (IOException e) {
                throw new ServiceException(AuthResultCode.IOS_EXCEPTION_AUTH_FAILED, "Errmsg:" + e.getMessage());
            }
            AppleLoginResult appleLoginResult = loginResultPair.getLeft();
            accountInfo.setxDsid(appleLoginResult.getxDsid());
            accountInfo.setxToken(appleLoginResult.getxToken());
            accountInfo.setCreditDisplay(appleLoginResult.getCreditDisplay());
            accountInfo.setxAppleActionSignature(actionSignatrue);

            // step5: 计算kbsync。 cal_kbsync XXXX(appleid 对应的xdsid
            String kbsync;
            try {
                kbsync = calcKbSync(process, accountInfo.getxDsid());
            } catch (IOException e) {
                throw new ServiceException(AuthResultCode.IOS_EXCEPTION_AUTH_FAILED_CALC_KBSYNC_FAILED, "Errmsg:"
                        + e.getMessage());
            }
            if (StringUtils.isBlank(kbsync) || kbsync.equals("error")) {
                throw new ServiceException(AuthResultCode.IOS_EXCEPTION_AUTH_FAILED_CALC_KBSYNC_FAILED,
                        "internal exception: calc kbsync failed");
            }
            accountInfo.setKbsync(kbsync);

            HttpContext httpContext = loginResultPair.getRight();
            HttpResponse response = (HttpResponse) httpContext.getAttribute("http.response");
            Header[] headers = response.getHeaders("X-Set-Apple-Store-Front");
            if (headers.length > 0) {
                accountInfo.setxAppleStoreFront(headers[0].getValue());
            }

            // step6: 完成Authorize PC，
            // 根据苹果服务器对authorizemachine请求，返回的响应中的keybag，diversitybag。 fin_authpc
            // XXXX( keybag + " " + diversitybag)
            // AuthMachineResult authMachineResp;
            // try {
            // authMachineResp = ItunesNetworkUtils.authMachine(accountInfo, ip,
            // port, httpContext);
            // } catch (IOException e) {
            // throw new
            // ServiceException(AuthResultCode.IOS_EXCEPTION_AUTH_MACHINE_FAILED,
            // e.getMessage());
            // }
            // if (authMachineResp != null && authMachineResp.getStatus() ==
            // AuthMachineResult.AUTH_MACHINE_SUCCESS_CODE) {
            // String finAuthPcResp = finAuthPc(process,
            // authMachineResp.getKeyBag(),
            // authMachineResp.getDiversitybag());
            // System.out.println(finAuthPcResp);
            // }
            ItunesNetworkUtils.setAccountInfoFromContext(httpContext, accountInfo);
        } catch (Exception e) {
            throw new ServiceException(AuthResultCode.IOS_EXCEPTION_FIN_AUTH_PC_FAILED, "Errmsg:" + e.getMessage());
        } finally {
            quitExe(process);
        }
        return accountInfo;

    }

}
