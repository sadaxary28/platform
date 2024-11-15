package com.infomaximum.platform.component.frontend.engine.network.protocol.graphqltransportws;

import com.infomaximum.network.protocol.Protocol;
import com.infomaximum.network.session.TransportSession;
import com.infomaximum.network.struct.UpgradeRequest;
import com.infomaximum.network.transport.Transport;
import com.infomaximum.platform.component.frontend.engine.network.protocol.graphqltransportws.handler.graphql.GraphQLTransportWSHandler;
import com.infomaximum.platform.component.frontend.engine.network.protocol.graphqltransportws.handler.handshake.Handshake;
import com.infomaximum.platform.component.frontend.engine.network.protocol.graphqltransportws.session.GraphqlTransportWSTransportSession;

public class GraphqlTransportWSProtocol extends Protocol {

    public final Handshake handshake;
    public final GraphQLTransportWSHandler packetHandler;

    public GraphqlTransportWSProtocol(Handshake handshake, GraphQLTransportWSHandler packetHandler, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        super(uncaughtExceptionHandler);

        this.handshake = handshake;
        this.packetHandler = packetHandler;
    }

    @Override
    public String getName() {
        return "graphql-transport-ws";
    }

    @Override
    public TransportSession onConnect(Transport transport, Object channel, UpgradeRequest upgradeRequest) {
        GraphqlTransportWSTransportSession transportSession = new GraphqlTransportWSTransportSession(this, transport, channel, upgradeRequest);

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
