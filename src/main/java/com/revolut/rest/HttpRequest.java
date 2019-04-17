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
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.revolut.rest.StatusCode.NOT_ACCEPTABLE_FORMAT;

abstract class HttpRequest implements HttpHandler, RequestProcessor {
    static final String POST_METHOD = "POST";
    static final String GET_METHOD = "GET";
    static final String PUT_METHOD = "PUT";
    static final String DELETE_METHOD = "DELETE";

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

    Map<String, String> extractParams(String input) throws RequestException {
        if (input == null || input.isEmpty())
            return new HashMap<>();

        var list = Arrays.stream(input.split("&"))
                .map(p -> p.split("="))
                .collect(Collectors.toList());

        var map = list.stream()
                .filter(s -> s.length == 2)
                .collect(Collectors.toMap(x -> x[0], x -> x[1]));

        var map1 = list.stream()
                .filter(s -> s.length == 1)
                .collect(Collectors.toMap(x -> x[0], x -> ""));

        var invalidParams = list.stream()
                .filter(s -> s.length > 2)
                .count();

        if (invalidParams > 0){
            throw new RequestException("Parameters of request are in invalid format.", NOT_ACCEPTABLE_FORMAT.getStatusCode());
        }

        map.putAll(map1);
        return map;
    }

    class RequestException extends Exception {
        String s;
        int statusCode;
        RequestException(String s, int code) {
            super(code + " " + s);
            this.s = s;
            this.statusCode = code;
        }
    }
}