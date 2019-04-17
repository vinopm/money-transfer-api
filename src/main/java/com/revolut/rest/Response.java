package com.revolut.rest;

public class Response implements ResponseIF {
    private final String responseBody;
    private final int statusCode;

    public Response(String responseBody, StatusCode statusCode){
        this.responseBody = responseBody;
        this.statusCode = statusCode.getStatusCode();
    }

    public Response(String responseBody, int statusCode){
        this.responseBody = responseBody;
        this.statusCode = statusCode;
    }

    @Override
    public String responseBody() {
        return this.responseBody;
    }

    @Override
    public int statusCode() {
        return this.statusCode;
    }
}
