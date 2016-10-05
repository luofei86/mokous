// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.winexeproxy.model;


/***
 * #define IOS_EXCEPTION_AUTH_TIME_OUT 4000 #define IOS_EXCEPTION_AUTH_FAILED
 * 1000 #define IOS_EXCEPTION_AUTH_FAILED_ACCOUNT_WRONG 1001 #define
 * IOS_EXCEPTION_AUTH_FAILED_ACCOUNT_DISABLE 1002 #define
 * IOS_EXCEPTION_INTERNAL_FAILED 2000 #define
 * IOS_EXCEPTION_INTERNAL_EVP_ENCODE_FAILED 2001 #define
 * IOS_EXCEPTION_INTERNAL_X_APPLE_ACTION_SIGNATURE_FAILED 2002 #define
 * IOS_EXCEPTION_INTERNAL_ITUNES_NOT_SUPPORT_ACTION_SIGNATURE 2003 #define
 * IOS_EXCEPTION_INTERNAL_SAPSETUP_INITIALIZE_FAILED 2005 #define
 * IOS_AUTH_SUCCESS 0
 */
/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
public class AuthResultCode {

    public static final int IOS_AUTH_SUCCESS = 0;
    public static final int IOS_EXCEPTION_AUTH_TIME_OUT = 54000;
    public static final int IOS_EXCEPTION_AUTH_FAILED = 51000;
    public static final int IOS_EXCEPTION_AUTH_FAILED_ACCOUNT_WRONG = 51001;
    public static final int IOS_EXCEPTION_AUTH_FAILED_ACCOUNT_DISABLE = 51002;
    public static final int IOS_EXCEPTION_AUTH_FAILED_UNVERIFY_APPLEID = 51003;
    public static final int IOS_EXCEPTION_AUTH_FAILED_UNUSERD_APPLEID = 51004;
    public static final int IOS_EXCEPTION_INTERNAL_FAILED = 52000;
    public static final int IOS_EXCEPTION_INTERNAL_EVP_ENCODE_FAILED = 52001;
    public static final int IOS_EXCEPTION_INTERNAL_X_APPLE_ACTION_SIGNATURE_FAILED = 52002;
    public static final int IOS_EXCEPTION_INTERNAL_ITUNES_NOT_SUPPORT_ACTION_SIGNATURE = 52003;
    public static final int IOS_EXCEPTION_AUTH_FAILED_CALC_KBSYNC_FAILED = 52004;
    public static final int IOS_EXCEPTION_INTERNAL_SAPSETUP_INITIALIZE_FAILED = 52005;
    public static final int IOS_EXCEPTION_GET_SIGN_SAP_SETUP_CERT_FAILED = 52006;
    public static final int IOS_EXCEPTION_GET_SIGN_SAP_SETUP_BUFFER_FAILED = 52007;
    public static final int IOS_EXCEPTION_GET_SIGN_SAP_SETUP_FAILED = 52008;
    public static final int IOS_EXCEPTION_FIN_AUTH_PC_FAILED = 52009;
    public static final int IOS_EXCEPTION_FIN_SAP = 53002;
    public static final int IOS_EXCEPTION_AUTH_FAILED_APPLE_UNKNOW_FAILED = 55002;
    public static final int IOS_EXCEPTION_AUTH_MACHINE_FAILED = 55001;
    public static final int IOS_LAUNCH_EXE_FAILED = 53001;



    public static int IOS_EXCEPTION_AUTH_FAILED_UNCAPTURED_ERROR = 56000;



    // public static int convertToServiceCode(int authResultCode){
    // if(authResultCode == IOS_AUTH_SUCCESS){
    // return 0;
    // }
    // return 50000 + authResultCode;
    // }
}
