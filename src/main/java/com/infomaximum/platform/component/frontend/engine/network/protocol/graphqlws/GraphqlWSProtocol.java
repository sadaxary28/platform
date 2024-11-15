package com.infomaximum.platform.component.frontend.engine.network.protocol.graphqlws;

import com.infomaximum.network.protocol.Protocol;
import com.infomaximum.network.struct.UpgradeRequest;
import com.infomaximum.network.transport.Transport;
import com.infomaximum.platform.component.frontend.engine.network.protocol.graphqlws.handler.graphql.GraphQLWSHandler;
import com.infomaximum.platform.component.frontend.engine.network.protocol.graphqlws.handler.handshake.Handshake;
import com.infomaximum.platform.component.frontend.engine.network.protocol.graphqlws.session.GraphqlWSTransportSession;

public class GraphqlWSProtocol extends Protocol {

    public final Handshake handshake;
    public final GraphQLWSHandler packetHandler;

    public GraphqlWSProtocol(Handshake handshake, GraphQLWSHandler packetHandler, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        super(uncaughtExceptionHandler);
        this.handshake = handshake;
        this.packetHandler = packetHandler;
    }

    @Override
    public String getName() {
        return "graphql-ws";
    }

    @Override
    public GraphqlWSTransportSession onConnect(Transport transport, Object channel, UpgradeRequest upgradeRequest) {

        GraphqlWSTransportSession transportSession = new GraphqlWSTransportSession(this, transport, channel, upgradeRequest);

        //Начинаем фазу рукопожатия
        if (handshake == null) {
            //Обработчика рукопожатий отсутсвует - сразу считаем что оно закончилось
//            onHandshake(transportSession.getSession());
        } else {
            handshake.onPhaseHandshake(transportSession.getSession());
        }

        return transportSession;
    }
}
