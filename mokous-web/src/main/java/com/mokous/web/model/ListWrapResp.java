// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.web.model;

import java.util.List;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月4日
 * 
 */
public class ListWrapResp<G> {
    private long totalCount;
    private List<G> resultList;
    private boolean more;
    private int next;
    private int prev;

    public ListWrapResp(List<G> resultList) {
        this(resultList == null ? 0 : resultList.size(), resultList, false, resultList == null ? 0 : resultList.size(),
                resultList == null ? 0 : resultList.size());
    }

    public ListWrapResp(long totalCount, List<G> resultList, int start, int size) {
        this(totalCount, resultList, totalCount > start + size, start + (resultList == null ? 0 : resultList.size()));
    }

    public ListWrapResp(long totalCount, List<G> resultList, boolean more, int next) {
        this(totalCount, resultList, more, next, 0);
    }

    public ListWrapResp(long totalCount, List<G> resultList, boolean more, int next, int prev) {
        this.totalCount = totalCount;
        this.resultList = resultList;
        this.more = more;
        this.next = next;
        this.prev = prev;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public List<G> getResultList() {
        return resultList;
    }

    public void setResultList(List<G> resultList) {
        this.resultList = resultList;
    }

    public boolean isMore() {
        return more;
    }

    public void setMore(boolean more) {
        this.more = more;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int nextStart) {
        this.next = nextStart;
    }

    @Override
    public String toString() {
        return "ListWrapResp [totalCount=" + totalCount + ", resultList=" + resultList + ", more=" + more + ", next="
                + next + ", prev=" + prev + "]";
    }

    public int getPrev() {
        return prev;
    }

    public void setPrev(int prev) {
        this.prev = prev;
    }
}
