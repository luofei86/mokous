package com.mokous.core.dao;

import java.sql.SQLException;
import java.util.List;

public interface LoadDao<T> {

    List<T> queryList(int startId, int size) throws SQLException;

}
