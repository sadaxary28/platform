package com.infomaximum.platform.component.frontend.request.graphql.builder;

import com.infomaximum.platform.component.frontend.engine.uploadfile.FrontendMultipartSource;
import com.infomaximum.platform.component.frontend.request.graphql.GraphQLRequest;
import com.infomaximum.platform.exception.PlatformException;
import jakarta.servlet.http.HttpServletRequest;

public interface GraphQLRequestBuilder {

    GraphQLRequest build(HttpServletRequest request) throws PlatformException;

    abstract class Builder {

        public abstract GraphQLRequestBuilder build(FrontendMultipartSource frontendMultipartSource);

    }

}
