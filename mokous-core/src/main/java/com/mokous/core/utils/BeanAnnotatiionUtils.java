/**
 * 
 */
package com.mokous.core.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author appchina
 *
 */
public class BeanAnnotatiionUtils {
	/**
	 * check annotation present on attribute of clazz
	 * 
	 * @param clazz
	 * @param attribute
	 * @param annotationClazz
	 * @return
	 */
	public static boolean isAnnotationPresent(Class<?> clazz, String attribute,
			Class<? extends Annotation> annotationClazz) {
		try {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if (field.getName().equals(attribute)) {
					return field.isAnnotationPresent(annotationClazz);
				}
			}
		} catch (Exception e) {
		}
		return false;
	}
}
