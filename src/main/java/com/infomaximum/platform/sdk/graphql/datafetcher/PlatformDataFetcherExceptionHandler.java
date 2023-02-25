package com.infomaximum.platform.sdk.graphql.datafetcher;

import com.infomaximum.cluster.graphql.executor.datafetcher.GDataFetcherExceptionHandler;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.exception.runtime.PlatformRuntimeException;
import com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlatformDataFetcherExceptionHandler extends GDataFetcherExceptionHandler {

    private final static Logger log = LoggerFactory.getLogger(PlatformDataFetcherExceptionHandler.class);

    public PlatformDataFetcherExceptionHandler() {
    }

    public void handlerException(Throwable exception) {
        if (exception instanceof PlatformRuntimeException platformRuntimeException) {
            PlatformException platformException = platformRuntimeException.getPlatformException();
            String code = platformException.getCode();
            if (!GeneralExceptionBuilder.ACCESS_DENIED_CODE.equals(code) && !GeneralExceptionBuilder.INVALID_CREDENTIALS.equals(code)) {
                log.warn(exception.getMessage(), exception);
            }
        }
    }

}
