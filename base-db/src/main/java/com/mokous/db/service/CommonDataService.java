//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.db.service;

import com.mokous.base.exception.BizException;
import org.springframework.util.CollectionUtils;

import java.net.BindException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author luofei
 * Generate 2020/1/13
 */
public interface CommonDataService<G> extends NormalCommonDataService<G> {
    default List<G> listByStartIdDirectFromDb(G g, int startId, int size) throws BizException {
        return getCommonDao(g).queryListByStartId(g, startId, size);
    }

    default List<G> listByEndIdDirectFromDb(G g, int endId, int size) throws BizException {
        return getCommonDao(g).queryListByEndId(g, endId, size);
    }

    default G getDirectFromDb(int id) throws BizException {
        return getCommonDao(null).queryObject(id);
    }

    default List<G> getDirectFromDb(List<Integer> ids) throws BizException {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return getCommonDao(null).queryList(ids);
    }
}
