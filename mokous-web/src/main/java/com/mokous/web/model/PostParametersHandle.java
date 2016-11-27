// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.web.model;

import java.util.Map;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月4日
 * 
 */
public interface PostParametersHandle<T> {
    public Map<String, Object> handle(T t);

}
