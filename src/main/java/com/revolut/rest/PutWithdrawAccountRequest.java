package com.revolut.rest;

import com.revolut.account.AccountID;
import com.revolut.account.Accounts;
import com.revolut.account.Money;
import com.revolut.transfer.TransactionID;

import java.util.Map;
import java.util.UUID;

import static com.revolut.rest.StatusCode.BAD_REQUEST;
import static com.revolut.rest.StatusCode.OK;

public class PutWithdrawAccountRequest implements RequestProcessor {
    private final Accounts accounts;
    private final PutHttpRequest putHttpRequestHandler;

    public PutWithdrawAccountRequest(Accounts accounts) {
        this.accounts = accounts;
        this.putHttpRequestHandler = new PutHttpRequest(this::handleWithdrawRequest);
    }

    @Override
    public ResponseIF processRequest(Request s) {
        return putHttpRequestHandler.processRequest(s);
    }

    private ResponseIF handleWithdrawRequest(Map<String, String> params) {
        var transactionID = params.get("transaction_id");
        var accountID = params.get("account_id");
        var amount = params.get("amount");

        if(transactionID == null || transactionID.isEmpty() || accountID == null || accountID.isEmpty() || amount == null || amount.isEmpty()){
            return new Response("Parameter transaction_id/account_id/amount is not provided", BAD_REQUEST);
        }

        try {
            accounts.withdraw(new TransactionID(UUID.fromString(transactionID)), new AccountID(UUID.fromString(accountID)), Money.parseMoney(amount));
        } catch (Accounts.AccountException e) {
            return new Response(e.msg, e.code);
        }

        return new Response("Success", OK);
    }
}
