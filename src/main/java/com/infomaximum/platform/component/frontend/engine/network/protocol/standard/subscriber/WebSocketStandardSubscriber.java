package com.infomaximum.platform.component.frontend.engine.network.protocol.standard.subscriber;

import com.infomaximum.network.mvc.ResponseEntity;
import com.infomaximum.network.packet.IPacket;
import com.infomaximum.network.protocol.standard.packet.RequestPacket;
import com.infomaximum.network.protocol.standard.packet.ResponsePacket;
import com.infomaximum.network.protocol.standard.session.StandardTransportSession;
import com.infomaximum.platform.component.frontend.engine.network.subscriber.WebSocketSubscriber;
import com.infomaximum.platform.component.frontend.engine.service.graphqlrequestexecute.GraphQLResponse;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Ulitin V. Не решена проблема! Если соединение сбрасывается по инициативе transportSession,
 * то у нас остается живая подписка, до первого нового события!
 */
public class WebSocketStandardSubscriber extends WebSocketSubscriber {

    private final static Logger log = LoggerFactory.getLogger(WebSocketStandardSubscriber.class);

    public WebSocketStandardSubscriber(StandardTransportSession transportSession, RequestPacket requestPacket) {
        super(requestPacket.getId(), transportSession);
    }

    @Override
    public IPacket buildPacket(GraphQLResponse<JSONObject> nextGraphQLResponse) {
        ResponseEntity responseEntity = (nextGraphQLResponse.error)
                ? ResponseEntity.error(nextGraphQLResponse.data)
                : ResponseEntity.success(nextGraphQLResponse.data);

        return new ResponsePacket(
                    (long) packetId,
                    responseEntity.code,
                    responseEntity.data);
    }

}
