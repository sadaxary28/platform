package com.infomaximum.platform.component.frontend.engine.service.graphqlrequestexecute;

import com.infomaximum.cluster.graphql.GraphQLEngine;
import com.infomaximum.cluster.graphql.executor.GraphQLExecutorPrepareImpl;
import com.infomaximum.cluster.graphql.executor.subscription.GraphQLSubscribeEngine;
import com.infomaximum.cluster.graphql.schema.GraphQLSchemaType;
import com.infomaximum.cluster.graphql.schema.scalartype.GraphQLTypeScalar;
import com.infomaximum.cluster.graphql.struct.GRequest;
import com.infomaximum.platform.component.frontend.authcontext.UnauthorizedContext;
import com.infomaximum.platform.component.frontend.context.impl.ContextTransactionRequestImpl;
import com.infomaximum.platform.component.frontend.context.source.impl.SourceGRequestAuthImpl;
import com.infomaximum.platform.component.frontend.engine.authorize.RequestAuthorize;
import com.infomaximum.platform.component.frontend.engine.graphql.PrepareGraphQLDocument;
import com.infomaximum.platform.component.frontend.engine.service.graphqlrequestexecute.struct.GraphQLResponse;
import com.infomaximum.platform.component.frontend.engine.service.graphqlrequestexecute.utils.GraphQLExecutionResultUtils;
import com.infomaximum.platform.exception.GraphQLWrapperPlatformException;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.exception.runtime.PlatformRuntimeException;
import com.infomaximum.platform.querypool.*;
import com.infomaximum.platform.sdk.component.Component;
import com.infomaximum.platform.sdk.context.ContextUtils;
import com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder;
import com.infomaximum.platform.sdk.graphql.out.GOutputFile;
import com.infomaximum.platform.utils.ExceptionUtils;
import graphql.*;
import graphql.execution.ExecutionId;
import graphql.execution.NonNullableValueCoercedAsNullException;
import graphql.execution.reactive.CompletionStageMappingPublisher;
import graphql.language.SourceLocation;
import graphql.schema.CoercingParseValueException;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class GraphQLRequestExecuteService {

    private final static Logger log = LoggerFactory.getLogger(GraphQLRequestExecuteService.class);

    public final static String JSON_PROP_DATA = "data";
    public final static String JSON_PROP_ERROR = "error";

    private final Component frontendComponent;
    private final QueryPool queryPool;
    private final GraphQLEngine graphQLEngine;
    private final GraphQLExecutorPrepareImpl graphQLExecutorPrepare;
    private final RequestAuthorize.Builder requestAuthorizeBuilder;
    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    public GraphQLRequestExecuteService(
            Component frontendComponent, QueryPool queryPool,
            GraphQLEngine graphQLEngine, GraphQLSubscribeEngine graphQLSubscribeEngine,
            RequestAuthorize.Builder requestAuthorizeBuilder,
            Thread.UncaughtExceptionHandler uncaughtExceptionHandler
    ) {
        this.frontendComponent = frontendComponent;
        this.queryPool = queryPool;
        this.graphQLEngine = graphQLEngine;
        this.graphQLExecutorPrepare = (GraphQLExecutorPrepareImpl) graphQLEngine.buildExecutor(frontendComponent, graphQLSubscribeEngine);
        this.requestAuthorizeBuilder = requestAuthorizeBuilder;
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }

    public CompletableFuture<GraphQLResponse> execute(GRequest gRequest) {

        SourceGRequestAuthImpl source = new SourceGRequestAuthImpl(gRequest);
        ContextTransactionRequestImpl context = new ContextTransactionRequestImpl(source);

        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .executionId(ExecutionId.generate())
                .query(gRequest.getQuery())
                .context(context)
                .variables(Collections.unmodifiableMap(gRequest.getQueryVariables()))
                .build();

        //Парсим graphql запрос - собирая ресурсы
        PrepareGraphQLDocument prepareGraphQLDocument;
        try {
            prepareGraphQLDocument = new PrepareGraphQLDocument(graphQLExecutorPrepare, executionInput);
        } catch (Throwable throwable) {
            graphQLExecutorPrepare.requestCompleted(context);

            GraphQLWrapperPlatformException graphQLSubsystemException = coercionGraphQLSubsystemException(throwable);
            return CompletableFuture.completedFuture(buildResponse(graphQLSubsystemException));
        }
        if (prepareGraphQLDocument.getPrepareDocumentRequest().preparsedDocumentEntry.hasErrors()) {//Произошла ошибка парсинга
            graphQLExecutorPrepare.requestCompleted(context);

            GraphQLWrapperPlatformException graphQLSubsystemException = coercionGraphQLSubsystemException(
                    prepareGraphQLDocument.getPrepareDocumentRequest().preparsedDocumentEntry.getErrors().get(0)
            );
            return CompletableFuture.completedFuture(buildResponse(graphQLSubsystemException));
        }

        //Выполняем graphql запрос
        if (prepareGraphQLDocument.isQueryPoolRequest()) {
            return queryPool.execute(
                    frontendComponent,
                    context,
                    new Query<GraphQLResponse>() {

                        private QueryPool.Priority priority;

                        private RequestAuthorize requestAuthorize;

                        @Override
                        public void prepare(ResourceProvider resources) throws PlatformException {
                            requestAuthorize = requestAuthorizeBuilder.build(frontendComponent, gRequest, resources);

                            priority = requestAuthorize.getRequestPriority();

                            //Лочим ресурсы
                            ResourceProviderImpl resourceProvider = (ResourceProviderImpl) resources;
                            prepareGraphQLDocument.getWaitLockResources().forEach((resource, lockType) -> resourceProvider.borrowResource(resource, lockType));
                        }

                        public QueryPool.Priority getPriority() {
                            return priority;
                        }

                        @Override
                        public GraphQLResponse execute(QueryTransaction transaction) throws PlatformException {
                            Instant instantStartExecute = Instant.now();

                            UnauthorizedContext authContext = requestAuthorize.authorize(context);
                            source.setAuthContext(authContext);

                            ExecutionResult executionResult = graphQLExecutorPrepare.execute(prepareGraphQLDocument.getPrepareDocumentRequest());

                            log.debug("Request {}, auth: {}, priority: {}, wait: {}, exec: {}, {}",
                                    ContextUtils.toTrace(context),
                                    authContext,
                                    priority,
                                    instantStartExecute.toEpochMilli() - gRequest.getInstant().toEpochMilli(),
                                    Instant.now().toEpochMilli() - instantStartExecute.toEpochMilli(),
                                    GraphQLExecutionResultUtils.toLog(gRequest, executionResult, uncaughtExceptionHandler)
                            );

                            //Все чистим
                            graphQLExecutorPrepare.requestCompleted(context);

                            if (isExceptionWithIgnoreAccessDenied(executionResult)) {
                                throw coercionGraphQLSubsystemException(executionResult.getErrors().get(0));
                            } else {
                                return buildResponse(executionResult);
                            }
                        }
                    }
            ).exceptionally(throwable -> {
                //Все чистим
                graphQLExecutorPrepare.requestCompleted(context);

                GraphQLWrapperPlatformException graphQLSubsystemException = coercionGraphQLSubsystemException(throwable);
                return buildResponse(graphQLSubsystemException);
            });
        } else {
            try {
                Instant instantStartExecute = Instant.now();

                ExecutionResult executionResult = graphQLExecutorPrepare.execute(prepareGraphQLDocument.getPrepareDocumentRequest());

                log.debug("Request {}, auth: {}, priority: null, wait: {}, exec: {}, query: {}",
                        ContextUtils.toTrace(context),
                        UnauthorizedContext.TO_STRING,
                        instantStartExecute.toEpochMilli() - gRequest.getInstant().toEpochMilli(),
                        Instant.now().toEpochMilli() - instantStartExecute.toEpochMilli(),
                        gRequest.getQuery().replaceAll(" ", "").replaceAll("\n", "").replaceAll("\r", "")
                );

                if (!executionResult.getErrors().isEmpty()) {
                    throw coercionGraphQLSubsystemException(executionResult.getErrors().get(0));
                } else {
                    return CompletableFuture.completedFuture(buildResponse(executionResult));
                }
            } catch (Throwable throwable) {
                GraphQLWrapperPlatformException graphQLSubsystemException = coercionGraphQLSubsystemException(throwable);
                return CompletableFuture.completedFuture(buildResponse(graphQLSubsystemException));
            } finally {
                graphQLExecutorPrepare.requestCompleted(context);
            }
        }
    }

    private boolean isExceptionWithIgnoreAccessDenied(ExecutionResult executionResult) {
        if (executionResult.getErrors().isEmpty()) return false;
        if (executionResult.getData() == null) {
            return true;//Хак. Необходимо более глубокое иследование - это надо для подписок - когда не удалось выполнить подписку, иначе агент не узнает о ошибке подписки
        }
        for (GraphQLError graphQLError : executionResult.getErrors()) {
            if (!(graphQLError instanceof ExceptionWhileDataFetching)) return true;
            ExceptionWhileDataFetching exceptionWhileDataFetching = (ExceptionWhileDataFetching) graphQLError;
            Throwable throwable = exceptionWhileDataFetching.getException();

            PlatformException subsystemException = null;
            if (throwable instanceof PlatformException) {
                subsystemException = (PlatformException) throwable;
            } else if (throwable instanceof PlatformRuntimeException) {
                subsystemException = ((PlatformRuntimeException) throwable).getPlatformException();
            }
            if (subsystemException == null) return true;
            if (!subsystemException.getCode().equals(GeneralExceptionBuilder.ACCESS_DENIED_CODE)) return true;
        }
        return false;
    }

    public static GraphQLWrapperPlatformException coercionGraphQLSubsystemException(Throwable throwable) {
        if (throwable instanceof GraphQLWrapperPlatformException) {
            return (GraphQLWrapperPlatformException) throwable;
        } else if (throwable instanceof PlatformException) {
            return new GraphQLWrapperPlatformException((PlatformException) throwable);
        } else if (throwable instanceof PlatformRuntimeException) {
            return new GraphQLWrapperPlatformException(((PlatformRuntimeException) throwable).getPlatformException());
        } else if (throwable instanceof AssertException
                || throwable instanceof CoercingParseValueException
                || throwable instanceof NonNullableValueCoercedAsNullException
        ) {
            List<SourceLocation> sourceLocations = (throwable instanceof GraphQLError) ? ((GraphQLError) throwable).getLocations() : null;
            return new GraphQLWrapperPlatformException(
                    GeneralExceptionBuilder.buildGraphQLValidationException(throwable.getMessage()),
                    sourceLocations
            );
        } else {
            throw ExceptionUtils.coercionRuntimeException(throwable);
        }
    }

    public GraphQLResponse<JSONObject> buildResponse(GraphQLWrapperPlatformException graphQLSubsystemException) {
        PlatformException e = graphQLSubsystemException.getPlatformException();
        List<SourceLocation> sourceLocations = graphQLSubsystemException.getSourceLocations();

        JSONObject error = new JSONObject();

        if (e.getComponentUuid() != null) {
            error.put("subsystem_uuid", e.getComponentUuid());
        }

        error.put("code", e.getCode());

        if (e.getParameters() != null && !e.getParameters().isEmpty()) {
            GraphQLSchemaType graphQLSchemaType = graphQLEngine.getGraphQLSchemaType();

            JSONObject outParameters = new JSONObject();
            for (Map.Entry<String, Object> entry : e.getParameters().entrySet()) {
                outParameters.put(entry.getKey(), buildJSONParameter(graphQLSchemaType, entry.getValue()));
            }
            error.put("parameters", outParameters);
        }

        if (e.getComment() != null) {
            error.put("message", e.getComment());
        }

        if (sourceLocations != null) {
            JSONArray locations = new JSONArray();
            for (SourceLocation sourceLocation : sourceLocations) {
                locations.add(new JSONObject()
                        .appendField("line", sourceLocation.getLine())
                        .appendField("column", sourceLocation.getColumn())
                );
            }
            error.put("source_location", locations);
        }

        return new GraphQLResponse<>(error, true);
    }

    private static GraphQLWrapperPlatformException coercionGraphQLSubsystemException(GraphQLError graphQLError) {
        ErrorClassification errorType = graphQLError.getErrorType();

        PlatformException subsystemException;
        if (errorType == ErrorType.InvalidSyntax) {
            subsystemException = GeneralExceptionBuilder.buildGraphQLInvalidSyntaxException();
        } else if (errorType == ErrorType.ValidationError) {
            subsystemException = GeneralExceptionBuilder.buildGraphQLValidationException();
        } else if (errorType == ErrorType.DataFetchingException) {
            ExceptionWhileDataFetching exceptionWhileDataFetching = (ExceptionWhileDataFetching) graphQLError;
            Throwable dataFetchingThrowable = exceptionWhileDataFetching.getException();
            if (dataFetchingThrowable instanceof PlatformRuntimeException) {
                subsystemException = ((PlatformRuntimeException) dataFetchingThrowable).getPlatformException();
            } else if (dataFetchingThrowable instanceof PlatformException) {
                subsystemException = (PlatformException) dataFetchingThrowable;
            } else {
                throw ExceptionUtils.coercionRuntimeException(dataFetchingThrowable);
            }
        } else {
            throw new RuntimeException("Not support error type: " + graphQLError.getErrorType());
        }
        return new GraphQLWrapperPlatformException(subsystemException, graphQLError.getLocations());
    }

    public static GraphQLResponse buildResponse(ExecutionResult executionResult) {
        GOutputFile gOutputFile = findOutputFile(executionResult.getData());
        if (gOutputFile != null) {
            return new GraphQLResponse(gOutputFile, false);
        } else {
            if (executionResult.getData() instanceof CompletionStageMappingPublisher) {
                return new GraphQLResponse(executionResult.getData(), false);
            } else {
                return new GraphQLResponse(new JSONObject(executionResult.getData()), false);
            }
        }
    }

    private static GOutputFile findOutputFile(Object response) {
        if (response instanceof Map) {
            for (Object oEntry : ((Map) response).entrySet()) {
                Map.Entry<String, Object> entry = (Map.Entry<String, Object>) oEntry;
                GOutputFile iFindOutputFile = findOutputFile(entry.getValue());
                if (iFindOutputFile != null) return iFindOutputFile;
            }
        } else if (response instanceof GOutputFile) {
            return (GOutputFile) response;
        }
        return null;
    }

    private static Object buildJSONParameter(GraphQLSchemaType graphQLSchemaType, Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Map) {
            JSONObject outObject = new JSONObject();
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) value).entrySet()) {
                outObject.put(entry.getKey(), buildJSONParameter(graphQLSchemaType, entry.getValue()));
            }
            return outObject;
        } else if (value instanceof Collection) {
            JSONArray outArray = new JSONArray();
            for (Object item : (Collection) value) {
                outArray.add(buildJSONParameter(graphQLSchemaType, item));
            }
            return outArray;
        } else if (value instanceof Enum) {
            return ((Enum) value).name();
        } else {
            GraphQLTypeScalar graphQLTypeScalar = graphQLSchemaType.getTypeScalarByClass(value.getClass());
            if (graphQLTypeScalar == null) {
                throw new RuntimeException("Not support type object(not scalar): " + value);
            }
            return graphQLTypeScalar.getGraphQLScalarType().getCoercing().serialize(value);
        }
    }
}
