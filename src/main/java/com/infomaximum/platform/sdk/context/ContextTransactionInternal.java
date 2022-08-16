package com.infomaximum.platform.sdk.context;

import com.infomaximum.platform.querypool.QueryTransaction;

public interface ContextTransactionInternal {

    void setTransaction(QueryTransaction transaction);
}
