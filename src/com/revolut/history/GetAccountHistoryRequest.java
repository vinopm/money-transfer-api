package com.revolut.history;

import com.revolut.account.AccountID;
import com.revolut.account.Accounts;
import com.revolut.rest.GetHttpRequest;
import com.revolut.rest.Request;
import com.revolut.rest.RequestProcessor;
import com.revolut.rest.Response;
import com.revolut.transfer.Transaction;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import static com.revolut.rest.StatusCode.*;

public class GetAccountHistoryRequest implements RequestProcessor {
    private final Accounts accounts;
    private final GetHttpRequest getHttpRequestHandler;

    GetAccountHistoryRequest(Accounts accounts) {
        this.accounts = accounts;
        this.getHttpRequestHandler = new GetHttpRequest(this::handleRequest);
    }

    @Override
    public Response processRequest(Request req) {
        return getHttpRequestHandler.processRequest(req);
    }

    private Response handleRequest(Map<String, String> params) {
        var accountID = params.get("account_id");

        if(accountID == null || accountID.isEmpty())
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

        final Collection<Transaction> transactions;
        try {
            transactions = accounts.transactions(new AccountID(UUID.fromString(accountID)));
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
        } catch (Exception e){
            return new Response() {
                @Override
                public String responseBody() {
                    return e.getMessage();
                }

                @Override
                public int statusCode() {
                    return BAD_REQUEST.getStatusCode();
                }
            };
        }

        return new Response() {
            @Override
            public String responseBody() {
                return Arrays.toString(transactions.toArray());
            }

            @Override
            public int statusCode() {
                return OK.getStatusCode();
            }
        };
    }


}
