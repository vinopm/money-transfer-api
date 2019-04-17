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
    public Response processRequest(Request s) {
        return putHttpRequestHandler.processRequest(s);
    }

    private Response handleDepositRequest(Map<String, String> params) {
        String transaction_id = params.get("transaction_id");
        String accountID = params.get("account_id");
        String amount = params.get("amount");

        if(transaction_id == null || transaction_id.isEmpty() || accountID == null || accountID.isEmpty() || amount == null || amount.isEmpty()){
            return new Response() {
                @Override
                public String responseBody() {
                    return "Parameter account_id/amount is not provided";
                }

                @Override
                public int statusCode() {
                    return BAD_REQUEST.getStatusCode();
                }
            };
        }

        try {
            accounts.deposit(new TransactionID(UUID.fromString(transaction_id)), new AccountID(UUID.fromString(accountID)), Money.parseMoney(amount));
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
                return "Success";
            }

            @Override
            public int statusCode() {
                return OK.getStatusCode();
            }
        };
    }
}
