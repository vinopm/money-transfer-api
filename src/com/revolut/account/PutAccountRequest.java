package com.revolut.account;

import com.revolut.rest.PutHttpRequest;
import com.revolut.rest.Request;
import com.revolut.rest.RequestProcessor;
import com.revolut.rest.Response;

import java.util.Map;
import java.util.UUID;

import static com.revolut.rest.HttpRequest.BAD_REQUEST;
import static com.revolut.rest.HttpRequest.OK;

public class PutAccountRequest implements RequestProcessor {
    private final Accounts accounts;
    private final PutHttpRequest putHttpRequestHandler;

    public PutAccountRequest(Accounts accounts) {
        this.accounts = accounts;
        this.putHttpRequestHandler = new PutHttpRequest(this::handleCreationRequest);
    }

    @Override
    public Response processRequest(Request s) {
        return putHttpRequestHandler.processRequest(s);
    }

    private Response handleCreationRequest(Map<String, String> requestParams) {
        String accountID = requestParams.get("account_id");

        if(accountID == null || accountID.isEmpty()){
            return new Response() {
                @Override
                public String responseBody() {
                    return "Parameter account_id is not provided";
                }

                @Override
                public int statusCode() {
                    return BAD_REQUEST;
                }
            };
        }

        accounts.createAccount(new AccountID(UUID.fromString(accountID)));

        return new Response() {
            @Override
            public String responseBody() {
                return "Success.";
            }

            @Override
            public int statusCode() {
                return OK;
            }
        };
    }
}
