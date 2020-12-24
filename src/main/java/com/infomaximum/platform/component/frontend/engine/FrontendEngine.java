package com.infomaximum.platform.component.frontend.engine;

import com.infomaximum.cluster.graphql.GraphQLEngine;
import com.infomaximum.cluster.graphql.executor.subscription.GraphQLSubscribeEngine;
import com.infomaximum.network.Network;
import com.infomaximum.network.builder.BuilderNetwork;
import com.infomaximum.network.exception.NetworkException;
import com.infomaximum.platform.Platform;
import com.infomaximum.platform.component.frontend.engine.authorize.RequestAuthorize;
import com.infomaximum.platform.component.frontend.engine.service.graphqlrequestexecute.GraphQLRequestExecuteService;
import com.infomaximum.platform.component.frontend.engine.uploadfile.FrontendMultipartSource;
import com.infomaximum.platform.sdk.component.Component;

public class FrontendEngine implements AutoCloseable {

    private final Builder builder;

    private final Platform platform;
    private final Component component;

    private final GraphQLEngine graphQLEngine;
    private final GraphQLSubscribeEngine graphQLSubscribeEngine;

    private final RequestAuthorize.Builder requestAuthorizeBuilder;
    private final FrontendMultipartSource frontendMiltipartSource;

    private GraphQLRequestExecuteService graphQLRequestExecuteService;

    private Network network;

    private FrontendEngine(Builder builder) {
        this.builder = builder;

        this.platform = builder.platform;
        this.component = builder.component;

        this.graphQLEngine = builder.platform.getGraphQLEngine();
        this.graphQLSubscribeEngine = graphQLEngine.buildSubscribeEngine();

        this.requestAuthorizeBuilder = builder.requestAuthorizeBuilder;

        this.frontendMiltipartSource = new FrontendMultipartSource(builder.component);
    }

    public void start() throws NetworkException {
        graphQLRequestExecuteService = new GraphQLRequestExecuteService(
                component,
                platform.getQueryPool(),
                graphQLEngine, graphQLSubscribeEngine,
                requestAuthorizeBuilder
        );

        network = builder.builderNetwork.build();
    }

    public Network getNetwork() {
        return network;
    }

    public FrontendMultipartSource getFrontendMiltipartSource() {
        return frontendMiltipartSource;
    }

    public GraphQLRequestExecuteService getGraphQLRequestExecuteService() {
        return graphQLRequestExecuteService;
    }

    public GraphQLSubscribeEngine getGraphQLSubscribeEngine() {
        return graphQLSubscribeEngine;
    }

    @Override
    public void close() {
        if (network != null) {
            network.close();
        }
    }

    public static class Builder {

        private final Platform platform;
        private final Component component;

        private BuilderNetwork builderNetwork;
        private RequestAuthorize.Builder requestAuthorizeBuilder;

        public Builder(Platform platform, Component component) {
            this.platform = platform;
            this.component = component;
        }

        public Builder withBuilderNetwork(BuilderNetwork builderNetwork) {
            this.builderNetwork = builderNetwork;
            return this;
        }

        public Builder withRequestAuthorizeBuilder(RequestAuthorize.Builder requestAuthorizeBuilder) {
            this.requestAuthorizeBuilder = requestAuthorizeBuilder;
            return this;
        }

        public FrontendEngine build() {
            return new FrontendEngine(this);
        }
    }
}
