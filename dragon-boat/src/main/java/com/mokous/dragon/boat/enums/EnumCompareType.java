//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.dragon.boat.enums;

/**
 * @author luofei
 * Generate 2020/1/11
 */
public enum EnumCompareType {
    EQUALS(0, "id", " = "), GREATER(1, "startId", " > "),

    LESS(-1, "endId", " &lt; "), IN(3, "id", " IN ");

    int key;
    String parameter;
    String value;

    EnumCompareType(int key, String parameter, String value) {
        this.key = key;
        this.parameter = parameter;
        this.value = value;
    }

    public String getParameter() {
        return parameter;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

}
