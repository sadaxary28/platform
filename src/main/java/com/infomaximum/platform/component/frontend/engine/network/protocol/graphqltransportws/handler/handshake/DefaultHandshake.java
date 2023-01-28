package com.infomaximum.platform.component.frontend.engine.network.protocol.graphqltransportws.handler.handshake;

import com.infomaximum.network.struct.HandshakeData;
import com.infomaximum.platform.component.frontend.engine.network.protocol.graphqltransportws.packet.Packet;


public class DefaultHandshake extends Handshake {

    @Override
    public Response handshake(Packet packet) {
        return new Response(null, null);
    }
}
