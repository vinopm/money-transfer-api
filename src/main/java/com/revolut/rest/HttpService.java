package com.revolut.rest;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 * Using com.sun.net.httpserver here for simplicity.
 * TODO change to use ServerSocketChannel instead.
 */
public class HttpService implements ExternalService {

    private final HttpServer httpServer;
    private final Map<String, HttpContext> endpoints = new ConcurrentHashMap<>();

    HttpService(int port) throws IOException {
        this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        this.httpServer.setExecutor(Executors.newWorkStealingPool());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> httpServer.stop(0)));
    }

    @Override
    public void start() {
        httpServer.start();
    }

    @Override
    public void stop() {
        httpServer.stop(0);
    }

    @Override
    public void createEndpoint(String path, RequestProcessor processor) {
        final var endPoint = httpServer.createContext(path, new HttpRequest() {
            @Override
            public ResponseIF processRequest(Request req) {
                return processor.processRequest(req);
            }
        });
        endpoints.put(path, endPoint);
    }

    @Override
    public void clearEndpoints() {
        endpoints.values().forEach(httpServer::removeContext);
    }
}