package com.infomaximum.platform.sdk.graphql.fieldconfiguration.struct;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.platform.component.frontend.authcontext.UnauthorizedContext;

public class FieldConfiguration implements RemoteObject {

    public final Class<? extends UnauthorizedContext>[] typeAuthContexts;

    public FieldConfiguration(Class<? extends UnauthorizedContext>... typeAuthContexts) {
        this.typeAuthContexts = typeAuthContexts;
    }

}
