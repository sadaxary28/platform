package com.infomaximum.platform.sdk.graphql.datafetcher;

import com.infomaximum.platform.Platform;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.exception.runtime.PlatformRuntimeException;
import com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder;
import graphql.ExceptionWhileDataFetching;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.execution.ExecutionPath;
import graphql.language.SourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlatformDataFetcherExceptionHandler implements DataFetcherExceptionHandler {

    private final static Logger log = LoggerFactory.getLogger(Platform.class);

    @Override
    public DataFetcherExceptionHandlerResult onException(DataFetcherExceptionHandlerParameters handlerParameters) {
        Throwable exception = handlerParameters.getException();
        SourceLocation sourceLocation = handlerParameters.getSourceLocation();
        ExecutionPath path = handlerParameters.getPath();

        ExceptionWhileDataFetching error = new ExceptionWhileDataFetching(path, exception, sourceLocation);

        PlatformException extractSubsystemException = extractSubsystemException(exception);
        String code = (extractSubsystemException != null) ? extractSubsystemException.getCode() : null;
        if (GeneralExceptionBuilder.ACCESS_DENIED_CODE.equals(code) || GeneralExceptionBuilder.INVALID_CREDENTIALS.equals(code)) {
            //ничего в лог не выводим
        } else {
            log.warn(exception.getMessage(), exception);
        }
        return DataFetcherExceptionHandlerResult.newResult().error(error).build();
    }

    private PlatformException extractSubsystemException(Throwable exception) {
        if (exception instanceof PlatformRuntimeException) {
            return ((PlatformRuntimeException) exception).getPlatformException();
        } else {
            return null;
        }
    }
}
