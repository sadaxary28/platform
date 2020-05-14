package com.infomaximum.platform.sdk.context.impl;

import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.infomaximum.platform.sdk.context.source.SourceSystem;
import com.infomaximum.subsystems.querypool.QueryTransaction;

public class ContextTransactionImpl implements ContextTransaction {

    private final SourceSystem source;
    private QueryTransaction transaction;

    public ContextTransactionImpl(SourceSystem source, QueryTransaction transaction) {
        this.source = source;
        this.transaction = transaction;
    }

    @Override
    public SourceSystem getSource() {
        return source;
    }

    @Override
    public QueryTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(QueryTransaction transaction) {
        this.transaction = transaction;
    }

}
