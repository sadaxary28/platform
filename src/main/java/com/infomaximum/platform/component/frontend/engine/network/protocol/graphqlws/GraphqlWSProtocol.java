package com.infomaximum.platform.component.frontend.engine.network.protocol.graphqlws;

import com.infomaximum.network.protocol.PacketHandler;
import com.infomaximum.network.protocol.Protocol;
import com.infomaximum.network.transport.Transport;
import com.infomaximum.platform.component.frontend.engine.network.protocol.graphqlws.handler.handshake.Handshake;
import com.infomaximum.platform.component.frontend.engine.network.protocol.graphqlws.session.GraphqlWSTransportSession;

import java.lang.reflect.InvocationTargetException;

public class GraphqlWSProtocol extends Protocol {

    public final Handshake handshake;
    public final PacketHandler packetHandler;

    public GraphqlWSProtocol(Handshake handshake, PacketHandler packetHandler, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        super(uncaughtExceptionHandler);
        this.handshake = handshake;
        this.packetHandler = packetHandler;
    }

    @Override
    public String getName() {
        return "graphql-ws";
    }

    @Override
    public GraphqlWSTransportSession onConnect(Transport transport, Object channel) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, Exception {

        GraphqlWSTransportSession transportSession = new GraphqlWSTransportSession(this, transport, channel);

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
