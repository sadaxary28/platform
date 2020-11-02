package com.infomaximum.platform.component.frontend.engine.authorize;

import com.infomaximum.cluster.graphql.struct.GRequest;
import com.infomaximum.platform.component.frontend.authcontext.UnauthorizedContext;
import com.infomaximum.platform.sdk.component.Component;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.infomaximum.subsystems.querypool.QueryPool;
import com.infomaximum.subsystems.querypool.ResourceProvider;

public interface RequestAuthorize {

    QueryPool.Priority getRequestPriority();

    UnauthorizedContext authorize(ContextTransaction contextTransaction);

    interface Builder {
        RequestAuthorize build(Component component, GRequest gRequest, ResourceProvider resources);
    }
}
