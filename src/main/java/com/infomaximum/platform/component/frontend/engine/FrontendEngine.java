package com.infomaximum.platform.component.frontend.engine;

import com.infomaximum.cluster.core.remote.controller.clusterfile.RControllerClusterFileImpl;
import com.infomaximum.cluster.core.service.transport.executor.ExecutorTransportImpl;
import com.infomaximum.cluster.graphql.GraphQLEngine;
import com.infomaximum.cluster.graphql.executor.subscription.GraphQLSubscribeEngine;
import com.infomaximum.network.Network;
import com.infomaximum.network.builder.BuilderNetwork;
import com.infomaximum.network.exception.NetworkException;
import com.infomaximum.platform.Platform;
import com.infomaximum.platform.component.frontend.engine.authorize.RequestAuthorize;
import com.infomaximum.platform.component.frontend.engine.service.graphqlrequestexecute.GraphQLRequestExecuteService;
import com.infomaximum.platform.component.frontend.engine.service.statistic.StatisticService;
import com.infomaximum.platform.component.frontend.engine.uploadfile.FrontendMultipartSource;
import com.infomaximum.platform.component.frontend.request.graphql.builder.GraphQLRequestBuilder;
import com.infomaximum.platform.component.frontend.request.graphql.builder.impl.DefaultGraphQLRequestBuilder;
import com.infomaximum.platform.sdk.component.Component;

public class FrontendEngine implements AutoCloseable {

    private final Builder builder;

    private final Platform platform;
    private final Component component;

    private final GraphQLEngine graphQLEngine;
    private final GraphQLSubscribeEngine graphQLSubscribeEngine;

    private final RequestAuthorize.Builder requestAuthorizeBuilder;
    private final FrontendMultipartSource frontendMiltipartSource;

    private final GraphQLRequestBuilder graphQLRequestBuilder;

    private GraphQLRequestExecuteService graphQLRequestExecuteService;

    private Network network;

    private final StatisticService statisticService;

    private FrontendEngine(Builder builder) {
        this.builder = builder;

        this.platform = builder.platform;
        this.component = builder.component;

        this.graphQLEngine = builder.platform.getGraphQLEngine();
        this.graphQLSubscribeEngine = graphQLEngine.buildSubscribeEngine();

        this.requestAuthorizeBuilder = builder.requestAuthorizeBuilder;

        this.frontendMiltipartSource = new FrontendMultipartSource(builder.component);

        this.graphQLRequestBuilder = builder.graphQLRequestBuilder.build(frontendMiltipartSource);

        this.statisticService = builder.statisticService;
    }

    public ExecutorTransportImpl.Builder registerControllers(ExecutorTransportImpl.Builder builder) {
        return builder
                .withRemoteController(
                        platform.getGraphQLEngine().buildRemoteControllerGraphQLSubscribe(component, graphQLSubscribeEngine)
                )
                .withRemoteController(
                        new RControllerClusterFileImpl.Builder(component, frontendMiltipartSource).build()//Обработчик ClusterFiles
                );
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

    public GraphQLRequestBuilder getGraphQLRequestBuilder() {
        return graphQLRequestBuilder;
    }

    public GraphQLRequestExecuteService getGraphQLRequestExecuteService() {
        return graphQLRequestExecuteService;
    }

    public GraphQLSubscribeEngine getGraphQLSubscribeEngine() {
        return graphQLSubscribeEngine;
    }

    public StatisticService getStatisticService() {
        return statisticService;
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

        private GraphQLRequestBuilder.Builder graphQLRequestBuilder = new DefaultGraphQLRequestBuilder.Builder();

        private StatisticService statisticService;

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

        public Builder withGraphQLRequestBuilder(GraphQLRequestBuilder.Builder graphQLRequestBuilder) {
            this.graphQLRequestBuilder = graphQLRequestBuilder;
            return this;
        }

        public Builder withStatisticService(StatisticService statisticService) {
            this.statisticService = statisticService;
            return this;
        }

        public FrontendEngine build() {
            return new FrontendEngine(this);
        }
    }
}
