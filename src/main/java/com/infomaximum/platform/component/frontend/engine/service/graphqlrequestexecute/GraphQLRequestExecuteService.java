package com.infomaximum.platform.component.frontend.engine.service.graphqlrequestexecute;

import com.infomaximum.cluster.graphql.struct.GRequest;
import com.infomaximum.platform.component.frontend.engine.service.graphqlrequestexecute.struct.GraphQLResponse;
import com.infomaximum.platform.exception.GraphQLWrapperPlatformException;
import net.minidev.json.JSONObject;

import java.util.concurrent.CompletableFuture;

public interface GraphQLRequestExecuteService {

    CompletableFuture<GraphQLResponse> execute(GRequest gRequest);

    GraphQLResponse<JSONObject> buildResponse(GraphQLWrapperPlatformException graphQLPlatformException);
}
