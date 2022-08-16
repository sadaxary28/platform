package com.infomaximum.platform.sdk.struct.querypool;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.context.ContextTransaction;

public abstract class QuerySystem<T> {

    public abstract void prepare(ResourceProvider resources) throws PlatformException;

    public abstract T execute(ContextTransaction context) throws PlatformException;
}
