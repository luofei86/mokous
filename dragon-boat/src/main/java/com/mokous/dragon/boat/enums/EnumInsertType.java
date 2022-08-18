//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.dragon.boat.enums;

/**
 * @author luofei
 * Generate 2020/1/11
 */
public enum EnumInsertType {
    INSERT(0,"直接插入"),INSERT_OR_IGNORE(1,"插入或忽略"),

    INSERT_OR_UPDATE(2,"插入或更新");

    int key;
    String value;

    EnumInsertType(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

}
