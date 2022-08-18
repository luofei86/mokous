package com.mokous.data.dao;

import java.sql.SQLException;
import java.util.List;

public interface LoadDao<G> {

    List<G> queryList(int startId, int size) throws SQLException;

}
