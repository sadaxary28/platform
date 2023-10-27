package com.infomaximum.platform.exception;

import com.infomaximum.cluster.exception.ExceptionBuilder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.UUID;

public class ClusterExceptionBuilder implements ExceptionBuilder {

    private static final ExceptionFactory EXCEPTION_FACTORY = new GeneralExceptionFactory();

    @Override
    public Class getTypeException() {
        return PlatformException.class;
    }

    @Override
    public Exception buildTransitRequestException(UUID nodeRuntimeId, int componentId, String rControllerClassName, int methodKey, Exception cause) {
        return EXCEPTION_FACTORY.build(
                "remote_component_transit_request",
                "node: " + nodeRuntimeId + ", componentId: " + componentId + ", rControllerClassName: " + rControllerClassName + ", methodKey: " + methodKey + ", cause: " + toStringCause(cause),
                new HashMap<String, Object>() {{
                    put("nodeRuntimeId", nodeRuntimeId);
                }}
        );
    }

    @Override
    public Exception buildRemoteComponentUnavailableException(UUID nodeRuntimeId, int componentId, String rControllerClassName, int methodKey, Exception cause) {
        return EXCEPTION_FACTORY.build(
                "remote_component_unavailable",
                "node: " + nodeRuntimeId + ", componentId: " + componentId + ", rControllerClassName: " + rControllerClassName + ", methodKey: " + methodKey + ", cause: " + toStringCause(cause),
                new HashMap<String, Object>() {{
                    put("nodeRuntimeId", nodeRuntimeId);
                }}
        );
    }

    @Override
    public Exception buildRemoteComponentNotFoundException(UUID nodeRuntimeId, int componentId) {
        return EXCEPTION_FACTORY.build(
                "remote_component_not_found",
                "node: " + nodeRuntimeId + ", componentId: " + componentId,
                new HashMap<String, Object>() {{
                    put("nodeRuntimeId", nodeRuntimeId);
                }}
        );
    }

    @Override
    public Exception buildMismatchRemoteApiNotFoundControllerException(UUID nodeRuntimeId, int componentId, String rControllerClassName) {
        return EXCEPTION_FACTORY.build(
                "mismatch_remote_api_not_found_controller",
                "node: " + nodeRuntimeId + ", componentId: " + componentId + ", rControllerClassName: " + rControllerClassName,
                new HashMap<String, Object>() {{
                    put("nodeRuntimeId", nodeRuntimeId);
                }}
        );
    }

    @Override
    public Exception buildMismatchRemoteApiNotFoundMethodException(UUID nodeRuntimeId, int componentId, String rControllerClassName, int methodKey) {
        return EXCEPTION_FACTORY.build(
                "mismatch_remote_api_not_found_method",
                "node: " + nodeRuntimeId + ", componentId: " + componentId + ", rControllerClassName: " + rControllerClassName + ", methodKey: " + methodKey,
                new HashMap<String, Object>() {{
                    put("nodeRuntimeId", nodeRuntimeId);
                }}
        );
    }

    private static String toStringCause(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
