package com.infomaximum.platform.exception;

import com.infomaximum.cluster.exception.ExceptionBuilder;
import com.infomaximum.subsystems.exception.ExceptionFactory;
import com.infomaximum.subsystems.exception.GeneralExceptionFactory;
import com.infomaximum.subsystems.exception.SubsystemException;

import java.util.HashMap;

public class ClusterExceptionBuilder extends ExceptionBuilder {

    private static final ExceptionFactory EXCEPTION_FACTORY = new GeneralExceptionFactory();

    @Override
    public SubsystemException buildRemoteComponentUnavailableException(int node, int componentUniqueId, Exception cause) {
        return EXCEPTION_FACTORY.build("remote_component_unavailable", cause, new HashMap<String, Object>() {{
            put("node", node);
            put("component_unique_id", componentUniqueId);
        }});
    }
}
