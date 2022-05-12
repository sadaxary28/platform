package com.infomaximum.platform.component.frontend.engine.service.graphqlrequestexecute.utils;

import com.infomaximum.cluster.graphql.struct.GRequest;
import com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder;
import com.infomaximum.subsystems.exception.SubsystemException;
import com.infomaximum.subsystems.exception.runtime.SubsystemRuntimeException;
import graphql.ExceptionWhileDataFetching;
import graphql.ExecutionResult;
import graphql.GraphQLError;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GraphQLExecutionResultUtils {

    public static String toLog(GRequest gRequest, ExecutionResult executionResult, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        String out = "query: " + gRequest.getQuery().replaceAll(" ", "").replaceAll("\n", "").replaceAll("\r", "");

        String outAccessDenied = getAccessDenied(executionResult, uncaughtExceptionHandler);
        if (outAccessDenied != null) {
            out += ", access_denied: [ " + outAccessDenied + "]";
        }

        return out;
    }


    //Формируем пути по которым произошол access_denied
    private static String getAccessDenied(ExecutionResult executionResult, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        if (executionResult.getErrors() == null || executionResult.getErrors().isEmpty()) {
            return null;
        }

        Map<String, Integer> errors = new HashMap<>();
        for (GraphQLError graphQLError : executionResult.getErrors()) {
            if (!(graphQLError instanceof ExceptionWhileDataFetching)) {
                continue;
            }

            ExceptionWhileDataFetching exceptionWhileDataFetching = (ExceptionWhileDataFetching) graphQLError;
            Throwable exception = exceptionWhileDataFetching.getException();
            if (!(exception instanceof SubsystemRuntimeException)) {
                uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), exception);
                return null;
            }
            SubsystemRuntimeException subsystemRuntimeException = (SubsystemRuntimeException) exception;
            SubsystemException subsystemException = subsystemRuntimeException.getSubsystemException();

            if (subsystemException.getCode().equals(GeneralExceptionBuilder.ACCESS_DENIED_CODE)) {
                String path = "/" + graphQLError.getPath().stream()
                        .filter(o -> (o instanceof String)).map(o -> (String) o)
                        .collect(Collectors.joining("/"));

                errors.compute(path, (s, integer) -> (integer == null) ? 1 : integer + 1);
            }
        }
        if (errors.isEmpty()) {
            return null;
        }

        return errors.entrySet().stream().map(entry -> entry.getKey() + " (" + entry.getValue() + ")").collect(Collectors.joining(", "));
    }
}
