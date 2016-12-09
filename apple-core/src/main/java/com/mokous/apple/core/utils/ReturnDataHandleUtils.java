// Copyright 2015 www.refanqie.com Inc. All Rights Reserved.

package com.mokous.apple.core.utils;

import java.lang.reflect.Type;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mokous.apple.core.cache.appleaccount.login.AuthActionResponse;
import com.mokous.web.model.ApiRespWrapper;
import com.mokous.web.model.ReturnDataHandle;
import com.mokous.web.utils.GsonUtils;

/**
 * @author miaozhijian@appchina.com (Your Name Here)
 * @param <T>
 *
 */
public class ReturnDataHandleUtils<T> {

    public static final ReturnDataHandle<ApiRespWrapper<AuthActionResponse>> APPLEIDAUTH_RD_HANDLE = new ReturnDataHandle<ApiRespWrapper<AuthActionResponse>>() {


        @Override
        public ApiRespWrapper<AuthActionResponse> handle(String value) throws Exception {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            Type type = new TypeToken<ApiRespWrapper<AuthActionResponse>>() {}.getType();
            Gson gson = new Gson();
            return gson.fromJson(value, type);
        }
    };
    public static final ReturnDataHandle<ApiRespWrapper<Boolean>> BOOLEAN_RD_HANDLE = new ReturnDataHandle<ApiRespWrapper<Boolean>>() {
        @Override
        public ApiRespWrapper<Boolean> handle(String value) throws Exception {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            Type type = new TypeToken<ApiRespWrapper<Boolean>>() {}.getType();
            return GsonUtils.convert(value, type);
        }
    };
//
//
//    public static final ReturnDataHandle<AppSearchResult> SEARCH_RD_HANDLE = new ReturnDataHandle<AppSearchResult>() {
//
//        @Override
//        public AppSearchResult handle(String value) throws Exception {
//            if (StringUtils.isEmpty(value)) {
//                return null;
//            }
//            return GsonUtils.convert(value, AppSearchResult.class);
//        }
//    };
//
//    public static final ReturnDataHandle<ApiRespWrapper<ListWrapResp<RootApplicationTag>>> ROOTAPPLICATIONTAG_LIST_RD_HANDLE = new ReturnDataHandle<ApiRespWrapper<ListWrapResp<RootApplicationTag>>>() {
//        @Override
//        public ApiRespWrapper<ListWrapResp<RootApplicationTag>> handle(String value) throws Exception {
//            if (StringUtils.isEmpty(value)) {
//                return null;
//            }
//
//            Type type = new TypeToken<ApiRespWrapper<ListWrapResp<RootApplicationTag>>>() {}.getType();
//            return GsonUtils.convert(value, type);
//        }
//    };
//
//    public static final ReturnDataHandle<ApiRespWrapper<ListWrapResp<AppleAuthorizerAccount>>> APPLEAUTHORIZERACCOUNT_LIST_RD_HANDLE = new ReturnDataHandle<ApiRespWrapper<ListWrapResp<AppleAuthorizerAccount>>>() {
//        @Override
//        public ApiRespWrapper<ListWrapResp<AppleAuthorizerAccount>> handle(String value) throws Exception {
//            if (StringUtils.isEmpty(value)) {
//                return null;
//            }
//            Type type = new TypeToken<ApiRespWrapper<ListWrapResp<AppleAuthorizerAccount>>>() {}.getType();
//            return GsonUtils.convert(value, type);
//        }
//    };
//
//    public static final ReturnDataHandle<ApiRespWrapper<ListWrapResp<AuthorizerAppIpa>>> AUTHORIZERAPPIPA_LIST_RD_HANDLE = new ReturnDataHandle<ApiRespWrapper<ListWrapResp<AuthorizerAppIpa>>>() {
//        @Override
//        public ApiRespWrapper<ListWrapResp<AuthorizerAppIpa>> handle(String value) throws Exception {
//            if (StringUtils.isEmpty(value)) {
//                return null;
//            }
//            Type type = new TypeToken<ApiRespWrapper<ListWrapResp<AuthorizerAppIpa>>>() {}.getType();
//            return GsonUtils.convert(value, type);
//        }
//    };
//
//    public static final ReturnDataHandle<ApiRespWrapper<ListWrapResp<AuthorizerAppDownloadTask>>> AUTHORIZERAPPDOWNLOADTASK_LIST_RD_HANDLE = new ReturnDataHandle<ApiRespWrapper<ListWrapResp<AuthorizerAppDownloadTask>>>() {
//        @Override
//        public ApiRespWrapper<ListWrapResp<AuthorizerAppDownloadTask>> handle(String value) throws Exception {
//            if (StringUtils.isEmpty(value)) {
//                return null;
//            }
//            Type type = new TypeToken<ApiRespWrapper<ListWrapResp<AuthorizerAppDownloadTask>>>() {}.getType();
//            return GsonUtils.convert(value, type);
//        }
//    };
//
//    public static final ReturnDataHandle<ApiRespWrapper<AuthorizerAppDownloadTask>> AUTHORIZERAPPDOWNLOADTASK_RD_HANDLE = new ReturnDataHandle<ApiRespWrapper<AuthorizerAppDownloadTask>>() {
//        @Override
//        public ApiRespWrapper<AuthorizerAppDownloadTask> handle(String value) throws Exception {
//            if (StringUtils.isEmpty(value)) {
//                return null;
//            }
//            Type type = new TypeToken<ApiRespWrapper<AuthorizerAppDownloadTask>>() {}.getType();
//            return GsonUtils.convert(value, type);
//        }
//    };
//
//    public static final ReturnDataHandle<ApiRespWrapper<ListWrapResp<AuthorizerAppDownloadFeedback>>> AUTHORIZERAPPDOWNLOADFEEDBACK_LIST_RD_HANDLE = new ReturnDataHandle<ApiRespWrapper<ListWrapResp<AuthorizerAppDownloadFeedback>>>() {
//        @Override
//        public ApiRespWrapper<ListWrapResp<AuthorizerAppDownloadFeedback>> handle(String value) throws Exception {
//            if (StringUtils.isEmpty(value)) {
//                return null;
//            }
//            Type type = new TypeToken<ApiRespWrapper<ListWrapResp<AuthorizerAppDownloadFeedback>>>() {}.getType();
//            return GsonUtils.convert(value, type);
//        }
//    };
//
//    public static final ReturnDataHandle<ApiRespWrapper<ListWrapResp<AuthorizerAppDownloadServerInfo>>> AUTHORIZERAPPDOWNLOADSERVERINFO_LIST_RD_HANDLE = new ReturnDataHandle<ApiRespWrapper<ListWrapResp<AuthorizerAppDownloadServerInfo>>>() {
//        @Override
//        public ApiRespWrapper<ListWrapResp<AuthorizerAppDownloadServerInfo>> handle(String value) throws Exception {
//            if (StringUtils.isEmpty(value)) {
//                return null;
//            }
//            Type type = new TypeToken<ApiRespWrapper<ListWrapResp<AuthorizerAppDownloadServerInfo>>>() {}.getType();
//            return GsonUtils.convert(value, type);
//        }
//    };
//
//    public static final ReturnDataHandle<ApiRespWrapper<AuthorizerAppDownloadFeedback>> AUTHORIZERAPPDOWNLOADFEEDBACK_RD_HANDLE = new ReturnDataHandle<ApiRespWrapper<AuthorizerAppDownloadFeedback>>() {
//        @Override
//        public ApiRespWrapper<AuthorizerAppDownloadFeedback> handle(String value) throws Exception {
//            if (StringUtils.isEmpty(value)) {
//                return null;
//            }
//            Type type = new TypeToken<ApiRespWrapper<AuthorizerAppDownloadFeedback>>() {}.getType();
//            return GsonUtils.convert(value, type);
//        }
//    };
//
//    public static final ReturnDataHandle<ApiRespWrapper<AuthorizerAppIpa>> AUTHORIZERAPPIPA_RD_HANDLE = new ReturnDataHandle<ApiRespWrapper<AuthorizerAppIpa>>() {
//        @Override
//        public ApiRespWrapper<AuthorizerAppIpa> handle(String value) throws Exception {
//            if (StringUtils.isEmpty(value)) {
//                return null;
//            }
//            Type type = new TypeToken<ApiRespWrapper<AuthorizerAppIpa>>() {}.getType();
//            return GsonUtils.convert(value, type);
//        }
//    };
//
//    public static final ReturnDataHandle<ApiRespWrapper<ListWrapResp<AppleAuthorizerAccountBuyAppInfo>>> APPLEAUTHORIZERACCOUNTBUYAPPINFO_LIST_RD_HANDLE = new ReturnDataHandle<ApiRespWrapper<ListWrapResp<AppleAuthorizerAccountBuyAppInfo>>>() {
//        @Override
//        public ApiRespWrapper<ListWrapResp<AppleAuthorizerAccountBuyAppInfo>> handle(String value) throws Exception {
//            if (StringUtils.isEmpty(value)) {
//                return null;
//            }
//            Type type = new TypeToken<ApiRespWrapper<ListWrapResp<AppleAuthorizerAccountBuyAppInfo>>>() {}.getType();
//            return GsonUtils.convert(value, type);
//        }
//    };
//
//    public static final ReturnDataHandle<ApiRespWrapper<AppleAuthorizerAccountBuyAppInfo>> APPLEAUTHORIZERACCOUNTBUYAPPINFO_RD_HANDLE = new ReturnDataHandle<ApiRespWrapper<AppleAuthorizerAccountBuyAppInfo>>() {
//        @Override
//        public ApiRespWrapper<AppleAuthorizerAccountBuyAppInfo> handle(String value) throws Exception {
//            if (StringUtils.isEmpty(value)) {
//                return null;
//            }
//            Type type = new TypeToken<ApiRespWrapper<AppleAuthorizerAccountBuyAppInfo>>() {}.getType();
//            return GsonUtils.convert(value, type);
//        }
//    };
//
//    public static final ReturnDataHandle<ApiRespWrapper<ListWrapResp<AppleAuthorizerAccountAuthPcMachineInfo>>> APPLEAUTHORIZERACCOUNTAUTHPCMACHINEINFO_LIST_RD_HANDLE = new ReturnDataHandle<ApiRespWrapper<ListWrapResp<AppleAuthorizerAccountAuthPcMachineInfo>>>() {
//        @Override
//        public ApiRespWrapper<ListWrapResp<AppleAuthorizerAccountAuthPcMachineInfo>> handle(String value)
//                throws Exception {
//            if (StringUtils.isEmpty(value)) {
//                return null;
//            }
//            Type type = new TypeToken<ApiRespWrapper<ListWrapResp<AppleAuthorizerAccountAuthPcMachineInfo>>>() {}
//                    .getType();
//            return GsonUtils.convert(value, type);
//        }
//    };
//
//    public static final ReturnDataHandle<ApiRespWrapper<ListWrapResp<AppleAuthorizerAccountAuthDeviceInfo>>> APPLEAUTHORIZERACCOUNTAUTHDEVICEINFO_LIST_RD_HANDLE = new ReturnDataHandle<ApiRespWrapper<ListWrapResp<AppleAuthorizerAccountAuthDeviceInfo>>>() {
//        @Override
//        public ApiRespWrapper<ListWrapResp<AppleAuthorizerAccountAuthDeviceInfo>> handle(String value) throws Exception {
//            if (StringUtils.isEmpty(value)) {
//                return null;
//            }
//            Type type = new TypeToken<ApiRespWrapper<ListWrapResp<AppleAuthorizerAccountAuthDeviceInfo>>>() {}
//                    .getType();
//            return GsonUtils.convert(value, type);
//        }
//    };
//
//    public static final ReturnDataHandle<ApiRespWrapper<ListWrapResp<AppleAuthorizerAccountAuthPcServerInfo>>> APPLEAUTHORIZERACCOUNTAUTHPCSERVERINFO_LIST_RD_HANDLE = new ReturnDataHandle<ApiRespWrapper<ListWrapResp<AppleAuthorizerAccountAuthPcServerInfo>>>() {
//        @Override
//        public ApiRespWrapper<ListWrapResp<AppleAuthorizerAccountAuthPcServerInfo>> handle(String value)
//                throws Exception {
//            if (StringUtils.isEmpty(value)) {
//                return null;
//            }
//            Type type = new TypeToken<ApiRespWrapper<ListWrapResp<AppleAuthorizerAccountAuthPcServerInfo>>>() {}
//                    .getType();
//            return GsonUtils.convert(value, type);
//        }
//    };
//
//    public static final ReturnDataHandle<ApiRespWrapper<ListWrapResp<AppStoreClientAuditSwitchIpConf>>> APPSTORECLIENTAUDITSWITCHIPCONF_LIST_RD_HANDLE = new ReturnDataHandle<ApiRespWrapper<ListWrapResp<AppStoreClientAuditSwitchIpConf>>>() {
//        @Override
//        public ApiRespWrapper<ListWrapResp<AppStoreClientAuditSwitchIpConf>> handle(String value) throws Exception {
//            if (StringUtils.isEmpty(value)) {
//                return null;
//            }
//            Type type = new TypeToken<ApiRespWrapper<ListWrapResp<AppStoreClientAuditSwitchIpConf>>>() {}.getType();
//            return GsonUtils.convert(value, type);
//        }
//    };
//
//    public static final ReturnDataHandle<ApiRespWrapper<ListWrapResp<AppStoreClientShareInfo>>> APPSTORECLIENTSHAREINFO_LIST_RD_HANDLE = new ReturnDataHandle<ApiRespWrapper<ListWrapResp<AppStoreClientShareInfo>>>() {
//        @Override
//        public ApiRespWrapper<ListWrapResp<AppStoreClientShareInfo>> handle(String value) throws Exception {
//            if (StringUtils.isEmpty(value)) {
//                return null;
//            }
//            Type type = new TypeToken<ApiRespWrapper<ListWrapResp<AppStoreClientShareInfo>>>() {}.getType();
//            return GsonUtils.convert(value, type);
//        }
//    };
//
//    public static final ReturnDataHandle<ApiRespWrapper<AppStoreClientShareInfo>> APPSTORECLIENTSHAREINFO_RD_HANDLE = new ReturnDataHandle<ApiRespWrapper<AppStoreClientShareInfo>>() {
//        @Override
//        public ApiRespWrapper<AppStoreClientShareInfo> handle(String value) throws Exception {
//            if (StringUtils.isEmpty(value)) {
//                return null;
//            }
//            Type type = new TypeToken<ApiRespWrapper<AppStoreClientShareInfo>>() {}.getType();
//            return GsonUtils.convert(value, type);
//        }
//    };
//
//    public static final ReturnDataHandle<ApiRespWrapper<AppleAuthorizerAccount>> APPLEAUTHORIZERACCOUNT_RD_HANDLE = new ReturnDataHandle<ApiRespWrapper<AppleAuthorizerAccount>>() {
//        @Override
//        public ApiRespWrapper<AppleAuthorizerAccount> handle(String value) throws Exception {
//            if (StringUtils.isEmpty(value)) {
//                return null;
//            }
//            Type type = new TypeToken<ApiRespWrapper<AppleAuthorizerAccount>>() {}.getType();
//            return GsonUtils.convert(value, type);
//        }
//    };
//
//    public static final ReturnDataHandle<ApiRespWrapper<ListWrapResp<AuthorizerPcServerInfo>>> AUTHORIZERPCSERVERINFO_LIST_RD_HANDLE = new ReturnDataHandle<ApiRespWrapper<ListWrapResp<AuthorizerPcServerInfo>>>() {
//        @Override
//        public ApiRespWrapper<ListWrapResp<AuthorizerPcServerInfo>> handle(String value) throws Exception {
//            if (StringUtils.isEmpty(value)) {
//                return null;
//            }
//            Type type = new TypeToken<ApiRespWrapper<ListWrapResp<AuthorizerPcServerInfo>>>() {}.getType();
//            return GsonUtils.convert(value, type);
//        }
//    };
//
//    public static final ReturnDataHandle<ApiRespWrapper<ListWrapResp<AuthorizerPcMachineInfo>>> AUTHORIZERPCMACHINEINFO_LIST_RD_HANDLE = new ReturnDataHandle<ApiRespWrapper<ListWrapResp<AuthorizerPcMachineInfo>>>() {
//        @Override
//        public ApiRespWrapper<ListWrapResp<AuthorizerPcMachineInfo>> handle(String value) throws Exception {
//            if (StringUtils.isEmpty(value)) {
//                return null;
//            }
//            Type type = new TypeToken<ApiRespWrapper<ListWrapResp<AuthorizerPcMachineInfo>>>() {}.getType();
//            return GsonUtils.convert(value, type);
//        }
//    };
//    public static final ReturnDataHandle<ApiRespWrapper<RootApplication>> BUNDLE_PD = new ReturnDataHandle<ApiRespWrapper<RootApplication>>() {
//        @Override
//        public ApiRespWrapper<RootApplication> handle(String value) throws Exception {
//            if (StringUtils.isEmpty(value)) {
//                return null;
//            }
//            Type type = new TypeToken<ApiRespWrapper<RootApplication>>() {}.getType();
//            return GsonUtils.convert(value, type);
//        }
//    };
//    public static final ReturnDataHandle<ApiRespWrapper<ApplicationBundleIdWrapper>> APPLICATIONWRAPPER_RD_HANDLE = new ReturnDataHandle<ApiRespWrapper<ApplicationBundleIdWrapper>>() {
//        @Override
//        public ApiRespWrapper<ApplicationBundleIdWrapper> handle(String value) throws Exception {
//            if (StringUtils.isEmpty(value)) {
//                return null;
//            }
//            Type type = new TypeToken<ApiRespWrapper<ApplicationBundleIdWrapper>>() {}.getType();
//            return GsonUtils.convert(value, type);
//        }
//    };

}
