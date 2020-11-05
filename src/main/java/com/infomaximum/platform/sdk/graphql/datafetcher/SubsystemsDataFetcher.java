package com.infomaximum.platform.sdk.graphql.datafetcher;

import com.infomaximum.cluster.core.remote.Remotes;
import com.infomaximum.cluster.graphql.executor.component.GraphQLComponentExecutor;
import com.infomaximum.cluster.graphql.executor.subscription.GraphQLSubscribeEngineImpl;
import com.infomaximum.cluster.graphql.schema.datafetcher.ComponentDataFetcher;
import com.infomaximum.cluster.graphql.schema.struct.out.RGraphQLObjectTypeField;
import com.infomaximum.cluster.graphql.struct.GRequest;
import com.infomaximum.platform.component.frontend.authcontext.UnauthorizedContext;
import com.infomaximum.platform.component.frontend.context.impl.ContextTransactionRequestImpl;
import com.infomaximum.platform.component.frontend.utils.GRequestUtils;
import com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder;
import com.infomaximum.platform.sdk.graphql.fieldconfiguration.struct.FieldConfiguration;
import com.infomaximum.platform.utils.ExceptionUtils;
import com.infomaximum.subsystems.exception.SubsystemException;
import com.infomaximum.subsystems.exception.runtime.SubsystemRuntimeException;
import graphql.schema.DataFetchingEnvironment;

/**
 * Created by kris on 11.01.17.
 */
public class SubsystemsDataFetcher extends ComponentDataFetcher {

    public SubsystemsDataFetcher(Remotes remotes, GraphQLComponentExecutor sdkGraphQLItemExecutor, GraphQLSubscribeEngineImpl subscribeEngine, String graphQLTypeName, RGraphQLObjectTypeField rTypeGraphQLField) {
        super(remotes, sdkGraphQLItemExecutor, subscribeEngine, graphQLTypeName, rTypeGraphQLField);
    }

    @Override
    public Object get(DataFetchingEnvironment environment) {
        ContextTransactionRequestImpl context = environment.getContext();

        UnauthorizedContext authContext = context.getSource().getAuthContext();

        //Проверяем, возможно поле требуется авторизации, тогда надо проверить, что права совпадают
        FieldConfiguration fieldConfiguration = (FieldConfiguration) rTypeGraphQLField.configuration;
        boolean isAccess = false;
        for (Class<? extends UnauthorizedContext> typeAuthContext : fieldConfiguration.typeAuthContexts) {
            if (typeAuthContext.isAssignableFrom(authContext.getClass())) {
                isAccess = true;
                break;
            }
        }

        if (!isAccess) {
            SubsystemException invalidCredentialException = GeneralExceptionBuilder.buildInvalidCredentialsException(rTypeGraphQLField.type, rTypeGraphQLField.name);
            throw new SubsystemRuntimeException(getExceptionDetails(context)  + ". " + invalidCredentialException.toString(),
                    invalidCredentialException);
        }

        try {
            return execute(environment);
        } catch (Throwable t) {
            throw ExceptionUtils.coercionRuntimeException(t);
        }
    }

    private String getExceptionDetails(ContextTransactionRequestImpl context) {
        GRequest gRequest = context.getRequest();
        return "Request (" + GRequestUtils.getHashRequest(gRequest) + ")";
    }
}
