// Copyright 2015 ios.appchina.com Inc. All Rights Reserved.

package com.mokous.core.cache.model;



/**
 * @author luofei@appchina.com (Your Name Here)
 *
 */
public class CacheFilter {
    public static class SizeCacheFilter extends CacheFilter {
        private int size;

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }

    public static class StartSizeCacheFilter extends SizeCacheFilter {
        private int start;
        private Double maxScore;
        private Double minScore;

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public Double getMaxScore() {
            return maxScore;
        }

        public void setMaxScore(Double maxScore) {
            this.maxScore = maxScore;
        }

        public Double getMinScore() {
            return minScore;
        }

        public void setMinScore(Double minScore) {
            this.minScore = minScore;
        }
    }

    public static class ChannelStartSizeCacheFilter extends StartSizeCacheFilter {
        private String channel;

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }
    }

    public static class PcSuiteStartSizeCacheFilter extends StartSizeCacheFilter {
        private String system;// support windows system version
        private String arch;// amd64/x86

        public String getSystem() {
            return system;
        }

        public void setSystem(String system) {
            this.system = system;
        }

        public String getArch() {
            return arch;
        }

        public void setArch(String arch) {
            this.arch = arch;
        }
    }

    public static class TypeStartSizeCacheFilter extends StartSizeCacheFilter {
        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class AccountEmailStartSizeCacheFilter extends AccountStartSizeCacheFilter {
        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class IdStartSizeCacheFilter extends StartSizeCacheFilter {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

    }

    public static class AppleRootStartSizeCacheFilter extends StartSizeCacheFilter {
        private String appleId;
        private int rootId;

        public String getAppleId() {
            return appleId;
        }

        public void setAppleId(String appleId) {
            this.appleId = appleId;
        }

        public int getRootId() {
            return rootId;
        }

        public void setRootId(int rootId) {
            this.rootId = rootId;
        }
    }

    public static class AccountStartSizeCacheFilter extends StartSizeCacheFilter {
        private int accountId;

        public int getAccountId() {
            return accountId;
        }

        public void setAccountId(int accountId) {
            this.accountId = accountId;
        }
    }

    public static class BundleIdStartSizeCacheFilter extends StartSizeCacheFilter {
        public static String ALL_BUNDLEID = "all";
        private String bundleId;

        public String getBundleId() {
            return bundleId;
        }

        public void setBundleId(String bundleId) {
            this.bundleId = bundleId;
        }
    }

    public static class AppStoreMarketVersionCacheFilter extends AppStoreMarketCacheFilter {
        private String clientVersion;

        public String getClientVersion() {
            return clientVersion;
        }

        public void setClientVersion(String clientVersion) {
            this.clientVersion = clientVersion;
        }

    }

    public static class AppStoreMarketCacheFilter extends BundleIdStartSizeCacheFilter {
        private int marketFlag;

        public int getMarketFlag() {
            return marketFlag;
        }

        public void setMarketFlag(int marketFlag) {
            this.marketFlag = marketFlag;
        }

    }

    public static class RootIdStartSizeCacheFilter extends StartSizeCacheFilter {
        private int rootId;

        public int getRootId() {
            return rootId;
        }

        public void setRootId(int rootId) {
            this.rootId = rootId;
        }
    }

    public static class ClientUsesrStartSizeCacheFilter extends StartSizeCacheFilter {
        private String uid;
        private String bundleId;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getBundleId() {
            return bundleId;
        }

        public void setBundleId(String bundleId) {
            this.bundleId = bundleId;
        }
    }

    public static class IosDeviceSizeFilter extends SizeCacheFilter {
        private String imei;
        private String udid;

        public String getImei() {
            return imei;
        }

        public void setImei(String imei) {
            this.imei = imei;
        }

        public String getUdid() {
            return udid;
        }

        public void setUdid(String udid) {
            this.udid = udid;
        }

    }

    public static class ListAdCacheFliter extends CacheFilter {
        private String place;
        private String channel;

        public String getPlace() {
            return place;
        }

        public void setPlace(String place) {
            this.place = place;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }
    }

    public static class AppStoreWallpaperCacheFilter extends StartSizeCacheFilter {
        private int chosenFlag;

        public int getChosenFlag() {
            return chosenFlag;
        }

        public void setChosenFlag(int chosenFlag) {
            this.chosenFlag = chosenFlag;
        }

    }

    public static class FunnyClientSpecialColumnCacheFilter extends StartSizeCacheFilter {
        private Integer ctype;
        // 模块控制
        private Integer promoteFlag;
        // 优选控制
        private Integer primeFlag;

        public Integer getCtype() {
            return ctype;
        }

        public void setCtype(Integer ctype) {
            this.ctype = ctype;
        }

        public Integer getPromoteFlag() {
            return promoteFlag;
        }

        public void setPromoteFlag(Integer promoteFlag) {
            this.promoteFlag = promoteFlag;
        }

        public Integer getPrimeFlag() {
            return primeFlag;
        }

        public void setPrimeFlag(Integer primeFlag) {
            this.primeFlag = primeFlag;
        }
    }
}
