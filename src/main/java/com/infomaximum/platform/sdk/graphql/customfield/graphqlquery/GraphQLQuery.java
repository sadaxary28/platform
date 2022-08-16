package com.infomaximum.platform.sdk.graphql.customfield.graphqlquery;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ResourceProvider;

import java.io.Serializable;

public abstract class GraphQLQuery<S extends RemoteObject, T extends Serializable> {

    public abstract void prepare(ResourceProvider resources);

    public abstract T execute(
            S source,
            ContextTransactionRequest context
    ) throws PlatformException;

}
