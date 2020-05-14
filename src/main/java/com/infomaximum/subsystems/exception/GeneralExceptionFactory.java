package com.infomaximum.subsystems.exception;

import java.util.Map;

//TODO Ulitin V. Убрать public class
public class GeneralExceptionFactory extends ExceptionFactory {

    @Override
    public SubsystemException build(String code, String comment, Map<String, Object> parameters, Throwable cause) {
        if (cause != null) {
            return new SubsystemException(code, comment, parameters, cause);
        }
        return new SubsystemException(code, comment, parameters);
    }
}
