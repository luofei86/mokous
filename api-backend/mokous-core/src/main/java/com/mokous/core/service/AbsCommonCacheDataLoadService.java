// Copyright 2014 www.refanqie.com Inc. All Rights Reserved.

package com.mokous.core.service;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.mokous.core.dao.LoadDao;
import com.mokous.web.exception.ServiceException;
import com.mokous.web.utils.CollectionUtils;

/**
 * @author luofei@refanqie.com (Your Name Here)
 */
public abstract class AbsCommonCacheDataLoadService<T extends Serializable> extends AbsCommonCacheDataService<T>
        implements LoadService, Comparable<AbsCommonCacheDataLoadService<T>> {
    public static final List<AbsCommonCacheDataLoadService<?>> LOAD_SERVICES = new CopyOnWriteArrayList<AbsCommonCacheDataLoadService<?>>();

    public AbsCommonCacheDataLoadService() {
        for (AbsCommonCacheDataLoadService<?> service : LOAD_SERVICES) {
            if (service.getClass().equals(this.getClass())) {
                return;
            }
        }
        LOAD_SERVICES.add(this);
    }

    @Override
    public void cacheInitLoad() throws ServiceException {
        deltaCacheInitLoad(0);
        afterCacheInit();
    }

    /**
     * 在一次总体的对缓存进行操作后，如果需要进行统计等工作，可以使用此接口来进行操作
     */
    protected void afterCacheInit() {
    }

    @Override
    protected void afterModifyData(List<T> gg) {
        if (CollectionUtils.emptyOrNull(gg)) {
            return;
        }
        this.cacheInitLoad();
    }

    protected int getSize() {
        return 1000;
    }

    protected long getEnableCacheVersionCode() {
        return 0;
    }

    protected long generateCacheVersionCode() {
        return 0;
    }

    protected void saveEnableCacheVersionCode(long versionCode) {
    }

    protected void expirePreVersionCache(long cachePreEnableVersionCode) {
    }

    protected int getNextId(List<T> values) throws Exception {
        T t = values.get(values.size() - 1);
        return getIdFromG(t);
    }

    protected abstract void putToCacheDb(List<T> values, long versionCode);

    protected void putToCacheDb(List<T> values) {
        putToCacheDb(values, 0);
    }

    protected abstract LoadDao<T> getLoadDao();

    protected abstract boolean isStop();

    /**
     * 值越小，越第一时间进行load
     *
     * @return
     */
    protected int getPriority() {
        Field[] fields = getClass().getFields();
        for (Field field : fields) {
            Type clazz = field.getType();
            if (clazz instanceof AbsCommonCacheDataLoadService) {
                // TODO
            }
        }
        return 0;
    }

    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public int compareTo(AbsCommonCacheDataLoadService<T> o) {
        return this.getPriority() - o.getPriority();
    }


    @Override
    public void deltaCacheInitLoad(int startId) throws ServiceException {
        long versionCode = generateCacheVersionCode();
        int size = getSize();
        do {
            List<T> values = null;
            try {
                getLogger().info("Load " + getName() + " data from Id:" + startId + ", size:" + size);
                values = this.getLoadDao().queryList(startId, size);
            } catch (SQLException e) {
                throw ServiceException.getSQLException(e.getMessage());
            }
            putToCacheDb(values, versionCode);
            if (values.size() < size) {
                break;
            }
            try {
                startId = getNextId(values);
            } catch (Exception e) {
                throw ServiceException.getInternalException(e.getMessage());
            }
        } while (!isStop());
        long cachePreEnableVersionCode = getEnableCacheVersionCode();
        saveEnableCacheVersionCode(versionCode);
        expirePreVersionCache(cachePreEnableVersionCode);
        getLogger().info("Finish load " + getName() + " data.");
    }


    @Override
    public void deltaCacheInitLoad(List<Integer> ids) throws ServiceException {
        List<T> datas = getDirectFromDb(ids);
        putToCacheDb(datas);
    }

    protected void afterModifyStatus(List<T> gg) {
        List<Integer> ids = new ArrayList<Integer>();
        for (T g : gg) {
            ids.add(getIdFromG(g));
        }
        deltaCacheInitLoad(ids);
    }
}
