// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.core.dto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface DbField {

    /**
     * @author luofei (Your Name Here)
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.FIELD })
    public @interface ZERO_ENABLE {

    }

    /**
     * @author luofei (Your Name Here)
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.FIELD })
    public @interface UNSIGNED_INT {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.FIELD })
    public @interface SELECT_ALL_KEY {
    }
}
