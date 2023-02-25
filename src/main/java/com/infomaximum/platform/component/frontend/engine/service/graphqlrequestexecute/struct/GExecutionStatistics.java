package com.infomaximum.platform.component.frontend.engine.service.graphqlrequestexecute.struct;

import com.infomaximum.platform.component.frontend.authcontext.UnauthorizedContext;
import com.infomaximum.platform.querypool.QueryPool;

public record GExecutionStatistics(
        UnauthorizedContext authContext,
        QueryPool.Priority priority,
        long timeWait,
        long timeExec,
        String accessDenied
) {

}