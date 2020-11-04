package com.infomaximum.platform.component.frontend.engine.service.errorhandler;

import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ActionErrorHandler {

    void handlerNotFound(HttpServletResponse response) throws IOException;

    ResponseEntity<byte[]> handlerServiceUnavailable();
}
