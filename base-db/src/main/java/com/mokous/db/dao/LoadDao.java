//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.db.dao;

import com.mokous.base.exception.BizException;

import java.util.List;

/**
 * @author luofei
 * Generate 2020/1/12
 */
public interface LoadDao<T> {
    List<T> queryList(int startId, int size) throws BizException;

    List<T> queryList(long startId, int size) throws BizException;
}
