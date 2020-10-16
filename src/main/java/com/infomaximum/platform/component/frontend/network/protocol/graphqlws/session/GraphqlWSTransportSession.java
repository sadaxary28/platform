package com.infomaximum.platform.component.frontend.network.protocol.graphqlws.session;

import com.infomaximum.network.packet.IPacket;
import com.infomaximum.network.session.Session;
import com.infomaximum.network.session.TransportSession;
import com.infomaximum.network.struct.HandshakeData;
import com.infomaximum.network.transport.Transport;
import com.infomaximum.platform.component.frontend.network.protocol.graphqlws.GraphqlWSProtocol;
import com.infomaximum.platform.component.frontend.network.protocol.graphqlws.handler.PacketHandler;
import com.infomaximum.platform.component.frontend.network.protocol.graphqlws.packet.Packet;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class GraphqlWSTransportSession extends TransportSession {

    private final static Logger log = LoggerFactory.getLogger(GraphqlWSTransportSession.class);

    private final Session session;

    //Флаг определяеющий что мы в фазе рукопожатия
    private boolean isPhaseHandshake;

    public GraphqlWSTransportSession(GraphqlWSProtocol protocol, final Transport transport, final Object channel) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        super(protocol, transport, channel);

        this.session = new Session(this);

        //Проверяем наличие фазы рукопожатия
        if (protocol.handshake != null) {
            isPhaseHandshake = true;
        } else {
            isPhaseHandshake = false;
//			network.onHandshake(session);
        }
    }

    @Override
    public void completedPhaseHandshake(HandshakeData handshakeData) {
        isPhaseHandshake = false;
//		session.initHandshakeData(handshakeData);
//		network.onHandshake(session);
    }

    @Override
    public void failPhaseHandshake(IPacket responsePacket) {

    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public void incomingPacket(JSONObject jPacket) {
        try {
            Packet packet = Packet.parse(jPacket);

            getPacketHandler()
                    .exec(session, packet)
                    .thenAccept(responsePacket -> {
                        if (responsePacket != null) {
                            try {
                                send(responsePacket);
                            } catch (Throwable e) {
                                if (!(e instanceof IOException)) {
                                    log.error("Exception", e);
                                }
                                try {
                                    transport.close(channel);
                                } catch (Throwable ignore) {
                                }
                                destroyed();
                            }
                        }
                    });
        } catch (Exception e) {
            log.error("{} Ошибка обработки входящего пакета: ", session, e);
            try {
                transport.close(channel);
            } catch (IOException ignore) {
            }
            destroyed();
        }
    }

    /**
     * Возврощаем обработчика пакетов
     *
     * @return
     */
    protected PacketHandler getPacketHandler() {
        GraphqlWSProtocol graphqlWSProtocol = (GraphqlWSProtocol) protocol;
        if (isPhaseHandshake) {
            return graphqlWSProtocol.handshake;
        } else {
            return graphqlWSProtocol.packetHandler;
        }
    }
}
