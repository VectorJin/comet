package com.vector.comet.netty;

import com.vector.comet.constants.BaseConstants;
import com.vector.comet.util.thread.AbortPolicyWithReport;
import com.vector.comet.util.thread.NamedThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by jinku on 2018/3/31.
 */
public class CometThreadPool {

    private static CometThreadPool instance = new CometThreadPool();

    private ExecutorService executor;

    private CometThreadPool() {
        executor =  new ThreadPoolExecutor(BaseConstants.COMET_THREAD_NUM, BaseConstants.COMET_THREAD_NUM,
                0, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>(),
                new NamedThreadFactory("comet", true), new AbortPolicyWithReport("comet"));
    }

    public static CometThreadPool getInstance() {
        return instance;
    }

    public void execute(Runnable task) {
        executor.execute(task);
    }
}
