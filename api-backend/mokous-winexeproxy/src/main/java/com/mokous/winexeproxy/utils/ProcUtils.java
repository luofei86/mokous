// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.winexeproxy.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.mokous.web.exception.ServiceException;


/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
public class ProcUtils {
    public static List<String> exeLauchServerCmd(String cmd) {
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            throw ServiceException.getInternalException(e.getMessage());
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        List<String> values = new ArrayList<String>();
        try {
            do {
                String lineValue = br.readLine();
                if (lineValue == null) {
                    break;
                }
                values.add(lineValue);
            } while (true);
        } catch (Exception e) {
            IOUtils.safeClose(br);
            throw ServiceException.getInternalException(e.getMessage());
        } finally {
            proc.destroy();
        }
        return values;
    }

}
