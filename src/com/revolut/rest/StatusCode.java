package com.revolut.rest;

public enum StatusCode {
    OK (200),
    BAD_REQUEST (400),
    NOT_FOUND (404),
    METHOD_NOT_ALLOWED (405),
    NOT_ACCEPTABLE_FORMAT (406),
    INTERNAL_SERVER_ERROR (500);

    private final int statusCode;

    StatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
