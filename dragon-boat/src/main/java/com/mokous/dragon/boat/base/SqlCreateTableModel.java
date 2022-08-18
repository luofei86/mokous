//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.dragon.boat.base;

import lombok.Data;

import java.util.List;

/**
 * @author luofei
 * Generate 2020/01/11
 */
@Data
public class SqlCreateTableModel {
    private String tableName;
    private List<DbFieldInfo> attributes;
    private List<List<DbFieldInfo>> uks;

    public SqlCreateTableModel(String tableName, List<DbFieldInfo> attributes,
            List<List<DbFieldInfo>> uks) {
        this.tableName = tableName;
        this.attributes = attributes;
        this.uks = uks;
    }
    @Override
    public String toString() {
        return "SqlCreateTableModel{" +
                "tableName='" + tableName + '\'' +
                ", attributes=" + attributes +
                ", uks=" + uks +
                '}';
    }
}
