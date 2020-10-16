package com.infomaximum.platform.sdk.context.source;

import com.infomaximum.cluster.graphql.struct.GRequest;

public interface SourceGRequest extends Source {

    GRequest getRequest();

}
