package com.infomaximum.platform.sdk.graphql.datafetcher;

import com.infomaximum.platform.Platform;
import com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder;
import com.infomaximum.subsystems.exception.SubsystemException;
import com.infomaximum.subsystems.exception.runtime.SubsystemRuntimeException;
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

        SubsystemException extractSubsystemException = extractSubsystemException(exception);
        String code = (extractSubsystemException != null)?extractSubsystemException.getCode():null;
        if (GeneralExceptionBuilder.ACCESS_DENIED_CODE.equals(code)) {
            //ничего в лог не выводим
        } else {
            log.warn(exception.getMessage(), exception);
        }
        return DataFetcherExceptionHandlerResult.newResult().error(error).build();
    }

    private SubsystemException extractSubsystemException(Throwable exception) {
        if (exception instanceof SubsystemRuntimeException) {
            return ((SubsystemRuntimeException) exception).getSubsystemException();
        } else {
            return null;
        }
    }
}
