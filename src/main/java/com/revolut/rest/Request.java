package com.revolut.rest;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;

public interface Request {
    Headers getRequestHeaders() ;
    URI getRequestURI() ;
    String getRequestMethod();
    HttpContext getHttpContext();
    InputStream getRequestBody() ;
    String getProtocol();
    InetSocketAddress getRemoteAddress();
}
