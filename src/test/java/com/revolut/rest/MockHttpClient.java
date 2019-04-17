package com.revolut.rest;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

class MockHttpClient {
    HttpClient client = HttpClient.newHttpClient();

    static HttpResponse<String> send(HttpRequest request) throws IOException, InterruptedException {
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }
}
