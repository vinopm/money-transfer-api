package com.revolut.rest;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class HttpRequest implements HttpHandler, RequestProcessor {
    static final String POST_METHOD = "POST";
    static final String GET_METHOD = "GET";
    static final String PUT_METHOD = "PUT";
    static final String DELETE_METHOD = "DELETE";

    public static final int OK = 200;
    public static final int BAD_REQUEST = 400;
    public static final int NOT_FOUND = 404;
    public static final int METHOD_NOT_ALLOWED = 405;
    public static final int NOT_ACCEPTABLE_FORMAT = 406;
    public static final int INTERNAL_SERVER_ERROR = 500;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        var response = processRequest(toRequestWrapper(exchange));
        exchange.sendResponseHeaders(response.statusCode(), response.responseBody().length());

        var out = exchange.getResponseBody();
        out.write(response.responseBody().getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();
    }

    private Request toRequestWrapper(HttpExchange exchange){
        return new Request() {
            @Override
            public Headers getRequestHeaders() {
                return exchange.getRequestHeaders();
            }

            @Override
            public URI getRequestURI() {
                return exchange.getRequestURI();
            }

            @Override
            public String getRequestMethod() {
                return exchange.getRequestMethod();
            }

            @Override
            public HttpContext getHttpContext() {
                return exchange.getHttpContext();
            }

            @Override
            public InputStream getRequestBody() {
                return exchange.getRequestBody();
            }

            @Override
            public String getProtocol() {
                return exchange.getProtocol();
            }

            @Override
            public InetSocketAddress getRemoteAddress() {
                return exchange.getRemoteAddress();
            }
        };
    }
}