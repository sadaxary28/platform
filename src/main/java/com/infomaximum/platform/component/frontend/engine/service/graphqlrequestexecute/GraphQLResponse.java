package com.infomaximum.platform.component.frontend.engine.service.graphqlrequestexecute;

public class GraphQLResponse<T> {

    public final T data;
    public final boolean error;

    public GraphQLResponse(T data, boolean error) {
        this.data = data;
        this.error = error;
    }

}
