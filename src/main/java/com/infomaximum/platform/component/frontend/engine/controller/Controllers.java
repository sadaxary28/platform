package com.infomaximum.platform.component.frontend.engine.controller;

import com.infomaximum.platform.component.frontend.engine.FrontendEngine;
import com.infomaximum.platform.component.frontend.engine.controller.prometheus.PrometheusController;

public class Controllers {

    public final Http http;
    public final Websocket websocket;
    public final PrometheusController prometheus;

    public Controllers(FrontendEngine frontendEngine) {
        this.http = new Http(frontendEngine);
        this.websocket = new Websocket(frontendEngine);
        this.prometheus = new PrometheusController();
    }

    public class Http {

        public final com.infomaximum.platform.component.frontend.engine.controller.http.graphql.GraphQLController graphQLController;

        public Http(FrontendEngine frontendEngine) {
            this.graphQLController = new com.infomaximum.platform.component.frontend.engine.controller.http.graphql.GraphQLController(frontendEngine);
        }

    }

    public class Websocket {

        public final com.infomaximum.platform.component.frontend.engine.controller.websocket.graphql.GraphQLController graphQLController;

        public Websocket(FrontendEngine frontendEngine) {
            this.graphQLController = new com.infomaximum.platform.component.frontend.engine.controller.websocket.graphql.GraphQLController(frontendEngine);
        }

    }
}
