// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.core.dto;


/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
public class SortedInfo extends StatusSerializable implements Comparable<SortedInfo> {

    /**
     * 
     */
    private static final long serialVersionUID = -4739260111864630415L;

    @Override
    public int compareTo(SortedInfo o) {
        long value = (o.getUpdateTime().getTime() - this.getUpdateTime().getTime());
        return value > 0 ? 1 : -1;
    }

}
