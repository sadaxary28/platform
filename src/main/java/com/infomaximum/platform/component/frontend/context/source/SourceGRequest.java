package com.infomaximum.platform.component.frontend.context.source;

import com.infomaximum.cluster.graphql.struct.GRequest;
import com.infomaximum.platform.sdk.context.source.Source;

public interface SourceGRequest extends Source {

    GRequest getRequest();
}
