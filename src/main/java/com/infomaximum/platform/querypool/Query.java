package com.infomaximum.platform.querypool;

import com.infomaximum.platform.exception.PlatformException;

public abstract class Query<T> {

    public abstract void prepare(ResourceProvider resources) throws PlatformException;

    public QueryPool.Priority getPriority(){
        return QueryPool.Priority.HIGH;
    }

    public String getMaintenanceMarker(){
        return null;
    }

    public abstract T execute(QueryTransaction transaction) throws PlatformException;
}
