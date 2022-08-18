//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.base.domain.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author luofei
 * Generate 2020/1/31
 */
@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ORDER_KEY {
    String value() default DbFields.ID_COLUMN;
}
