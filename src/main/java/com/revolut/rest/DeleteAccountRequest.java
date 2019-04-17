package com.revolut.rest;

import com.revolut.account.AccountID;
import com.revolut.account.Accounts;
import com.revolut.account.Accounts.AccountException;

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
    public ResponseIF processRequest(Request s) {
        return deleteHttpRequestHandler.processRequest(s);
    }

    private ResponseIF handleDeleteRequest(Map<String, String> requestParams) {
        var accountID = requestParams.get("account_id");

        if(accountID == null || accountID.isEmpty()){
            return new Response("Parameter account_id is not provided", BAD_REQUEST);
        }

        try {
            accounts.deleteAccount(new AccountID(UUID.fromString(accountID)));
        } catch (AccountException e) {
            return new Response(e.msg, e.code);
        }

        return new Response("Success.", OK);
    }
}
