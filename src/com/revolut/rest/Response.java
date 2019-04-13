package com.revolut.rest;

import com.sun.net.httpserver.Headers;

import java.io.OutputStream;

public interface Response {

    String responseBody();
    int statusCode();
}