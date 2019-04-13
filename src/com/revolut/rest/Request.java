package com.revolut.rest;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;

public interface Request {
    public Headers getRequestHeaders () ;
    public URI getRequestURI () ;
    public String getRequestMethod ();
    public HttpContext getHttpContext ();
    public InputStream getRequestBody () ;
    public String getProtocol ();
    public InetSocketAddress getRemoteAddress ();
}
