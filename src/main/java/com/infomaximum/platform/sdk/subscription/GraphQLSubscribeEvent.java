package com.infomaximum.platform.sdk.subscription;

import com.infomaximum.cluster.graphql.struct.GSubscribeEvent;
import com.infomaximum.platform.sdk.component.Component;
import com.infomaximum.subsystems.querypool.QueryTransaction;

public class GraphQLSubscribeEvent {

    private final com.infomaximum.cluster.graphql.executor.subscription.GraphQLSubscribeEvent graphQLSubscribeEvent;

    public GraphQLSubscribeEvent(Component component) {
        this.graphQLSubscribeEvent = new com.infomaximum.cluster.graphql.executor.subscription.GraphQLSubscribeEvent(component);
    }

    public void push(GSubscribeEvent<?> event, QueryTransaction transaction) {
        transaction.addCommitListener(
                event.getSubscribeValue().subscribeKey, () -> graphQLSubscribeEvent.pushEvent(event));
    }

    public void push(GSubscribeEvent<?> event) {
        graphQLSubscribeEvent.pushEvent(event);
    }
}
