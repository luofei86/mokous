// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.winexeproxy.service.impl;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.springframework.stereotype.Service;

import com.mokous.core.service.AsyncService;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
@Service
public class AsyncServiceImpl implements AsyncService {
    private static final int MAX_INT = 100;
    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 3, 60, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(MAX_INT));

    @Override
    public boolean async(Runnable runnable) {
        if (threadPoolExecutor.getQueue().size() >= MAX_INT) {
            return false;
        }
        try {
            threadPoolExecutor.execute(runnable);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @PreDestroy
    public void shutdown() {
        threadPoolExecutor.shutdown();
    }
}
