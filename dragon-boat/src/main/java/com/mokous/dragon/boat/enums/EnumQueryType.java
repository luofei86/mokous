//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.dragon.boat.enums;

/**
 * @author luofei
 * Generate 2020/1/11
 */
public enum EnumQueryType {
    QUERY_BY_ID(0, "query", "id", " * ", "ById", EnumCompareType.EQUALS),

    QUERY_BY_IDS(1, "query", "id", " * ", "ListByIds", EnumCompareType.IN),

    QUERY_BY_START_ID(2, "query", "startId", " * ", "ListByStartId", EnumCompareType.GREATER),

    QUERY_BY_START_ID_FILTER(3, "query", "startId", " * ", "ListByStartIdFilter", EnumCompareType.GREATER),

    QUERY_BY_END_ID_FILTER(4, "query", "endId", " * ", "ListByEndIdFilter", EnumCompareType.LESS),

    QUERY_BY_DOMAIN_FILTER(5, "query", null, " * ", "ListByFilter", null),

    COUNT_BY_DOMAIN_FILTER(6, "count", null, " count(*) ", "ByFilter", null),

    QUERY_BY_UNIQUE_KEY_FILTER(7, "query", null, " * ", "ByUniqueKey", null);

    int key;
    String prefix;
    String parameter;
    String action;
    String suffix;
    EnumCompareType value;

    EnumQueryType(int key, String prefix, String parameter, String action, String suffix, EnumCompareType value) {
        this.key = key;
        this.prefix = prefix;
        this.parameter = parameter;
        this.action = action;
        this.suffix = suffix;
        this.value = value;
    }

    public String getParameter() {
        return parameter;
    }

    public int getKey() {
        return key;
    }

    public EnumCompareType getValue() {
        return value;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getAction() {
        return action;
    }

    public String getSuffix() {
        return suffix;
    }
}
