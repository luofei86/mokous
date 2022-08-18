// Copyright 2018 https://mokous.com Inc. All Rights Reserved.

package com.mokous.data.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.mokous.core.dto.DbData;
import com.mokous.data.dao.CommonDao;
import com.mokous.web.exception.ServiceException;
import com.mokous.web.utils.CollectionUtils;

/**
 * 通用数据操作抽象实现
 * 
 * @author mokous86@gmail.com
 * 
 *         create date: Feb 1, 2018
 *
 */
public abstract class AbstractCommonDataService<G extends DbData> implements CommonDataService<G> {
    private static final Logger logger = Logger.getLogger(AbstractCommonDataService.class);
    private static final int MAX_QUERY_LIST_SIZE = 1000;

    @Override
    public List<G> listDirectFromDb(G g) throws ServiceException {
        return listDirectFromDb(g, DbData.ID_COLUMN, true);
    }

    @Override
    public List<G> listDirectFromDb(G g, String orderColumn, boolean orderAsc) throws ServiceException {
        List<G> datas = new ArrayList<G>();
        int start = 0;
        List<G> innerDatas = null;
        do {
            innerDatas = listDirectFromDb(g, start, MAX_QUERY_LIST_SIZE, orderColumn, orderAsc);
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
        return listDirectFromDb(g, start, size, DbData.ID_COLUMN, true);
    }

    @Override
    public List<G> listDirectFromDb(G g, String st, String et, int start, int size, String orderColumn, boolean orderAsc)
            throws ServiceException {
        try {
            return getCommonDao().queryList(g, st, et, start, size, orderColumn, orderAsc);
        } catch (SQLException e) {
            logger.error("List data from db failed.Errmsg:" + e.getMessage(), e);
            throw ServiceException.getSQLException("从数据库中获取数据异常.ErrorCode:" + e.getErrorCode());
        }
    }

    @Override
    public List<G> listDirectFromDb(G g, int start, int size, String orderColumn, boolean orderAsc)
            throws ServiceException {
        return listDirectFromDb(g, null, null, start, size, orderColumn, orderAsc);
    }

    @Override
    public List<G> listByStartIdDirectFromDb(G g, int startId, int size, String orderColumn, boolean orderAsc)
            throws ServiceException {
        try {
            return getCommonDao().queryListByStartId(g, startId, size, orderColumn, orderAsc);
        } catch (SQLException e) {
            logger.error("List data from db failed.Errmsg:" + e.getMessage(), e);
            throw ServiceException.getSQLException("从数据库中获取数据异常.ErrorCode:" + e.getErrorCode());
        }
    }

    @Override
    public List<G> listByEndIdDirectFromDb(G g, int endId, int size, String orderColumn, boolean orderAsc)
            throws ServiceException {
        try {
            return getCommonDao().queryListByEndId(g, endId, size, orderColumn, orderAsc);
        } catch (SQLException e) {
            logger.error("List data from db failed.Errmsg:" + e.getMessage(), e);
            throw ServiceException.getSQLException("从数据库中获取数据异常.ErrorCode:" + e.getErrorCode());
        }
    }

    @Override
    public List<G> listByStartIdDirectFromDb(G g, int startId, int size) throws ServiceException {
        return listByStartIdDirectFromDb(g, startId, size, DbData.ID_COLUMN, true);
    }

    @Override
    public List<G> listByEndIdDirectFromDb(G g, int endId, int size) throws ServiceException {
        return listByEndIdDirectFromDb(g, endId, size, DbData.ID_COLUMN, true);
    }

    @Override
    public List<G> listDirectFromDb(G g, String st, String et, int start, int size) throws ServiceException {
        return listDirectFromDb(g, st, et, start, size, DbData.ID_COLUMN, true);
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
    public long countDirectFromDb(G g) throws ServiceException {
        return countDirectFromDb(g, null, null);
    }

    @Override
    public long countDirectFromDb(G g, String st, String et) throws ServiceException {
        try {
            return getCommonDao().count(g, st, et);
        } catch (SQLException e) {
            throw ServiceException.getSQLException(e);
        }
    }

    @Override
    public boolean addData(G g) throws ServiceException {
        g = beforToDb(g);
        try {
            boolean insert = getCommonDao().insert(g);
            if (insert) {
                afterAddData(g);
            }
            return insert;
        } catch (SQLException e) {
            throw ServiceException.getSQLException(e);
        }
    }

    @Override
    public boolean addOrIgnoreData(G g) throws ServiceException {
        g = beforToDb(g);
        try {
            boolean insert = getCommonDao().insertOrIgnore(g);
            if (insert) {
                afterAddData(g);
            }
            return insert;
        } catch (SQLException e) {
            throw ServiceException.getSQLException(e);
        }
    }

    @Override
    public boolean modifyStatus(G g) throws ServiceException {
        if (g == null) {
            return false;
        }
        beforeModifyData(g);
        try {
            boolean update = getCommonDao().updateStatus(g);
            if (update) {
                afterModifyStatus(g);
            }
            return update;
        } catch (SQLException e) {
            throw ServiceException.getSQLException(e);
        }
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
    public void batchAdd(List<G> gg) throws ServiceException {
        gg = beforToDb(gg);
        try {
            getCommonDao().insert(gg);
        } catch (SQLException e) {
            throw ServiceException.getSQLException(e);
        }
        afterAddData(gg);
    }

    @Override
    public void batchAddOrUpdate(List<G> gg) throws ServiceException {
        gg = beforToDb(gg);
        try {
            getCommonDao().insertOrUpdate(gg);
        } catch (SQLException e) {
            throw ServiceException.getSQLException(e);
        }
        afterAddData(gg);
    }

    @Override
    public void batchIgnore(List<G> gg) throws ServiceException {
        gg = beforToDb(gg);
        try {
            getCommonDao().insertOrIgnore(gg);
        } catch (SQLException e) {
            throw ServiceException.getSQLException(e);
        }
        afterAddData(gg);
    }

    @Override
    public boolean addDataOrUpdate(G g) throws ServiceException {
        g = beforToDb(g);
        try {
            boolean insert = getCommonDao().insertOrUpdate(g);
            if (insert) {
                afterAddData(g);
            }
            return insert;
        } catch (SQLException e) {
            throw ServiceException.getSQLException(e);
        }
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

    protected void afterModifyStatus(G g) {
        List<G> gg = new ArrayList<G>(1);
        gg.add(g);
        afterModifyData(gg);
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

}
