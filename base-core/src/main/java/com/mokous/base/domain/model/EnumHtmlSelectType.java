//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.base.domain.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author luofei
 * Generate 2020/2/3
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumHtmlSelectType {
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.FIELD })
    public @interface KEY {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.FIELD })
    public @interface INT_KEY {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.FIELD })
    public @interface STRING_KEY {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.FIELD })
    public @interface VALUE {

    }
}
