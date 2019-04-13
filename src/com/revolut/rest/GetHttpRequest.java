package com.revolut.rest;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GetHttpRequest extends HttpRequest {
    private final Function<Map<String, String>, Response> processRequest;

    public GetHttpRequest(Function<Map<String, String>, Response> processRequest){
        this.processRequest = processRequest;
    }

    @Override
    public Response processRequest(Request request) {
        try{
            final var params = parseRawInput(request);
            final var paramsMap = extractParams(params);
            return processRequest.apply(paramsMap);
        } catch (RequestException e) {
            return new Response() {
                @Override
                public String responseBody() {
                    return e.s;
                }

                @Override
                public int statusCode() {
                    return e.statusCode;
                }
            };
        }
    }

    private String parseRawInput(Request request) throws RequestException {
        if(request == null)
            throw new RequestException("Request is null", BAD_REQUEST);

        var method = request.getRequestMethod();
        if(!method.equals(GET_METHOD))
            throw new RequestException("Method " + method + " not allowed.", METHOD_NOT_ALLOWED);

        var params = request.getRequestURI().getRawQuery();
        if(params == null){
            throw new RequestException("The request parameters are empty.", BAD_REQUEST);
        }
        params = java.net.URLDecoder.decode(params, StandardCharsets.UTF_8);

        return params;
    }

    private Map<String, String> extractParams(String input) throws RequestException {
        var list = Arrays.stream(input.split("&"))
                .map(p -> p.split("="))
                .collect(Collectors.toList());

        var map = list.stream()
                .filter(s -> s.length == 2)
                .collect(Collectors.toMap(x -> x[0], x -> x[1]));

        var map1 = list.stream()
                .filter(s -> s.length == 1)
                .collect(Collectors.toMap(x -> x[0], x -> ""));

        var invalidParams = list.stream()
                .filter(s -> s.length > 2)
                .count();

        if (invalidParams > 0){
            throw new RequestException("Parameters of request are in invalid format.", NOT_ACCEPTABLE_FORMAT);
        }

        map.putAll(map1);
        return map;
    }

    private class RequestException extends Exception {
        String s;
        int statusCode;
        RequestException(String s, int code) {
            super(code + " " + s);
            this.s = s;
            this.statusCode = code;
        }
    }
}