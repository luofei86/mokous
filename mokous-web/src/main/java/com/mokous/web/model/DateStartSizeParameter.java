// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.web.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月4日
 * 
 */
public class DateStartSizeParameter extends StartSizeParameter {
    private static final String RANGE_TIME_SPLIT = " - ";
    private static final String TIME_MMDDYYYY_FORMAT = "MM/dd/yyyy";
    private static final String STET_YYYYMMDD_FORMAT = "yyyy-MM-dd";
    private String st;
    private String et;
    private String time;

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }

    public String getEt() {
        return et;
    }

    public void setEt(String et) {
        this.et = et;
    }

    public void transfer() {
        if (!StringUtils.isEmpty(time)) {
            String[] times = time.split(RANGE_TIME_SPLIT);
            if (times.length == 2) {
                st = times[0];
                et = times[1];
                if (!StringUtils.isEmpty(st)) {
                    try {
                        st = new SimpleDateFormat(STET_YYYYMMDD_FORMAT).format((new SimpleDateFormat(
                                TIME_MMDDYYYY_FORMAT).parse(st.trim())));
                    } catch (ParseException e) {
                    }
                }
                if (!StringUtils.isEmpty(et)) {
                    try {
                        et = new SimpleDateFormat(STET_YYYYMMDD_FORMAT).format((new SimpleDateFormat(
                                TIME_MMDDYYYY_FORMAT).parse(et.trim())));
                    } catch (ParseException e) {
                    }
                }
            }
        }
        start = (pager.getPage() - 1) * pager.getSize();
        size = pager.getSize();
        if (StringUtils.isEmpty(st)) {
            st = new SimpleDateFormat(STET_YYYYMMDD_FORMAT).format(DateUtils.addMonths(new Date(), -1));
        }
        if (StringUtils.isEmpty(et)) {
            et = new SimpleDateFormat(STET_YYYYMMDD_FORMAT).format(DateUtils.addDays(new Date(), 1));
        }
        if (StringUtils.isEmpty(time)) {
            try {
                time = new SimpleDateFormat(TIME_MMDDYYYY_FORMAT).format((new SimpleDateFormat(STET_YYYYMMDD_FORMAT)
                        .parse(st.trim())))
                        + RANGE_TIME_SPLIT
                        + new SimpleDateFormat(TIME_MMDDYYYY_FORMAT).format((new SimpleDateFormat(STET_YYYYMMDD_FORMAT)
                                .parse(et.trim())));
            } catch (ParseException e) {
            }
        }
    }

    public void transfer(int pageSize) {
        pager.setSize(pageSize);
        transfer();
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
