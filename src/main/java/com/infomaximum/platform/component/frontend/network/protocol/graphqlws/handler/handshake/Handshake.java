package com.infomaximum.platform.component.frontend.network.protocol.graphqlws.handler.handshake;

import com.infomaximum.network.packet.IPacket;
import com.infomaximum.network.protocol.standard.packet.ResponsePacket;
import com.infomaximum.network.session.Session;
import com.infomaximum.network.struct.HandshakeData;
import com.infomaximum.platform.component.frontend.network.protocol.graphqlws.handler.PacketHandler;
import com.infomaximum.platform.component.frontend.network.protocol.graphqlws.packet.Packet;
import com.infomaximum.platform.component.frontend.network.protocol.graphqlws.packet.TypePacket;

import java.util.concurrent.CompletableFuture;

/**
 * Created by kris on 01.09.16.
 */
public class Handshake implements PacketHandler {

    public Handshake() {
    }

    public void onPhaseHandshake(Session session) {

    }

    /**
     * Завершаем фазу рукопожатия
     *
     * @param session
     */
    public void completedPhaseHandshake(Session session, HandshakeData handshakeData) {
        session.getTransportSession().completedPhaseHandshake(handshakeData);
    }

    /**
     * Ошибка фазы рукопожатия - разрываем соединение
     *
     * @param session
     */
    public void failPhaseHandshake(Session session, ResponsePacket responsePacket) {
        session.getTransportSession().failPhaseHandshake(responsePacket);
    }

    @Override
    public CompletableFuture<IPacket> exec(Session session, Packet packet) {
        Packet responsePacket;
        if (packet.type == TypePacket.GQL_CONNECTION_INIT) {
            responsePacket = new Packet(packet.id, TypePacket.GQL_CONNECTION_ACK);
            completedPhaseHandshake(session, null);
        } else {
            responsePacket = new Packet(packet.id, TypePacket.GQL_CONNECTION_ERROR);
        }
        return CompletableFuture.completedFuture(responsePacket);
    }
}
