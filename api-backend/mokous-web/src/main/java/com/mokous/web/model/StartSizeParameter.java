// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.web.model;


/**
 * @author luofei (Your Name Here)
 * @date 2016年10月4日
 * 
 */
public class StartSizeParameter {
    protected Pager pager = new Pager();
    protected int start;
    protected int size;

    public void transfer(int size) {
        pager.setSize(size);
        transfer();
    }

    public void transfer() {
        this.setSize(pager.getSize());
        this.setStart((pager.getPage() - 1) * pager.getSize());
    }

    public static StartSizeParameter instanceAllList() {
        StartSizeParameter para = new StartSizeParameter();
        para.setSize(Integer.MAX_VALUE);
        return para;
    }

    public StartSizeParameter() {
        super();
        this.start = 0;
        this.size = 30;
    }

    public StartSizeParameter(int size) {
        super();
        this.start = 0;
        this.size = size;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }


    public Pager getPager() {
        return pager;
    }

    public void setPager(Pager pager) {
        this.pager = pager;
    }

    @Override
    public String toString() {
        return "StartSizeParameter [pager=" + pager + ", start=" + start + ", size=" + size + "]";
    }

}
