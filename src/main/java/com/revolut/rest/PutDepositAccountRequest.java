package com.revolut.rest;

import com.revolut.account.AccountID;
import com.revolut.account.Accounts;
import com.revolut.account.Money;
import com.revolut.transfer.TransactionID;

import java.util.Map;
import java.util.UUID;

import static com.revolut.rest.StatusCode.BAD_REQUEST;
import static com.revolut.rest.StatusCode.OK;

public class PutDepositAccountRequest implements RequestProcessor {
    private final Accounts accounts;
    private final PutHttpRequest putHttpRequestHandler;

    public PutDepositAccountRequest(Accounts accounts) {
        this.accounts = accounts;
        this.putHttpRequestHandler = new PutHttpRequest(this::handleDepositRequest);
    }

    @Override
    public ResponseIF processRequest(Request s) {
        return putHttpRequestHandler.processRequest(s);
    }

    private ResponseIF handleDepositRequest(Map<String, String> params) {
        var transaction_id = params.get("transaction_id");
        var accountID = params.get("account_id");
        var amount = params.get("amount");

        if(transaction_id == null || transaction_id.isEmpty() || accountID == null || accountID.isEmpty() || amount == null || amount.isEmpty()){
            return new Response("Parameter account_id/amount is not provided", BAD_REQUEST.getStatusCode());
        }

        try {
            accounts.deposit(new TransactionID(UUID.fromString(transaction_id)), new AccountID(UUID.fromString(accountID)), Money.parseMoney(amount));
        } catch (Accounts.AccountException e) {
            return new Response(e.msg, e.code);
        }

        return new Response("Success", OK);
    }
}
