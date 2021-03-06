package com.revolut.rest;

import java.util.HashMap;
import java.util.Map;

import static com.revolut.rest.StatusCode.*;

public class MockExternalService implements ExternalService {
    private final Map<String, RequestProcessor> mapOfServices = new HashMap<>();
    private volatile boolean running = false;
    @Override
    public void start() {
        running = true;
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public void createEndpoint(String path, RequestProcessor processor) {
        mapOfServices.put(path, processor);
    }

    @Override
    public void clearEndpoints() {
        mapOfServices.clear();
    }

    ResponseIF makeRequest(String path, Request requestParameters){
        if(!running)
            throw new IllegalStateException("Mock service is not running.");

        var processor = mapOfServices.getOrDefault(path, s -> new Response("Invalid method.", NOT_FOUND));
        return processor.processRequest(requestParameters);
    }
}
