//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.db.enums;

import com.mokous.base.domain.model.DbFields;
import com.mokous.base.domain.model.EnumHtmlSelectType;

/**
 * @author luofei
 * Generate 2020/2/3
 */
public enum EnumDelFlag {
    YES(DbFields.DEL_FLAG_OK, "正常"), DEL(-1, "删除");

    @EnumHtmlSelectType.KEY
    @EnumHtmlSelectType.INT_KEY
    private int key;
    @EnumHtmlSelectType.VALUE
    private String value;

    EnumDelFlag(int key, String value) {
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
