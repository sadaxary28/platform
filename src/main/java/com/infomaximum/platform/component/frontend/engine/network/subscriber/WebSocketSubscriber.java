package com.infomaximum.platform.component.frontend.engine.network.subscriber;

import com.infomaximum.network.packet.IPacket;
import com.infomaximum.network.session.TransportSession;
import com.infomaximum.platform.component.frontend.engine.service.graphqlrequestexecute.GraphQLRequestExecuteService;
import com.infomaximum.platform.component.frontend.engine.service.graphqlrequestexecute.struct.GraphQLResponse;
import graphql.ExecutionResult;
import net.minidev.json.JSONObject;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

public abstract class WebSocketSubscriber implements Subscriber {

    private final static Logger log = LoggerFactory.getLogger(WebSocketSubscriber.class);

    protected final Serializable packetId;
    protected final TransportSession transportSession;

    protected final CompletableFuture<IPacket> firstResponseCompletableFuture;

    private Subscription subscription;

    public WebSocketSubscriber(Serializable packetId, TransportSession transportSession) {
        this.packetId = packetId;
        this.transportSession = transportSession;

        this.firstResponseCompletableFuture = new CompletableFuture<>();
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(Object nextExecutionResult) {
        GraphQLResponse nextGraphQLResponse =
                GraphQLRequestExecuteService.buildResponse((ExecutionResult) nextExecutionResult);

        IPacket responsePacket = buildPacket(nextGraphQLResponse);

        if (firstResponseCompletableFuture.isDone()) {
                 try {
                transportSession.send(responsePacket);
            } catch (Throwable e) {
                log.error("Exception", e);
                subscription.cancel();
            }
        } else {
            firstResponseCompletableFuture.complete(responsePacket);
        }

        subscription.request(1);
    }

    public abstract IPacket buildPacket(GraphQLResponse<JSONObject> nextGraphQLResponse);

    @Override
    public void onError(Throwable throwable) {
        subscription.cancel();
        if (firstResponseCompletableFuture.isDone()) {
            firstResponseCompletableFuture.completeExceptionally(throwable);
        }
        log.debug("OnError", throwable);
    }

    @Override
    public void onComplete() {
    }

    public CompletableFuture<IPacket> getFirstResponseCompletableFuture() {
        return firstResponseCompletableFuture;
    }
}
