package com.infomaximum.platform.component.frontend.engine.authorize;

import com.infomaximum.cluster.graphql.struct.GRequest;
import com.infomaximum.platform.component.frontend.authcontext.UnauthorizedContext;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryPool;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.component.Component;

public interface RequestAuthorize {

    QueryPool.Priority getRequestPriority();

    UnauthorizedContext authorize(ContextTransactionRequest context) throws PlatformException;

    interface Builder {
        RequestAuthorize build(Component component, GRequest gRequest, ResourceProvider resources) throws PlatformException;
    }
}
