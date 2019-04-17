package com.revolut.uuid;

import com.revolut.rest.GetHttpRequest;
import com.revolut.rest.Request;
import com.revolut.rest.RequestProcessor;
import com.revolut.rest.Response;

import java.util.Map;
import java.util.UUID;

import static com.revolut.rest.StatusCode.*;

public class GetUUID implements RequestProcessor {
    private final GetHttpRequest getHttpRequestHandler;

    GetUUID(){
        getHttpRequestHandler = new GetHttpRequest(this::generateUUID);
    }

    @Override
    public Response processRequest(Request s) {
        return getHttpRequestHandler.processRequest(s);
    }

    private Response generateUUID(Map<String, String> params) {
        if(params.size() > 0){
            return new Response() {
                @Override
                public String responseBody() {
                    return "This endpoint expects 0 parameters.";
                }

                @Override
                public int statusCode() {
                    return NOT_ACCEPTABLE_FORMAT.getStatusCode();
                }
            };
        }

        final var uuid = UUID.randomUUID();
        final var uuidString = uuid.toString();

        return new Response() {
            @Override
            public String responseBody() {
                return uuidString;
            }

            @Override
            public int statusCode() {
                return OK.getStatusCode();
            }
        };
    }
}