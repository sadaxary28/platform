package com.infomaximum.platform.utils;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.exception.runtime.PlatformRuntimeException;

public class ExceptionUtils {

    public static RuntimeException coercionRuntimeException(Throwable throwable) {
        if (throwable instanceof PlatformException) {
            throw new PlatformRuntimeException((PlatformException) throwable);
        } else if (throwable instanceof RuntimeException) {
            return (RuntimeException) throwable;
        } else {
            return new RuntimeException(throwable);
        }
    }

}
