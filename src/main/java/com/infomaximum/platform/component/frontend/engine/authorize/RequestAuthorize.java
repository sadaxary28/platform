package com.infomaximum.platform.component.frontend.engine.authorize;

import com.infomaximum.cluster.graphql.struct.GRequest;
import com.infomaximum.platform.component.frontend.authcontext.UnauthorizedContext;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.sdk.component.Component;
import com.infomaximum.subsystems.exception.SubsystemException;
import com.infomaximum.subsystems.querypool.QueryPool;
import com.infomaximum.subsystems.querypool.ResourceProvider;

public interface RequestAuthorize {

    QueryPool.Priority getRequestPriority();

    UnauthorizedContext authorize(ContextTransactionRequest context) throws SubsystemException;

    interface Builder {
        RequestAuthorize build(Component component, GRequest gRequest, ResourceProvider resources) throws SubsystemException;
    }
}
