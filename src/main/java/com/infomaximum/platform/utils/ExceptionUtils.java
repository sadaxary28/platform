package com.infomaximum.platform.utils;

import com.infomaximum.subsystems.exception.SubsystemException;
import com.infomaximum.subsystems.exception.runtime.SubsystemRuntimeException;

public class ExceptionUtils {

    public static RuntimeException coercionRuntimeException(Throwable throwable) {
        if (throwable instanceof SubsystemException) {
            throw new SubsystemRuntimeException((SubsystemException) throwable);
        } else if (throwable instanceof RuntimeException) {
            return (RuntimeException) throwable;
        } else {
            return new RuntimeException(throwable);
        }
    }

}
