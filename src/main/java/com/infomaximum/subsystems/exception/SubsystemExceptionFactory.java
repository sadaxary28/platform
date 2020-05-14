package com.infomaximum.subsystems.exception;

import java.util.Map;

public class SubsystemExceptionFactory extends ExceptionFactory {

    private final String subsystemUUID;

    public SubsystemExceptionFactory(String subsystemUUID) {
        this.subsystemUUID = subsystemUUID;
    }

    public String getSubsystemUUID() {
        return subsystemUUID;
    }

    @Override
    public SubsystemException build(String code, String comment, Map<String, Object> parameters, Throwable cause) {
        if (cause != null) {
            return new SubsystemException(subsystemUUID, code, comment, parameters, cause);
        }
        return new SubsystemException(subsystemUUID, code, comment, parameters);
    }
}
