package com.infomaximum.testcomponent.exception;

import com.infomaximum.platform.exception.ExceptionFactory;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.exception.PlatformExceptionFactory;
import com.infomaximum.testcomponent.TestComponent;

public class TestExceptionBuilder {

    private static final ExceptionFactory EXCEPTION_FACTORY = new PlatformExceptionFactory(TestComponent.UUID);

    public final static String CODE_TRUE_ASSET="true_asset";

    public static PlatformException buildTrueAssetException() {
        return EXCEPTION_FACTORY.build(CODE_TRUE_ASSET);
    }
}
