package com.infomaximum.platform.sdk.graphql.customfield.graphqlquery;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.exception.GraphQLExecutorDataFetcherException;
import com.infomaximum.cluster.graphql.preparecustomfield.PrepareCustomField;
import com.infomaximum.cluster.graphql.struct.ContextRequest;
import com.infomaximum.cluster.graphql.struct.GRequest;
import com.infomaximum.cluster.struct.Component;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GraphQLQueryCustomField implements PrepareCustomField<GraphQLQuery> {

    private final ConcurrentMap<GRequest, ConcurrentMap<String, GraphQLQuery>> graphQLRequests;

    public GraphQLQueryCustomField() {
        graphQLRequests = new ConcurrentHashMap<>();
    }

    @Override
    public boolean isSupport(Class clazz) {
        return (GraphQLQuery.class.isAssignableFrom(clazz));
    }

    @Override
    public Type getEndType(Type genericType) {
        return ((ParameterizedType) genericType).getActualTypeArguments()[1];
    }

    @Override
    public Serializable requestPrepare(Component component, String keyField, GraphQLQuery value, ContextRequest context) {
        ConcurrentMap<String, GraphQLQuery> requestQueries = graphQLRequests.computeIfAbsent(
                context.getRequest(),
                s -> new ConcurrentHashMap<>()
        );

        if (requestQueries.putIfAbsent(keyField, value) != null) {
            throw new RuntimeException("Ошибка в логике работы - дублирующие идентификаторы: " + keyField);
        }

        try (GraphQLQueryResourceProvider resourceProvider = new GraphQLQueryResourceProvider((com.infomaximum.platform.sdk.component.Component) component)) {
            value.prepare(resourceProvider);
            return resourceProvider.getResources();
        }
    }


    @Override
    public Serializable execute(String keyFieldRequest, RemoteObject source, ContextRequest context) throws GraphQLExecutorDataFetcherException {
        ConcurrentMap<String, GraphQLQuery> requestQueries = graphQLRequests.get(context.getRequest());
        GraphQLQuery graphQLQuery = requestQueries.get(keyFieldRequest);
        try {
            ContextTransactionRequest contextTransactionRequest = (ContextTransactionRequest) context;
            return graphQLQuery.execute(
                    source,
                    contextTransactionRequest
            );
        } catch (PlatformException e) {
            throw new GraphQLExecutorDataFetcherException(e);
        }
    }

    @Override
    public void requestCompleted(ContextRequest context) {
        graphQLRequests.remove(context.getRequest());
    }
}
