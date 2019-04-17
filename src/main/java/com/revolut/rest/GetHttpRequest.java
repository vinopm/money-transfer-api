package com.revolut.rest;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;

import static com.revolut.rest.StatusCode.*;

public class GetHttpRequest extends HttpRequest {

    private final Function<Map<String, String>, ResponseIF> processRequest;

    public GetHttpRequest(Function<Map<String, String>, ResponseIF> processRequest){
        this.processRequest = processRequest;
    }

    private String parseRawInput(Request request) throws RequestException {
        if(request == null)
            throw new RequestException("Request is null", BAD_REQUEST.getStatusCode());

        var method = request.getRequestMethod();
        if(!method.equals(GET_METHOD))
            throw new RequestException("Method " + method + " not allowed.", METHOD_NOT_ALLOWED.getStatusCode());

        var params = request.getRequestURI().getRawQuery();
        if(params == null){
            return "";
        }
        params = java.net.URLDecoder.decode(params, StandardCharsets.UTF_8);

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
