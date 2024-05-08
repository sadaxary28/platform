package com.infomaximum.platform.component.frontend.engine.controller.prometheus;

import io.prometheus.metrics.exporter.common.PrometheusHttpExchange;
import io.prometheus.metrics.exporter.common.PrometheusHttpRequest;
import io.prometheus.metrics.exporter.common.PrometheusHttpResponse;
import io.prometheus.metrics.exporter.common.PrometheusScrapeHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

public class PrometheusController {

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (PrometheusHttpExchangeImpl prometheusHttpExchange = new PrometheusHttpExchangeImpl(request, response)) {
            PrometheusScrapeHandler prometheusScrapeHandler = new PrometheusScrapeHandler();
            prometheusScrapeHandler.handleRequest(prometheusHttpExchange);
        }
    }

    private record PrometheusHttpExchangeImpl(HttpServletRequest request,
                                              HttpServletResponse response) implements PrometheusHttpExchange {

        @Override
        public PrometheusHttpRequest getRequest() {
            return new PrometheusHttpRequest() {
                @Override
                public String getQueryString() {
                    return request.getQueryString();
                }

                @Override
                public Enumeration<String> getHeaders(String name) {
                    return request.getHeaders(name);
                }

                @Override
                public String getMethod() {
                    return request.getMethod();
                }

                @Override
                public String getRequestPath() {
                    return request.getRequestURI();
                }
            };
        }

        @Override
        public PrometheusHttpResponse getResponse() {
            return new PrometheusHttpResponse() {
                @Override
                public void setHeader(String name, String value) {
                    response.setHeader(name, value);
                }

                @Override
                public OutputStream sendHeadersAndGetBody(int statusCode, int contentLength) throws IOException {
                    return response.getOutputStream();
                }
            };
        }

        @Override
        public void handleException(IOException e) throws IOException {
            throw e;
        }

        @Override
        public void handleException(RuntimeException e) {
            throw e;
        }

        @Override
        public void close() {

        }
    }
}
