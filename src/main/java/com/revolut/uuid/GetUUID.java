package com.revolut.uuid;

import com.revolut.rest.GetHttpRequest;
import com.revolut.rest.Request;
import com.revolut.rest.RequestProcessor;
import com.revolut.rest.Response;
import com.revolut.rest.ResponseIF;

import java.util.Map;
import java.util.UUID;

import static com.revolut.rest.StatusCode.*;

public class GetUUID implements RequestProcessor {
    private final GetHttpRequest getHttpRequestHandler;

    GetUUID(){
        getHttpRequestHandler = new GetHttpRequest(this::generateUUID);
    }

    @Override
    public ResponseIF processRequest(Request s) {
        return getHttpRequestHandler.processRequest(s);
    }

    private ResponseIF generateUUID(Map<String, String> params) {
        if(params.size() > 0){
            return new Response("This endpoint expects 0 parameters.", NOT_ACCEPTABLE_FORMAT);
        }

        final var uuid = UUID.randomUUID();
        final var uuidString = uuid.toString();

        return new Response(uuidString, OK);
    }
}