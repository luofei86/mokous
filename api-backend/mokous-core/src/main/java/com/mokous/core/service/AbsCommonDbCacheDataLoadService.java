// Copyright 2016 ios.appchina.com Inc. All Rights Reserved.

package com.mokous.core.service;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.mokous.core.dao.LoadDao;
import com.mokous.core.dto.StatusSerializable;
import com.mokous.web.exception.ServiceException;

/**
 * 所有继续此类的方法在进行modify前，都要确认数据是刚从数据库中获取
 * 
 * @author luofei@appchina.com create date: 2016年3月8日
 *
 */
public abstract class AbsCommonDbCacheDataLoadService<G extends StatusSerializable> extends
        AbsCommonDbCacheDataService<G> implements LoadService {
    private static final Logger log = Logger.getLogger(AbsCommonDbCacheDataLoadService.class);

    @Override
    public void cacheInitLoad() throws ServiceException {
        deltaCacheInitLoad(0);
        afterCacheInit();
    }

    protected void afterCacheInit() {
    }

    protected Logger getLogger() {
        return log;
    }

    public String getName() {
        return this.getClass().getName();
    }

    protected abstract LoadDao<G> getLoadDao();

    protected void putToCacheDb(List<G> gg) {
        toCache(gg);
    }

    protected int getNextId(List<G> values) throws Exception {
        G t = values.get(values.size() - 1);
        return getId(t);
    }

    protected abstract boolean isStop();

    @Override
    public void deltaCacheInitLoad(int startId) throws ServiceException {
        int size = getSize();
        do {
            List<G> values = null;
            try {
                getLogger().info("Load " + getName() + " data from Id:" + startId + ", size:" + size);
                values = this.getLoadDao().queryList(startId, size);
            } catch (SQLException e) {
                throw ServiceException.getSQLException(e.getMessage());
            }
            putToCacheDb(values);
            if (values.size() < size) {
                break;
            }
            try {
                startId = getNextId(values);
            } catch (Exception e) {
                throw ServiceException.getInternalException(e.getMessage());
            }
        } while (!isStop());
        getLogger().info("Finish load " + getName() + " data.");

    }

    protected int getSize() {
        return 1000;
    }

    @Override
    public void deltaCacheInitLoad(List<Integer> deltaIds) throws ServiceException {
        List<G> gg = null;
        try {
            gg = getCommonDao().queryList(deltaIds);
        } catch (SQLException e) {
            throw ServiceException.getSQLException(e);
        }
        putToCacheDb(gg);
    }

    @Override
    protected void afterAddData(List<G> gg) {
        cacheInitLoad();
    }

    @Override
    protected boolean legalData(G g) {
        return g.isOk();
    }

}
