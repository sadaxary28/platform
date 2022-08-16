package com.infomaximum.platform.sdk.context;

import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.sdk.context.source.Source;

public interface ContextTransaction<S extends Source> extends Context<S> {

    QueryTransaction getTransaction();

}
