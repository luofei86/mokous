//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.base.utils;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author luofei
 * Generate 2020/1/14
 */
public class DomainUtils {
    public static boolean isAttributeSupportAnnotation(Class<?> clazz, String propertyName,
            Class<? extends Annotation> annotationClass) {
        Field f = getField(clazz, propertyName);
        return isFieldSupportAnnotation(f, annotationClass);
    }

    private static boolean isFieldSupportAnnotation(Field f, Class<? extends Annotation> annotationClass) {
        if (f == null) {
            return false;
        }
        try {
            f.setAccessible(true);
            return f.isAnnotationPresent(annotationClass);
        } catch (Exception e) {
        }
        return false;
    }

    private static Field getField(Class<?> clazz, String propertyName) {
        return FieldUtils.getField(clazz, propertyName, true);
    }
}
