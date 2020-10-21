package com.infomaximum.platform.component.frontend.engine;

import com.infomaximum.cluster.graphql.GraphQLEngine;
import com.infomaximum.cluster.graphql.executor.subscription.GraphQLSubscribeEngine;
import com.infomaximum.network.Network;
import com.infomaximum.network.builder.BuilderNetwork;
import com.infomaximum.network.exception.NetworkException;
import com.infomaximum.platform.sdk.component.Component;

public class FrontendEngine implements AutoCloseable {

    private final Builder builder;

    private final Component component;

    private final GraphQLEngine graphQLEngine;
    public final GraphQLSubscribeEngine graphQLSubscribeEngine;

    private Network network;

    private FrontendEngine(Builder builder) {
        this.builder = builder;

        this.component = builder.component;

        this.graphQLEngine = builder.graphQLEngine;
        this.graphQLSubscribeEngine = graphQLEngine.buildSubscribeEngine();
    }

    public void start() throws NetworkException {
        network = builder.builderNetwork.build();
    }

    public Network getNetwork() {
        return network;
    }

    @Override
    public void close() {
        if (network != null) {
            network.close();
        }
    }

    public static class Builder {

        private final Component component;

        private GraphQLEngine graphQLEngine;
        private BuilderNetwork builderNetwork;

        public Builder(Component component) {
            this.component = component;
        }

        public Builder withGraphQLEngine(GraphQLEngine graphQLEngine) {
            this.graphQLEngine = graphQLEngine;
            return this;
        }

        public Builder withBuilderNetwork(BuilderNetwork builderNetwork) {
            this.builderNetwork = builderNetwork;
            return this;
        }

        public FrontendEngine build() {
            return new FrontendEngine(this);
        }
    }
}
