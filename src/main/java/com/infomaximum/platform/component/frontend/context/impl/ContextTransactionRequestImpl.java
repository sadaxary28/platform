package com.infomaximum.platform.component.frontend.context.impl;

import com.infomaximum.cluster.graphql.struct.ContextRequest;
import com.infomaximum.cluster.graphql.struct.GRequest;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.component.frontend.context.source.SourceGRequestAuth;
import com.infomaximum.subsystems.querypool.QueryTransaction;

public class ContextTransactionRequestImpl implements ContextTransactionRequest, ContextRequest {

    private final SourceGRequestAuth source;
    private QueryTransaction transaction;

    public ContextTransactionRequestImpl(SourceGRequestAuth source) {
        this.source = source;
    }

    @Override
    public SourceGRequestAuth getSource() {
        return source;
    }

    @Override
    public QueryTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(QueryTransaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public GRequest getRequest() {
        return getSource().getRequest();
    }
}
