package com.infomaximum.platform.sdk.subscription;

import com.infomaximum.cluster.graphql.struct.GSubscribeEvent;
import com.infomaximum.platform.sdk.component.Component;
import com.infomaximum.platform.sdk.context.Context;
import com.infomaximum.platform.sdk.context.ContextTransaction;

public class GraphQLSubscribeEvent {

    private final com.infomaximum.cluster.graphql.executor.subscription.GraphQLSubscribeEvent graphQLSubscribeEvent;

    public GraphQLSubscribeEvent(Component component) {
        this.graphQLSubscribeEvent = new com.infomaximum.cluster.graphql.executor.subscription.GraphQLSubscribeEvent(component);
    }

    public void push(GSubscribeEvent event, Context context) {
        if (context instanceof ContextTransaction &&
                !((ContextTransaction) context).getTransaction().closed()) {
            ContextTransaction contextTransaction = (ContextTransaction) context;
            contextTransaction.getTransaction().addCommitListener(() -> {
                graphQLSubscribeEvent.pushEvent(event);
            });
        } else {
            graphQLSubscribeEvent.pushEvent(event);
        }
    }

}
