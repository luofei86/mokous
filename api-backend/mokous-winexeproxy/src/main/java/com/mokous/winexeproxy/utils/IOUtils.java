// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.winexeproxy.utils;

import java.io.Closeable;


/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
public class IOUtils {
    public static final String LINE_SEP = System.getProperty("line.separator");


    public static final void safeClose(Closeable io) {
        if (io != null) {
            try {
                io.close();
            } catch (Exception e) {
            }
        }
    }
}
