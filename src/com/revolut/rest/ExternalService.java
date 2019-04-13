package com.revolut.rest;

public interface ExternalService {
    void start();
    void stop();
    void createEndpoint(String endpoint, RequestProcessor processor);
    void clearEndpoints();
}