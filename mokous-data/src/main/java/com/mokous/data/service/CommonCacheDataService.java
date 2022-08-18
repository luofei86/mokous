package com.mokous.data.service;

import java.util.List;

import com.mokous.core.dto.DbData;



public interface CommonCacheDataService<G extends DbData> {

    /**
     * ONLY RETURN LEGAL DATA
     * 
     * @param id
     * @return
     */
    public abstract G getData(int id);

    public abstract List<G> getData(List<Integer> ids);

}
