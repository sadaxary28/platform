package com.infomaximum.platform.component.frontend.engine.controller;

import com.infomaximum.platform.component.frontend.engine.FrontendEngine;
import com.infomaximum.platform.component.frontend.engine.controller.http.graphql.GraphQLController;

public class Controllers {

    public final Http HTTP;

    public Controllers(FrontendEngine frontendEngine) {
        this.HTTP = new Http(frontendEngine);
    }

    public class Http {

        public final GraphQLController graphQLController;

        public Http(FrontendEngine frontendEngine) {
            this.graphQLController = new GraphQLController(frontendEngine);
        }

    }
}
