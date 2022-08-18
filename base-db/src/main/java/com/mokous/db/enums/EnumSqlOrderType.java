//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.db.enums;

import org.apache.ibatis.executor.BatchResult;

/**
 * @author luofei
 * Generate 2020/1/12
 */
public enum EnumSqlOrderType {
    ASC("ASC", "ASC"),
    DESC("DESC", "DESC");
    String order;
    String sql;

    EnumSqlOrderType(String order, String sql) {
        this.order = order;
        this.sql = sql;
    }

    public String getOrder() {
        return order;
    }

    public String getSql() {
        return sql;
    }

    public static EnumSqlOrderType enumValueOf(String order) {
        for (EnumSqlOrderType value : EnumSqlOrderType.values()) {
            if(value.order.equalsIgnoreCase(order)){
                return value;
            }
        }
        return DESC;
    }
}
