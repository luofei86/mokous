// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.web.model;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月4日
 * 
 */
public class Pager {
    private int page = 1; // 页号
    private long total = -1; // 记录总数
    private int size = 30; // 每页显示记录数
    private int navigatePageNum = 6; // 导航页码数

    public Pager() {
    }

    public Pager(int size) {
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getNavigatePageNum() {
        return navigatePageNum;
    }

    public void setNavigatePageNum(int navigatePageNum) {
        this.navigatePageNum = navigatePageNum;
    }

    public int getAllPage() {
        int allPage = 0;
        if (total != 0 && total % size == 0) {
            allPage = (int) (total / size);
        } else {
            allPage = (int) (total / size) + 1;
        }
        return allPage;
    }

    public int getOffset() {
        return (page - 1) * size;
    }

    public Integer getPrePage() {
        return page == 1 ? null : page - 1;
    }

    public Integer getNextPage() {
        int allPage = getAllPage();
        return page == allPage || allPage == 0 ? null : page + 1;
    }

    public int[] getNaviPages() {
        int[] naviPages;
        int allPage = getAllPage();
        // 当总页数小于或等于导航页码数时
        if (allPage <= navigatePageNum) {
            naviPages = new int[allPage];
            for (int i = 0; i < allPage; i++) {
                naviPages[i] = i + 1;
            }
        } else { // 当总页数大于导航页码数时
            naviPages = new int[navigatePageNum];
            int startNum = page - navigatePageNum / 2;
            int endNum = page + navigatePageNum / 2;

            if (startNum < 1) {
                startNum = 1;
                // 最前navPageCount页
                for (int i = 0; i < navigatePageNum; i++) {
                    naviPages[i] = startNum++;
                }
            } else if (endNum > allPage) {
                endNum = allPage;
                // 最后navPageCount页
                for (int i = navigatePageNum - 1; i >= 0; i--) {
                    naviPages[i] = endNum--;
                }
            } else {
                // 所有中间页
                for (int i = 0; i < navigatePageNum; i++) {
                    naviPages[i] = startNum++;
                }
            }
        }
        return naviPages;
    }
}
