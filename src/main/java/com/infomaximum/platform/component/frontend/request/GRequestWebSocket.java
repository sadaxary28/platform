package com.infomaximum.platform.component.frontend.request;

import com.infomaximum.cluster.graphql.struct.GRequest;

import javax.servlet.http.Cookie;
import java.io.Serializable;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GRequestWebSocket extends GRequest {

    private final String sessionUuid;

    private final Map<String, String> parameters;
    private final Cookie[] cookies;

    public GRequestWebSocket(Instant instant, RemoteAddress remoteAddress, String query, HashMap<String, Serializable> queryVariables, String sessionUuid, Map<String, String> parameters, Cookie[] cookies) {
        super(instant, remoteAddress, query, queryVariables);

        this.sessionUuid = sessionUuid;
        this.parameters = parameters != null ? parameters : new HashMap<>();
        this.cookies = cookies;
    }

    public String getSessionUuid() {
        return sessionUuid;
    }

    public Map<String, String> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

    public Cookie getCookie(String name) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) return cookie;
            }
        }
        return null;
    }
}
