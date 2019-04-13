package com.revolut.rest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpServiceTest {
    private HttpService httpService;

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
        httpService = new HttpService(10000);
        httpService.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        httpService.clearEndpoints();
        httpService.stop();
    }

    @Test
    void simpleConnectivityTest() throws IOException, InterruptedException {
        httpService.createEndpoint("/", s -> mockResponse);
        var response = MockHttpClient.send(HttpRequest.newBuilder(URI.create("http://localhost:10000/")).GET().setHeader("testkey", "testvalue").build());
        assertEquals("RESPONSE", response.body());
    }


}
