package com.infomaximum.platform.sdk.subscription;

import com.infomaximum.cluster.graphql.struct.GSubscribeEvent;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.sdk.component.Component;
import com.infomaximum.platform.sdk.context.Context;
import com.infomaximum.platform.sdk.context.ContextTransaction;

public class GraphQLSubscribeEvent {

    private final com.infomaximum.cluster.graphql.executor.subscription.GraphQLSubscribeEvent graphQLSubscribeEvent;

    public GraphQLSubscribeEvent(Component component) {
        this.graphQLSubscribeEvent = new com.infomaximum.cluster.graphql.executor.subscription.GraphQLSubscribeEvent(component);
    }

    public void push(GSubscribeEvent<?> event, Context context) {
        if (context instanceof ContextTransaction &&
                !((ContextTransaction) context).getTransaction().closed()) {
            ContextTransaction contextTransaction = (ContextTransaction) context;
            push(event, contextTransaction.getTransaction());
        } else {
            graphQLSubscribeEvent.pushEvent(event);
        }
    }

    public void push(GSubscribeEvent<?> event, QueryTransaction transaction) {
        transaction.addCommitListener(
                event.getSubscribeValue().subscribeKey, () -> graphQLSubscribeEvent.pushEvent(event));
    }
}
