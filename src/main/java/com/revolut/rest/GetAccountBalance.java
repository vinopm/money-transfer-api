package com.revolut.rest;

import com.revolut.account.AccountID;
import com.revolut.account.Accounts;
import com.revolut.account.Accounts.AccountException;

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
    public ResponseIF processRequest(Request s) {
        return getHttpRequestHandler.processRequest(s);
    }

    private ResponseIF handleGetBalanceRequest(Map<String, String> requestParams) {
        var accountID = requestParams.get("account_id");

        if(accountID == null || accountID.isEmpty()){
            return new Response("Parameter account_id is not provided", BAD_REQUEST);
        }

        try {
            var balance = accounts.getBalance(new AccountID(UUID.fromString(accountID)));

            return new Response(balance.toString(), OK);

        } catch (AccountException e) {
            return new Response(e.msg, e.code);
        }
    }
}
