package com.infomaximum.platform.component.frontend.request.graphql.builder.impl.attribute;

import com.infomaximum.platform.exception.PlatformException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;

public interface GraphQLRequestAttributeBuilder {

    public HashMap<String, String[]> build(HttpServletRequest request) throws PlatformException;
}
