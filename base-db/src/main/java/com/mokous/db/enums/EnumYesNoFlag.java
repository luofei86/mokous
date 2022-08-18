//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.db.enums;

import com.mokous.base.domain.model.DbFields;
import com.mokous.base.domain.model.EnumHtmlSelectType;

/**
 * @author luofei
 * Generate 2020/2/3
 */
public enum EnumYesNoFlag {
    YES("Y", "是"), NO("N", "否");

    @EnumHtmlSelectType.KEY
    @EnumHtmlSelectType.STRING_KEY
    private String key;
    @EnumHtmlSelectType.VALUE
    private String value;

    EnumYesNoFlag(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
