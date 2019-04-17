package com.revolut.rest;

import com.revolut.account.AccountID;
import com.revolut.account.Accounts;

import java.util.Map;
import java.util.UUID;

import static com.revolut.rest.StatusCode.BAD_REQUEST;
import static com.revolut.rest.StatusCode.OK;

public class GetAccountBalance implements RequestProcessor {
    private final Accounts accounts;
    private final GetHttpRequest getHttpRequestHandler;

    public GetAccountBalance(Accounts accounts) {
        this.accounts = accounts;
        this.getHttpRequestHandler = new GetHttpRequest(this::handleGetBalanceRequest);
    }

    @Override
    public Response processRequest(Request s) {
        return getHttpRequestHandler.processRequest(s);
    }

    private Response handleGetBalanceRequest(Map<String, String> requestParams) {
        var accountID = requestParams.get("account_id");

        if(accountID == null || accountID.isEmpty()){
            return new Response() {
                @Override
                public String responseBody() {
                    return "Parameter account_id is not provided";
                }

                @Override
                public int statusCode() {
                    return BAD_REQUEST.getStatusCode();
                }
            };
        }

        try {
            var balance = accounts.getBalance(new AccountID(UUID.fromString(accountID)));

            return new Response() {
                @Override
                public String responseBody() {
                    return balance.toString();
                }

                @Override
                public int statusCode() {
                    return OK.getStatusCode();
                }
            };

        } catch (Accounts.AccountException e) {
            return new Response() {
                @Override
                public String responseBody() {
                    return e.msg;
                }

                @Override
                public int statusCode() {
                    return e.code;
                }
            };
        }
    }
}
