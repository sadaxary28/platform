package com.infomaximum.platform.sdk.context.impl;

import com.infomaximum.platform.sdk.context.Context;
import com.infomaximum.platform.sdk.context.source.Source;

public class ContextImpl implements Context {

    private final Source source;

    public ContextImpl(Source source) {
        this.source = source;
    }

    @Override
    public Source getSource() {
        return source;
    }
}

