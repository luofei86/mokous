//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.db.service;

import com.mokous.base.exception.BizException;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luofei
 * Generate 2020/1/13
 */
public interface CommonDataLongIdService<G> extends NormalCommonDataService<G> {
    default List<G> listByStartIdDirectFromDb(G g, long startId, int size) throws BizException {
        return getCommonDao(g).queryListByStartId(g, startId, size);
    }

    default List<G> listByEndIdDirectFromDb(G g, long endId, int size) throws BizException {
        return getCommonDao(g).queryListByEndId(g, endId, size);
    }

    default G getDirectFromDb(long id) throws BizException {
        return getCommonDao(null).queryObject(id);
    }

    default List<G> getDirectFromDb(List<Long> ids) throws BizException {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return getCommonDao(null).queryList(ids);
    }
}
