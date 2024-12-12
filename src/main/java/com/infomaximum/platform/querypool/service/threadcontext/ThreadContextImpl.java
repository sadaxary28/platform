package com.infomaximum.platform.querypool.service.threadcontext;

import com.infomaximum.platform.sdk.context.Context;


public class ThreadContextImpl implements ThreadContext {

    private final ThreadLocal<Context> threadContexts;

    public ThreadContextImpl() {
        threadContexts = new ThreadLocal<Context>();
    }

    public void setContext(Context context) {
        threadContexts.set(context);
    }

    public void clearContext() {
        threadContexts.remove();
    }

    @Override
    public Context getContext() {
        return threadContexts.get();
    }
}
