package com.infomaximum.platform.sdk.context.impl;

import com.infomaximum.cluster.graphql.struct.ContextRequest;
import com.infomaximum.cluster.graphql.struct.GRequest;
import com.infomaximum.platform.sdk.context.source.SourceGRequest;

public class ContextRequestImpl implements ContextRequest {

    private final SourceGRequest source;

    public ContextRequestImpl(SourceGRequest source) {
        this.source = source;
    }

    @Override
    public GRequest getRequest() {
        return source.getRequest();
    }
}