package com.revolut.rest;

import com.revolut.rest.ExternalService;
import com.revolut.rest.Request;
import com.revolut.rest.RequestProcessor;
import com.revolut.rest.Response;

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

    public Response makeRequest(String path, Request requestParameters){
        if(!running)
            throw new IllegalStateException("Mock service is not running.");

        var processor = mapOfServices.getOrDefault(path, s -> new Response() {
            @Override
            public String responseBody() {
                return "Invalid method.";
            }

            @Override
            public int statusCode() {
                return NOT_FOUND.getStatusCode();
            }
        });
        return processor.processRequest(requestParameters);
    }
}
