package com.infomaximum.platform.exception;

import com.infomaximum.cluster.exception.ClusterException;
import com.infomaximum.cluster.exception.ExceptionBuilder;

import java.util.HashMap;

public class ClusterExceptionBuilder extends ExceptionBuilder {

    private static final ExceptionFactory EXCEPTION_FACTORY = new GeneralExceptionFactory();

    @Override
    public PlatformException buildTransitRequestException(int node, int componentUniqueId, String rControllerClassName, int methodKey, Exception cause) {
        return EXCEPTION_FACTORY.build(
                "remote_component_transit_request",
                "node: " + node + ", componentUniqueId: " + componentUniqueId + ", rControllerClassName: " + rControllerClassName + ", methodKey: " + methodKey,
                new HashMap<String, Object>() {{
                    put("node", node);
                }},
                cause
        );
    }

    @Override
    public PlatformException buildRemoteComponentUnavailableException(int node, int componentUniqueId, String rControllerClassName, int methodKey, Exception cause) {
        return EXCEPTION_FACTORY.build(
                "remote_component_unavailable",
                "node: " + node + ", componentUniqueId: " + componentUniqueId + ", rControllerClassName: " + rControllerClassName + ", methodKey: " + methodKey,
                new HashMap<String, Object>() {{
                    put("node", node);
                }},
                cause
        );
    }
}
