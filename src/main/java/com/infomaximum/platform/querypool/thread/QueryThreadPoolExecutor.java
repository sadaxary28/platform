package com.infomaximum.platform.querypool.thread;

import java.util.concurrent.*;

public class QueryThreadPoolExecutor extends ThreadPoolExecutor {

    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    public QueryThreadPoolExecutor(
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> workQueue,
            Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
                Thread.ofVirtual().name("VQueryPool", 1).uncaughtExceptionHandler(uncaughtExceptionHandler).factory()
        );
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);

        if (t == null) {
            try {
                Future<?> future = (Future<?>) r;
                if (future.isDone()) {
                    future.get();
                }
            } catch (ExecutionException e) {
                t = e.getCause();
            } catch (Throwable e) {
                t = e;
            }
        }

        if (t != null) {
            uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), t);
        }
    }
}
