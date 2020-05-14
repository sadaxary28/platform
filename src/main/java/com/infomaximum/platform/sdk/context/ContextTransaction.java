package com.infomaximum.platform.sdk.context;

import com.infomaximum.platform.sdk.context.source.Source;
import com.infomaximum.subsystems.querypool.QueryTransaction;

public interface ContextTransaction<S extends Source> extends Context<S> {

    QueryTransaction getTransaction();

}
