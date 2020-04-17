package com.infomaximum.subsystems.querypool;

import com.infomaximum.subsystems.exception.SubsystemException;

public abstract class Query<T> {

    public abstract void prepare(ResourceProvider resources) throws SubsystemException;

    public QueryPool.Priority getPriority(){
        return QueryPool.Priority.HIGH;
    }

    public String getMaintenanceMarker(){
        return null;
    }

    public abstract T execute(QueryTransaction transaction) throws SubsystemException;
}
