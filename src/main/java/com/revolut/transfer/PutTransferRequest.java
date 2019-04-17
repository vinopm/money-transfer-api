package com.revolut.transfer;

import com.revolut.account.AccountID;
import com.revolut.account.Accounts;
import com.revolut.account.Accounts.AccountException;
import com.revolut.account.Money;
import com.revolut.rest.PutHttpRequest;
import com.revolut.rest.Request;
import com.revolut.rest.RequestProcessor;
import com.revolut.rest.Response;

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
    public Response processRequest(Request s) {
        return putHttpRequestHandler.processRequest(s);
    }

    private Response handleCreationRequest(Map<String, String> requestParams) {
        var transactionID = requestParams.get("transaction_id");
        var fromAccountID = requestParams.get("from");
        var toAccountID = requestParams.get("to");
        var amount = requestParams.get("amount");

        if(transactionID == null || fromAccountID == null
                || toAccountID == null || amount == null
                || transactionID.isEmpty() || fromAccountID.isEmpty()
                || toAccountID.isEmpty() || amount.isEmpty())
            return new Response() {
                @Override
                public String responseBody() {
                    return "Parameters are in invalid format.";
                }

                @Override
                public int statusCode() {
                    return NOT_ACCEPTABLE_FORMAT.getStatusCode();
                }
            };
        final Transaction transaction;
        try {
            transaction = accounts.transfer(new TransactionID(UUID.fromString(transactionID)), new AccountID(UUID.fromString(fromAccountID)), new AccountID(UUID.fromString(toAccountID)), Money.parseMoney(amount));
        } catch (AccountException e) {
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
        } catch (IllegalArgumentException e){
            return new Response() {
                @Override
                public String responseBody() {
                    return e.getMessage();
                }

                @Override
                public int statusCode() {
                    return NOT_ACCEPTABLE_FORMAT.getStatusCode();
                }
            };
        }

        return new Response() {
            @Override
            public String responseBody() {
                return transaction.toString();
            }

            @Override
            public int statusCode() {
                return OK.getStatusCode();
            }
        };
    }
}
