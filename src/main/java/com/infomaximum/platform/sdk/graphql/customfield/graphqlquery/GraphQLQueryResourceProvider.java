package com.infomaximum.platform.sdk.graphql.customfield.graphqlquery;

import com.infomaximum.platform.sdk.component.Component;
import com.infomaximum.subsystems.querypool.QueryPool;
import com.infomaximum.subsystems.querypool.ResourceProviderImpl;

import java.util.HashMap;

/**
 * Для того, что бы ограничить "не правильное" использование ResourceProvider мы унаследоваись в нужном нам пакете
 */
public class GraphQLQueryResourceProvider extends ResourceProviderImpl {

    GraphQLQueryResourceProvider(Component component) {
        super(component);
    }

    @Override
    protected HashMap<String, QueryPool.LockType> getResources() {
        return super.getResources();
    }

}
