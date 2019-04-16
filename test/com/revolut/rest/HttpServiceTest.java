package com.revolut.rest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;

import static com.revolut.rest.StatusCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpServiceTest {
    private HttpService httpService;

    private final int port = 10000;

    final String endpointName = "/endpoint";

    private Response mockResponse = new Response() {
        @Override
        public String responseBody() {
            return "RESPONSE";
        }

        @Override
        public int statusCode() {
            return 200;
        }
    };

    @BeforeEach
    void setUp() throws IOException {
        httpService = new HttpService(port);
        httpService.start();

        httpService.createEndpoint(endpointName, new RequestProcessor() {
            @Override
            public Response processRequest(Request s) {
                String params = URLDecoder.decode(s.getRequestURI().getRawQuery(), StandardCharsets.UTF_8);
                return new Response() {
                    @Override
                    public String responseBody() {
                        return params + " RESPONSE";
                    }

                    @Override
                    public int statusCode() {
                        return OK.getStatusCode();
                    }
                };
            }
        });
    }

    @AfterEach
    void tearDown() throws IOException {
        httpService.clearEndpoints();
        httpService.stop();
    }

    @Test
    void simpleConnectivityTest() throws IOException, InterruptedException {

        final String params = "param1=test123";
        var response = MockHttpClient.send(HttpRequest.newBuilder(URI.create("http://localhost:"+port+endpointName+"?"+params)).GET().setHeader("testkey", "testvalue").build());
        assertEquals(params + " RESPONSE", response.body());
    }

    @Test
    void notFoundTest() throws IOException, InterruptedException {
        final String params = "param1=test123";
        var response = MockHttpClient.send(HttpRequest.newBuilder(URI.create("http://localhost:"+port+"/nonexistent"+"?"+params)).GET().setHeader("testkey", "testvalue").build());
        assertEquals(NOT_FOUND.getStatusCode(), response.statusCode());
    }
}
