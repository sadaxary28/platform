package com.infomaximum.platform.sdk.context.source.impl;

import com.infomaximum.cluster.graphql.struct.GRequest;
import com.infomaximum.platform.sdk.context.source.SourceGRequest;

public class SourceGRequestImpl implements SourceGRequest {

    private final GRequest request;

    public SourceGRequestImpl(GRequest request) {
        this.request = request;
    }

    @Override
    public GRequest getRequest() {
        return request;
    }

}
