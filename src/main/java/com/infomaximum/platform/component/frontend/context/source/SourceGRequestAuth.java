package com.infomaximum.platform.component.frontend.context.source;

import com.infomaximum.platform.component.frontend.authcontext.UnauthorizedContext;

public interface SourceGRequestAuth extends SourceGRequest {

    UnauthorizedContext getAuthContext();
}
