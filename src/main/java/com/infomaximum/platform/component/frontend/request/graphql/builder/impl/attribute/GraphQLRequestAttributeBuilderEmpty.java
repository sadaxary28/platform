package com.infomaximum.platform.component.frontend.request.graphql.builder.impl.attribute;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

public class GraphQLRequestAttributeBuilderEmpty implements GraphQLRequestAttributeBuilder {

    @Override
    public HashMap<String, String[]> build(HttpServletRequest request) {
        return null;
    }
}
