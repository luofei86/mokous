// Copyright 2014 www.refanqie.com Inc. All Rights Reserved.

package com.mokous.core.service;

import java.util.List;

import com.mokous.web.exception.ServiceException;

/**
 * @author luofei@refanqie.com (Your Name Here)
 *
 */
public interface LoadService {
    public void cacheInitLoad() throws ServiceException;

    public void deltaCacheInitLoad(int startId) throws ServiceException;

    public void deltaCacheInitLoad(List<Integer> deltaIds) throws ServiceException;

}
