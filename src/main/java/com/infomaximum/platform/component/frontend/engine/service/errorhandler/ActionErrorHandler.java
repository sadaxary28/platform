package com.infomaximum.platform.component.frontend.engine.service.errorhandler;

import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Response;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface ActionErrorHandler {

    void handlerNotFound(Response response) throws IOException;

    ResponseEntity<byte[]> handlerServiceUnavailable();
}
