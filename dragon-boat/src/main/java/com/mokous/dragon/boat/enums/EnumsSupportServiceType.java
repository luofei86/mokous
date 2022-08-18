//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.dragon.boat.enums;

/**
 * @author luofei
 * Generate 2020/1/11
 */
public enum EnumsSupportServiceType {
    COMMON_DATA("CommonData", "数据直接数据库访问服务"),
    COMMON_CACHE_DATA("CommonCacheData", "数据缓存访问服务"),
    SET_CACHE("SetCache", "数据Set缓存访问服务"),
    SORTED_LIST_CACHE("SortedListCache", "数据SortedSet缓存访问服务");

    private String key;
    private String value;

    EnumsSupportServiceType(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static EnumsSupportServiceType valueOfKey(String key){
        for (EnumsSupportServiceType value : EnumsSupportServiceType.values()) {
            if(value.key.equalsIgnoreCase(key)){
                return value;
            }
        }
        return null;
    }
}
