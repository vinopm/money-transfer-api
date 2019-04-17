package com.revolut.rest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;

import static com.revolut.rest.StatusCode.*;

public class PutHttpRequest extends HttpRequest {

    private final Function<Map<String, String>, ResponseIF> processRequest;

    public PutHttpRequest(Function<Map<String, String>, ResponseIF> processRequest){
        this.processRequest = processRequest;
    }

    private String parseRawInput(Request request) throws RequestException {
        if(request == null)
            throw new RequestException("Request is null", BAD_REQUEST.getStatusCode());

        var method = request.getRequestMethod();
        if(!method.equals(PUT_METHOD))
            throw new RequestException("Method " + method + " not allowed.", METHOD_NOT_ALLOWED.getStatusCode());

        final String params;
        try {
            params = new String(request.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RequestException("Unable to parse request body parameters.", BAD_REQUEST.getStatusCode());
        }

        if(params.isEmpty()){
            throw new RequestException("The request parameters are empty.", BAD_REQUEST.getStatusCode());
        }

        return params;
    }

    @Override
    public ResponseIF processRequest(Request request) {
        try{
            final var params = parseRawInput(request);
            final var paramsMap = extractParams(params);
            return processRequest.apply(paramsMap);
        } catch (RequestException e) {
            return new Response(e.s, e.statusCode);
        }
    }
}
