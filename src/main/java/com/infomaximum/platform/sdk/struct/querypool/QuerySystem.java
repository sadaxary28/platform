package com.infomaximum.platform.sdk.struct.querypool;

import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.infomaximum.subsystems.exception.SubsystemException;
import com.infomaximum.subsystems.querypool.ResourceProvider;

public abstract class QuerySystem<T> {

    public abstract void prepare(ResourceProvider resources) throws SubsystemException;

    public abstract T execute(ContextTransaction context) throws SubsystemException;
}
