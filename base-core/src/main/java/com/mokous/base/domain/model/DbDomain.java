//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.base.domain.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author luofei
 * Generate 2020/1/12
 */
public abstract class DbDomain implements Serializable {
    private int id;
    @DbFieldConstraint.ZERO_ENABLE
    @DbFieldConstraint.SELECT_ALL_KEY
    private int delFlag;
    private Date updateTime;
    private Date createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(int delFlag) {
        this.delFlag = delFlag;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "DbDomain{" +
                "id=" + id +
                ", delFlag=" + delFlag +
                ", updateTime=" + updateTime +
                ", createTime=" + createTime +
                '}';
    }
}
