package com.infomaximum.platform.component.frontend.engine.network.protocol.graphqlws;

import com.infomaximum.network.protocol.Protocol;
import com.infomaximum.network.protocol.ProtocolBuilder;
import com.infomaximum.platform.component.frontend.engine.network.protocol.graphqlws.handler.PacketHandler;
import com.infomaximum.platform.component.frontend.engine.network.protocol.graphqlws.handler.handshake.Handshake;

public class GraphqlWSProtocolBuilder extends ProtocolBuilder {

    private final PacketHandler packetHandler;

    public GraphqlWSProtocolBuilder(PacketHandler packetHandler) {
        this.packetHandler=packetHandler;
    }

    @Override
    public Protocol build(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        return new GraphqlWSProtocol(new Handshake(), packetHandler);
    }
}
