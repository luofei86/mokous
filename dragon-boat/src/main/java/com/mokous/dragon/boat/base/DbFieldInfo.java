//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.dragon.boat.base;

import lombok.Data;

/**
 * @author luofei
 * Generate 2020/1/11
 */
@Data
public class DbFieldInfo {
    private String column;
    private String mysqlType;
    private String name;
    private String type;
    private String jdbcType;
    private boolean canNull;
    private boolean ignoreOutputInJava;
    private boolean indexKey;
    private boolean primaryKey;
    private boolean uniqueKey;
    private boolean autoIncrement;
    private boolean unsigned;

    public DbFieldInfo(String column, String mysqlType, String name, String type, String jdbcType, boolean canNull,
            boolean ignoreOutputInJava, boolean indexKey, boolean primaryKey, boolean uniqueKey,
            boolean autoIncrement) {
        this.column = column;
        this.mysqlType = mysqlType;
        this.name = name;
        this.type = type;
        this.jdbcType = jdbcType;
        this.canNull = canNull;
        this.ignoreOutputInJava = ignoreOutputInJava;
        this.indexKey = indexKey;
        this.primaryKey = primaryKey;
        this.uniqueKey = uniqueKey;
        this.autoIncrement = autoIncrement;
    }

    public DbFieldInfo(String columnName, String columnType, String convertToClassAttribute, String javaType,
            String jdbcType, boolean canNull, boolean unsigned, boolean ignoreOutputInJava, boolean autoIncrement) {
        this.column = columnName;
        this.mysqlType = columnType;
        this.name = convertToClassAttribute;
        this.type = javaType;
        this.jdbcType = jdbcType;
        this.canNull = canNull;
        this.unsigned = unsigned;
        this.ignoreOutputInJava = ignoreOutputInJava;
        this.autoIncrement = autoIncrement;
    }

    @Override
    public String toString() {
        return "DbFieldInfo{" +
                "column='" + column + '\'' +
                ", mysqlType='" + mysqlType + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", jdbcType='" + jdbcType + '\'' +
                ", canNull=" + canNull +
                ", ignoreOutputInJava=" + ignoreOutputInJava +
                ", indexKey=" + indexKey +
                ", primaryKey=" + primaryKey +
                ", uniqueKey=" + uniqueKey +
                ", autoIncrement=" + autoIncrement +
                ", unsigned=" + unsigned +
                '}';
    }
}