// Copyright 2015 ios.appchina.com Inc. All Rights Reserved.

package com.mokous.core.service;

import java.util.List;

import com.mokous.web.exception.ServiceException;

/**
 * @author luofei@appchina.com (Your Name Here)
 *
 */
public interface CommonDataService<G> {
    public List<G> listDirectFromDb(G g) throws ServiceException;

    public List<G> listDirectFromDb(G g, String st, String et, int start, int size) throws ServiceException;

    public List<G> listDirectFromDb(G g, int start, int size) throws ServiceException;

    public List<G> listByStartIdDirectFromDb(G g, int startId, int size) throws ServiceException;

    public List<G> listByEndIdDirectFromDb(G g, int endId, int size) throws ServiceException;

    public long countDirectFromDb(G g) throws ServiceException;

    public long countDirectFromDb(G g, String st, String et) throws ServiceException;

    public G getDirectFromDb(int id) throws ServiceException;

    public List<G> getDirectFromDb(List<Integer> ids) throws ServiceException;

    public void addData(G g) throws ServiceException;

    public void batchAdd(List<G> gg) throws ServiceException;

    public boolean addOrIgnoreData(G g) throws ServiceException;

    public void modifyStatus(G g) throws ServiceException;

    public void modiftStatus(List<G> gg) throws ServiceException;

}
