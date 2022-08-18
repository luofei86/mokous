//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.db.service;

import com.mokous.base.exception.BizException;

import java.util.List;

/**
 * @author luofei
 * Generate 2020/1/13
 */
public interface LoadService {
    void cacheInitLoad() throws BizException;

    void deltaCacheInitLoad(int startId) throws BizException;

    void deltaCacheInitLoad(List<Integer> deltaIds) throws BizException;
}
