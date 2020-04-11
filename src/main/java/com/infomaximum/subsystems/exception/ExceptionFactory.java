package com.infomaximum.subsystems.exception;

import java.util.Map;

public abstract class ExceptionFactory {

    public abstract SubsystemException build(String code, String comment, Map<String, Object> parameters, Throwable cause);

    public SubsystemException build(String code, String comment, Map<String, Object> parameters) {
        return build(code, comment, parameters, null);
    }

    public SubsystemException build(String code, Map<String, Object> parameters) {
        return build(code, null, parameters, null);
    }

    public SubsystemException build(String code, String comment) {
        return build(code, comment, null, null);
    }

    public SubsystemException build(String code, Throwable e) {
        return build(code,  e != null ? e.getMessage() : null, null, e);
    }

    public SubsystemException build(String code, Throwable e, Map<String, Object> parameters) {
        return build(code,  e != null ? e.getMessage() : null, parameters, e);
    }

    public SubsystemException build(String code) {
        return build(code, null, null, null);
    }
}
