// Copyright 2015 ios.appchina.com Inc. All Rights Reserved.

package com.mokous.core.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.mokous.core.dao.CommonDao;
import com.mokous.web.exception.ServiceException;
import com.mokous.web.utils.CollectionUtils;

/**
 * @author luofei@appchina.com (Your Name Here)
 *
 */
public abstract class AbsCommonDataService<G> implements CommonDataService<G> {
    private static final int MAX_QUERY_LIST_SIZE = 1000;

    @Override
    public List<G> listDirectFromDb(G g) throws ServiceException {
        List<G> datas = new ArrayList<G>();
        int start = 0;
        List<G> innerDatas = null;
        do {
            innerDatas = listDirectFromDb(g, start, MAX_QUERY_LIST_SIZE);
            if (CollectionUtils.emptyOrNull(innerDatas)) {
                break;
            }
            datas.addAll(innerDatas);
            if (innerDatas.size() < MAX_QUERY_LIST_SIZE) {
                break;
            }
            innerDatas.clear();
            start += MAX_QUERY_LIST_SIZE;
        } while (true);
        return datas;
    }

    public abstract CommonDao<G> getCommonDao();

    @Override
    public List<G> listDirectFromDb(G g, int start, int size) throws ServiceException {
        try {
            return getCommonDao().queryList(g, start, size);
        } catch (SQLException e) {
            throw ServiceException.getSQLException(e);
        }
    }

    @Override
    public List<G> listByStartIdDirectFromDb(G g, int startId, int size) throws ServiceException {
        try {
            return getCommonDao().queryListByStartId(g, startId, size);
        } catch (SQLException e) {
            throw ServiceException.getSQLException(e);
        }
    }

    @Override
    public List<G> listByEndIdDirectFromDb(G g, int endId, int size) throws ServiceException {
        try {
            return getCommonDao().queryListByEndId(g, endId, size);
        } catch (SQLException e) {
            throw ServiceException.getSQLException(e);
        }
    }

    @Override
    public List<G> listDirectFromDb(G g, String st, String et, int start, int size) throws ServiceException {
        if (StringUtils.isEmpty(st) && StringUtils.isEmpty(et)) {
            return listDirectFromDb(g, start, size);
        }
        try {
            return getCommonDao().queryList(g, st, et, start, size);
        } catch (SQLException e) {
            throw ServiceException.getSQLException(e);
        }
    }

    @Override
    public G getDirectFromDb(int id) throws ServiceException {
        try {
            return getCommonDao().queryObject(id);
        } catch (SQLException e) {
            throw ServiceException.getSQLException(e);
        }
    }

    @Override
    public List<G> getDirectFromDb(List<Integer> ids) throws ServiceException {
        try {
            return getCommonDao().queryList(ids);
        } catch (SQLException e) {
            throw ServiceException.getSQLException(e);
        }
    }


    @Override
    public void addData(G g) throws ServiceException {
        g = beforToDb(g);
        try {
            getCommonDao().insertOrUpdate(g);
        } catch (SQLException e) {
            throw ServiceException.getSQLException(e);
        }
        afterAddData(g);
    }

    @Override
    public boolean addOrIgnoreData(G g) throws ServiceException {
        boolean ret = false;
        g = beforToDb(g);
        try {
            ret = getCommonDao().insertOrIgnore(g);
        } catch (SQLException e) {
            throw ServiceException.getSQLException(e);
        }
        if (ret) {
            afterAddData(g);
        }
        return ret;
    }

    protected void afterAddData(G g) {
        List<G> gg = new ArrayList<G>();
        gg.add(g);
        afterAddData(gg);
    }

    protected void afterAddData(List<G> gg) {
        afterModifyData(gg);
    }

    protected void afterModifyData(G g) {
        if (g == null) {
            throw ServiceException.getInternalException("The modify object is null");
        }
        List<G> gg = new ArrayList<G>();
        gg.add(g);
        afterModifyData(gg);
    }

    protected void beforeModifyData(G g) {
        if (g == null) {
            throw ServiceException.getInternalException("The modify object is null");
        }
        List<G> gg = new ArrayList<G>();
        gg.add(g);
        beforeModifyData(gg);
    }

    protected void beforeModifyData(List<G> gg) {
    }

    protected void afterModifyData(List<G> gg) {
    }

    protected void afterModifyStatus(List<G> gg) {
    }

    protected G beforToDb(G g) {
        return g;
    }

    protected List<G> beforToDb(List<G> gg) {
        for (G g : gg) {
            beforToDb(g);
        }
        return gg;
    }

    @Override
    public void modifyStatus(G g) throws ServiceException {
        if (g == null) {
            return;
        }
        List<G> gg = new ArrayList<G>();
        gg.add(g);
        modiftStatus(gg);
    }

    @Override
    public void modiftStatus(List<G> gg) throws ServiceException {
        beforeModifyData(gg);
        try {
            getCommonDao().updateStatus(gg);
        } catch (SQLException e) {
            throw ServiceException.getSQLException(e);
        }
        afterModifyStatus(gg);
    }

    @Override
    public long countDirectFromDb(G g) throws ServiceException {
        try {
            return getCommonDao().count(g);
        } catch (SQLException e) {
            throw ServiceException.getSQLException(e);
        }
    }

    @Override
    public long countDirectFromDb(G g, String st, String et) throws ServiceException {
        if (StringUtils.isEmpty(st) && StringUtils.isEmpty(et)) {
            return countDirectFromDb(g);
        }
        try {
            return getCommonDao().count(g, st, et);
        } catch (SQLException e) {
            throw ServiceException.getSQLException(e);
        }
    }

    @Override
    public void batchAdd(List<G> gg) throws ServiceException {
        gg = beforToDb(gg);
        try {
            getCommonDao().insertOrUpdate(gg);
        } catch (SQLException e) {
            throw ServiceException.getSQLException(e);
        }
        afterAddData(gg);
    }

}
