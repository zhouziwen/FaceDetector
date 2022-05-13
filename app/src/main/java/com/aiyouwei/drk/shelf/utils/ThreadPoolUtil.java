package com.aiyouwei.drk.shelf.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtil {
    /**
     * 定义线程池
     */
    //当前设备的cpu数量
    public static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    //保持存活数量
    public static int KEEP_ALIVE_TIME = 1;
    //保持存活数量单位
    public static TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    //同步队列
    public static SynchronousQueue executorQueue = new SynchronousQueue<Runnable>();
    //阻塞队列
    public static LinkedBlockingDeque linkedBlockingDeque = new LinkedBlockingDeque<Runnable>();
    public static ExecutorService executorService =
            new ThreadPoolExecutor( NUMBER_OF_CORES, NUMBER_OF_CORES * 2,
                    KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, linkedBlockingDeque );


    /**
     * 异步回调失败的线程池可延时操作
     * 定时5分钟执行
     */
    public static ScheduledExecutorService callBackFailThreadPool = Executors.newScheduledThreadPool( NUMBER_OF_CORES );


    /**
     * 运行的线程时间
     */
    public static int ThreadRunNo() {
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        while (threadGroup.getParent() != null) {
            threadGroup = threadGroup.getParent();
        }
        return threadGroup.activeCount();
    }

}
