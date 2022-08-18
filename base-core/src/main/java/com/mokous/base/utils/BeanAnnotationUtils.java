//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.base.utils;

import java.lang.annotation.Annotation;

/**
 * @author luofei
 * Generate 2020/1/14
 */
public class BeanAnnotationUtils {
    public static final boolean isAnnotationPresent(Class<?> clazz, String propertyName, Class<? extends Annotation> annotationClass){
        return DomainUtils.isAttributeSupportAnnotation(clazz, propertyName, annotationClass);
    }


}
