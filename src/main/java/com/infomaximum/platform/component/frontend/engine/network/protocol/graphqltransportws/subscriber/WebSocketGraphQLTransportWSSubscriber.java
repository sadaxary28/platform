package com.infomaximum.platform.component.frontend.engine.network.protocol.graphqltransportws.subscriber;

import com.infomaximum.network.packet.IPacket;
import com.infomaximum.network.session.TransportSession;
import com.infomaximum.platform.component.frontend.engine.network.protocol.GraphQLSubscriber;
import com.infomaximum.platform.component.frontend.engine.network.protocol.graphqltransportws.packet.Packet;
import com.infomaximum.platform.component.frontend.engine.network.protocol.graphqltransportws.packet.TypePacket;
import com.infomaximum.platform.component.frontend.engine.network.subscriber.WebSocketSubscriber;
import com.infomaximum.platform.component.frontend.engine.service.graphqlrequestexecute.struct.GraphQLResponse;
import net.minidev.json.JSONObject;

import java.io.Serializable;

public class WebSocketGraphQLTransportWSSubscriber extends WebSocketSubscriber {

    public WebSocketGraphQLTransportWSSubscriber(GraphQLSubscriber graphQLSubscriber, Serializable packetId, TransportSession transportSession) {
        super(graphQLSubscriber, packetId, transportSession);
    }

    @Override
    public IPacket buildPacket(GraphQLResponse<JSONObject> nextGraphQLResponse) {
        return new Packet(
                (String) packetId,
                TypePacket.GQL_NEXT,
                nextGraphQLResponse.data
        );
    }

}
