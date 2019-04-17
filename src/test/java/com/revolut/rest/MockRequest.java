package com.revolut.rest;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class MockRequest implements Request {
    private String queryPath;
    private String requestBody;
    private String method;

    private static final int testPort = 65458;

    MockRequest setQueryPath(String queryPath){
        this.queryPath = queryPath;
        return this;
    }

    MockRequest setRequestBody(String requestBody){
        this.requestBody = requestBody;
        return this;
    }

    MockRequest setMethod(String method){
        this.method = method;
        return this;
    }

    @Override
    public Headers getRequestHeaders() {
        throw new UnsupportedOperationException();
    }

    @Override
    public URI getRequestURI() {
        return URI.create("http://localhost:"+ testPort + queryPath);
    }

    @Override
    public String getRequestMethod() {
        return method;
    }

    @Override
    public HttpContext getHttpContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream getRequestBody() {
        return new ByteArrayInputStream(requestBody.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String getProtocol() {
        throw new UnsupportedOperationException();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        throw new UnsupportedOperationException();
    }
}
