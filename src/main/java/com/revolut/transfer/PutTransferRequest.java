package com.revolut.transfer;

import com.revolut.account.AccountID;
import com.revolut.account.Accounts;
import com.revolut.account.Accounts.AccountException;
import com.revolut.account.Money;
import com.revolut.rest.PutHttpRequest;
import com.revolut.rest.Request;
import com.revolut.rest.RequestProcessor;
import com.revolut.rest.Response;
import com.revolut.rest.ResponseIF;

import java.util.Map;
import java.util.UUID;

import static com.revolut.rest.StatusCode.*;

class PutTransferRequest implements RequestProcessor {

    private final PutHttpRequest putHttpRequestHandler;
    private final Accounts accounts;

    PutTransferRequest(Accounts accounts){
        this.putHttpRequestHandler = new PutHttpRequest(this::handleCreationRequest);
        this.accounts = accounts;
    }

    @Override
    public ResponseIF processRequest(Request s) {
        return putHttpRequestHandler.processRequest(s);
    }

    private ResponseIF handleCreationRequest(Map<String, String> requestParams) {
        var transactionID = requestParams.get("transaction_id");
        var fromAccountID = requestParams.get("from");
        var toAccountID = requestParams.get("to");
        var amount = requestParams.get("amount");

        if(transactionID == null || fromAccountID == null
                || toAccountID == null || amount == null
                || transactionID.isEmpty() || fromAccountID.isEmpty()
                || toAccountID.isEmpty() || amount.isEmpty())
            return new Response("Parameters are in invalid format.", NOT_ACCEPTABLE_FORMAT);

        final Transaction transaction;
        try {
            transaction = accounts.transfer(new TransactionID(UUID.fromString(transactionID)), new AccountID(UUID.fromString(fromAccountID)), new AccountID(UUID.fromString(toAccountID)), Money.parseMoney(amount));
        } catch (AccountException e) {
            return new Response(e.msg, e.code);
        } catch (IllegalArgumentException e){
            return new Response(e.getMessage(), NOT_ACCEPTABLE_FORMAT);
        }

        return new Response(transaction.toString(), OK);
    }
}
