package com.infomaximum.testcomponent.exception;

import com.infomaximum.subsystems.exception.ExceptionFactory;
import com.infomaximum.subsystems.exception.SubsystemException;
import com.infomaximum.subsystems.exception.SubsystemExceptionFactory;
import com.infomaximum.testcomponent.TestComponent;

public class TestExceptionBuilder {

    private static final ExceptionFactory EXCEPTION_FACTORY = new SubsystemExceptionFactory(TestComponent.UUID);

    public final static String CODE_TRUE_ASSET="true_asset";

    public static SubsystemException buildTrueAssetException() {
        return EXCEPTION_FACTORY.build(CODE_TRUE_ASSET);
    }
}
