package com.infomaximum.platform.component.frontend.engine.network.protocol.graphqltransportws.handler.graphql;

import com.infomaximum.cluster.graphql.executor.struct.GSubscriptionPublisher;
import com.infomaximum.cluster.graphql.struct.GRequest;
import com.infomaximum.network.packet.IPacket;
import com.infomaximum.network.protocol.PacketHandler;
import com.infomaximum.network.session.Session;
import com.infomaximum.network.session.SessionImpl;
import com.infomaximum.network.struct.RemoteAddress;
import com.infomaximum.network.struct.UpgradeRequest;
import com.infomaximum.platform.component.frontend.engine.network.protocol.GraphQLSubscriber;
import com.infomaximum.platform.component.frontend.engine.network.protocol.graphqltransportws.packet.Packet;
import com.infomaximum.platform.component.frontend.engine.network.protocol.graphqltransportws.packet.TypePacket;
import com.infomaximum.platform.component.frontend.engine.network.protocol.graphqltransportws.subscriber.WebSocketGraphQLTransportWSSubscriber;
import com.infomaximum.platform.component.frontend.engine.provider.ProviderGraphQLRequestExecuteService;
import com.infomaximum.platform.component.frontend.engine.service.graphqlrequestexecute.struct.GraphQLResponse;
import com.infomaximum.platform.component.frontend.request.GRequestWebSocket;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class GraphQLTransportWSHandler implements PacketHandler {

    private final ProviderGraphQLRequestExecuteService providerGraphQLRequestExecuteService;

    private final GraphQLSubscriber graphQLSubscriber;

    public GraphQLTransportWSHandler(ProviderGraphQLRequestExecuteService providerGraphQLRequestExecuteService) {
        this.providerGraphQLRequestExecuteService = providerGraphQLRequestExecuteService;
        this.graphQLSubscriber = new GraphQLSubscriber();
    }

    @Override
    public CompletableFuture<IPacket[]> exec(Session session, IPacket packet) {
        Packet requestPacket = (Packet) packet;
        TypePacket typePacket = requestPacket.type;

        if (typePacket == TypePacket.GQL_PING) {
            return CompletableFuture.completedFuture(new IPacket[]{ new Packet(TypePacket.GQL_PONG) });
        } else if (typePacket == TypePacket.GQL_SUBSCRIBE) {
            return execGraphQL(session, requestPacket);
        } else if (typePacket == TypePacket.GQL_COMPLETE) {
            graphQLSubscriber.unSubscriber(session, requestPacket.id);
            return CompletableFuture.completedFuture(new IPacket[0]);
        } else {
            return CompletableFuture.completedFuture(new IPacket[]{
                    new Packet(requestPacket.id, TypePacket.GQL_ERROR)
            });
        }
    }

    private CompletableFuture<IPacket[]> execGraphQL(Session session, Packet requestPacket) {
        JSONObject payload = (JSONObject) requestPacket.payload;

        String query = payload.getAsString("query");
        if (query == null || query.trim().isEmpty()) {
            return CompletableFuture.completedFuture(new IPacket[]{
                    new Packet(requestPacket.id, TypePacket.GQL_ERROR)
            });
        }

        HashMap<String, Serializable> variables;
        JSONObject jVariables = (JSONObject) payload.get("variables");
        if (jVariables != null) {
            variables = new HashMap<>((Map) jVariables);
        } else {
            variables = new HashMap<>();
        }

        String operationName = payload.getAsString("operation_name");

        UpgradeRequest upgradeRequest = ((SessionImpl) session).getTransportSession().getUpgradeRequest();

        Map<String, String> parameters = upgradeRequest.getParameters();

        RemoteAddress remoteAddress = ((SessionImpl) session).getTransportSession().buildRemoteAddress();
        String xTraceId = ((SessionImpl) session).getTransportSession().getXTraceId();

        GRequestWebSocket gRequest = new GRequestWebSocket(
                Instant.now(),
                new GRequest.RemoteAddress(remoteAddress.getRawRemoteAddress(), remoteAddress.getEndRemoteAddress()),
                query, variables, operationName,
                xTraceId,
                session.getUuid(),
                parameters,
                upgradeRequest.getCookies(),
                session.getHandshakeData()
        );

        return providerGraphQLRequestExecuteService.getGraphQLRequestExecuteService()
                .execute(gRequest)
                .thenCompose(graphQLResponse -> buildResponsePacket(graphQLResponse, session, requestPacket));
    }

    private CompletableFuture<IPacket[]> buildResponsePacket(
            GraphQLResponse graphQLResponse,
            Session session, Packet requestPacket
    ) {
        if (graphQLResponse.error) {
            JSONArray jErrors = new JSONArray();
            jErrors.add(graphQLResponse.data);
            return CompletableFuture.completedFuture(new IPacket[]{
                    new Packet(requestPacket.id, TypePacket.GQL_ERROR, jErrors)
            });
        } else {
            Object data = graphQLResponse.data;
            if (data instanceof JSONObject jPayload) {
                return CompletableFuture.completedFuture(new IPacket[]{
                        new Packet(requestPacket.id, TypePacket.GQL_NEXT, jPayload),
                        new Packet(requestPacket.id, TypePacket.GQL_COMPLETE)
                });
            } else if (data instanceof GSubscriptionPublisher subscriptionPublisher) {
                WebSocketGraphQLTransportWSSubscriber websocketSubscriber = new WebSocketGraphQLTransportWSSubscriber(graphQLSubscriber, requestPacket.id, ((SessionImpl) session).getTransportSession());
                subscriptionPublisher.subscribe(websocketSubscriber);

                CompletableFuture<IPacket[]> result = new CompletableFuture<>();
                websocketSubscriber.getFirstResponseCompletableFuture()
                        .thenApply(iPacket -> result.complete(new IPacket[]{ iPacket }));
                return result;
            } else {
                throw new RuntimeException("Not support type out: " + data);
            }
        }
    }
}
