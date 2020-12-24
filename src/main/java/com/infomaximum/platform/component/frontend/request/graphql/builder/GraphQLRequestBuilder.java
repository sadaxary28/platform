package com.infomaximum.platform.component.frontend.request.graphql.builder;

import com.infomaximum.platform.component.frontend.engine.uploadfile.FrontendMultipartSource;
import com.infomaximum.platform.component.frontend.request.graphql.GraphQLRequest;
import com.infomaximum.subsystems.exception.SubsystemException;

import javax.servlet.http.HttpServletRequest;

public interface GraphQLRequestBuilder {

    GraphQLRequest build(HttpServletRequest request) throws SubsystemException;

    abstract class Builder {

        public abstract GraphQLRequestBuilder build(FrontendMultipartSource frontendMultipartSource);

    }

}
