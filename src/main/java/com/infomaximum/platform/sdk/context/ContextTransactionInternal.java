package com.infomaximum.platform.sdk.context;

import com.infomaximum.subsystems.querypool.QueryTransaction;

public interface ContextTransactionInternal {

    void setTransaction(QueryTransaction transaction);
}
