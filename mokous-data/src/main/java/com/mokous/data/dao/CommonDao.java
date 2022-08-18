// Copyright 2015 ios.appchina.com Inc. All Rights Reserved.

package com.mokous.data.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * @author luofei@appchina.com (Your Name Here)
 *
 */
public interface CommonDao<G> extends LoadDao<G> {
    public long count(G g) throws SQLException;

    public long count(G g, String st, String et) throws SQLException;

    public List<G> queryList(G g, int start, int size) throws SQLException;

    public List<G> queryListByStartId(G g, int startId, int size) throws SQLException;

    public List<G> queryListByStartId(G g, int startId, int size, String orderColumn, boolean orderAsc)
            throws SQLException;

    public List<G> queryListByEndId(G g, int endId, int size) throws SQLException;

    public List<G> queryListByEndId(G g, int endId, int size, String orderColumn, boolean orderAsc) throws SQLException;

    public List<G> queryList(G g, String st, String et, int start, int size) throws SQLException;

    public List<G> queryList(G g, String st, String et, int start, int size, String orderColumn, boolean orderAsc)
            throws SQLException;

    public List<G> queryList(List<Integer> ids) throws SQLException;

    public G queryObject(int id) throws SQLException;

    public boolean insertOrUpdate(G g) throws SQLException;

    public boolean insert(G g) throws SQLException;

    public boolean insertOrIgnore(G g) throws SQLException;

    public void insertOrUpdate(List<G> gg) throws SQLException;

    public void insert(List<G> gg) throws SQLException;

    public void insertOrIgnore(List<G> gg) throws SQLException;

    public boolean updateStatus(G g) throws SQLException;

    public void updateStatus(List<G> gg) throws SQLException;

    public void update(G g) throws SQLException;

    public SqlMapClient getSqlMapClient();

    public Map<String, Object> buildListPara(G g);

    public Map<String, Object> buildCountPara(G g);
}
