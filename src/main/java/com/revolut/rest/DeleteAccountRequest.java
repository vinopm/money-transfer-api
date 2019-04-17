package com.revolut.rest;

import com.revolut.account.AccountID;
import com.revolut.account.Accounts;

import java.util.Map;
import java.util.UUID;

import static com.revolut.rest.StatusCode.BAD_REQUEST;
import static com.revolut.rest.StatusCode.OK;

public class DeleteAccountRequest implements RequestProcessor {
    private final Accounts accounts;
    private final DeleteHttpRequest deleteHttpRequestHandler;

    public DeleteAccountRequest(Accounts accounts) {
        this.accounts = accounts;
        this.deleteHttpRequestHandler = new DeleteHttpRequest(this::handleDeleteRequest);
    }

    @Override
    public Response processRequest(Request s) {
        return deleteHttpRequestHandler.processRequest(s);
    }

    private Response handleDeleteRequest(Map<String, String> requestParams) {
        String accountID = requestParams.get("account_id");

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
            accounts.deleteAccount(new AccountID(UUID.fromString(accountID)));
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

        return new Response() {
            @Override
            public String responseBody() {
                return "Success.";
            }

            @Override
            public int statusCode() {
                return OK.getStatusCode();
            }
        };
    }
}
