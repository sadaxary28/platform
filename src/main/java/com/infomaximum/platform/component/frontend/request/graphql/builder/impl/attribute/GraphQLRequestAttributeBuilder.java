package com.infomaximum.platform.component.frontend.request.graphql.builder.impl.attribute;

import com.infomaximum.subsystems.exception.SubsystemException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

public interface GraphQLRequestAttributeBuilder {

    public HashMap<String, String[]> build(HttpServletRequest request) throws SubsystemException;
}
