// Copyright 2018 https://mokous.com Inc. All Rights Reserved.

package com.mokous.data.service;

import java.util.List;

import com.mokous.web.exception.ServiceException;


/**
 * 通用数据操作类
 * 
 * @author mokous86@gmail.com
 * 
 *         create date: Jan 31, 2018
 *
 */
public interface CommonDataService<G> {
    /**
     * 通过g中的条件获得列表，列表以id序从小到大返回
     * 
     * @param g
     * @return
     * @throws ServiceException
     */
    public List<G> listDirectFromDb(G g) throws ServiceException;

    /**
     * 通过g中的条件获得列表，列表按指定序返回
     * 
     * @param g
     * @param orderColumn
     * @param orderAsc
     * @return
     * @throws ServiceException
     */
    public List<G> listDirectFromDb(G g, String orderColumn, boolean orderAsc) throws ServiceException;

    /**
     * 通过g中的条件及创建时间st,et获得列表，列表以id序从小到大返回
     * 
     * @param g
     * @param st
     * @param et
     * @param start
     * @param size
     * @return
     * @throws ServiceException
     */
    public List<G> listDirectFromDb(G g, String st, String et, int start, int size) throws ServiceException;

    /**
     * 通过g中的条件及创建时间st,et获得列表，列表按指定序返回
     * 
     * @param g
     * @param st
     * @param et
     * @param start
     * @param size
     * @param orderColumn
     * @param orderAsc
     * @return
     * @throws ServiceException
     */
    public List<G> listDirectFromDb(G g, String st, String et, int start, int size, String orderColumn, boolean orderAsc)
            throws ServiceException;

    /**
     * 通过g中的条件按id序从小到大返回数据
     * 
     * @param g
     * @param start
     * @param size
     * @return
     * @throws ServiceException
     */
    public List<G> listDirectFromDb(G g, int start, int size) throws ServiceException;

    /**
     * 通过g中的条件按指定序返回数据
     * 
     * @param g
     * @param start
     * @param size
     * @param orderColumn
     * @param orderAsc
     * @return
     * @throws ServiceException
     */
    public List<G> listDirectFromDb(G g, int start, int size, String orderColumn, boolean orderAsc)
            throws ServiceException;

    /**
     * 通过g中的条件从id>startId开始按id序从小到大返回数据
     * 
     * @param g
     * @param startId
     * @param size
     * @return
     * @throws ServiceException
     */
    public List<G> listByStartIdDirectFromDb(G g, int startId, int size) throws ServiceException;

    /**
     * 通过g中的条件从id>startId开始按指定序返回数据
     * 
     * @param g
     * @param startId
     * @param size
     * @param orderColumn
     * @param orderAsc
     * @return
     * @throws ServiceException
     */
    public List<G> listByStartIdDirectFromDb(G g, int startId, int size, String orderColumn, boolean orderAsc)
            throws ServiceException;

    /**
     * 通过g中的条件从id<endId开始按id序从大到小返回数据
     * 
     * @param g
     * @param startId
     * @param size
     * @return
     * @throws ServiceException
     */
    public List<G> listByEndIdDirectFromDb(G g, int endId, int size) throws ServiceException;

    /**
     * 通过g中的条件从id<endId开始按指定序返回数据
     * 
     * @param g
     * @param endId
     * @param size
     * @param orderColumn
     * @param orderAsc
     * @return
     * @throws ServiceException
     */
    public List<G> listByEndIdDirectFromDb(G g, int endId, int size, String orderColumn, boolean orderAsc)
            throws ServiceException;

    /**
     * 按g中的条件返回符合条件的记录数
     * 
     * @param g
     * @return
     * @throws ServiceException
     */
    public long countDirectFromDb(G g) throws ServiceException;

    /**
     * 
     * @param g
     * @param st
     * @param et
     * @return
     * @throws ServiceException
     */
    public long countDirectFromDb(G g, String st, String et) throws ServiceException;

    /**
     * 获取id对应的数据
     * 
     * @param id
     * @return
     * @throws ServiceException
     */
    public G getDirectFromDb(int id) throws ServiceException;

    /**
     * 通过id列表获取数据
     * 
     * @param ids
     * @return
     * @throws ServiceException
     */
    public List<G> getDirectFromDb(List<Integer> ids) throws ServiceException;

    /**
     * 批量向数据库中插入数据，此方法不对数据进行ignore或on duplicate key
     * update，所以对于违反唯一性约束的记录，将会返回数据库异常
     * 
     * @param gg
     * @throws ServiceException
     */
    public void batchAdd(List<G> gg) throws ServiceException;

    /**
     * 批量向数据库中插入数据，此方法将在数据有冲突时on duplicate key update具体更新的字段取决于具体的业务
     * 
     * @param gg
     * @throws ServiceException
     */
    public void batchAddOrUpdate(List<G> gg) throws ServiceException;

    /**
     * 批量向数据库中插入数据，此方法对唯一性冲突的插入数据进行ignore，不返回约束冲突异常
     * 
     * @param gg
     * @throws ServiceException
     */
    public void batchIgnore(List<G> gg) throws ServiceException;

    /**
     * 向数据库中插入数据，此方法对唯一性冲突的插入数据进行ignore，不返回约束冲突异常
     * 
     * 当数据为插入成功时返回true，当数据插入ignore时返回false
     * 
     * @param g
     * @return
     * @throws ServiceException
     */
    public boolean addOrIgnoreData(G g) throws ServiceException;

    /**
     * 向数据库中插入数据，此方法不对数据进行ignore或on duplicate key
     * update，所以对于违反唯一性约束的记录，将会返回数据库异常
     * 
     * @param g
     * @throws ServiceException
     */
    public boolean addData(G g) throws ServiceException;

    /**
     * 向数据库中插入数据，此方法将在数据有冲突时on duplicate key update具体更新的字段取决于具体的业务
     * 
     * @param g
     * @throws ServiceException
     */
    public boolean addDataOrUpdate(G g) throws ServiceException;

    /**
     * 更新数据的del_flag列
     * 
     * @param g
     * @return
     * @throws ServiceException
     */
    public boolean modifyStatus(G g) throws ServiceException;

    /**
     * 批量更新数据的del_flag列
     * 
     * @param gg
     * @throws ServiceException
     */
    public void modiftStatus(List<G> gg) throws ServiceException;

}
