package com.revolut.history;

import com.revolut.account.AccountID;
import com.revolut.account.Accounts;
import com.revolut.account.Accounts.AccountException;
import com.revolut.rest.GetHttpRequest;
import com.revolut.rest.Request;
import com.revolut.rest.RequestProcessor;
import com.revolut.rest.Response;
import com.revolut.rest.ResponseIF;
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
    public ResponseIF processRequest(Request req) {
        return getHttpRequestHandler.processRequest(req);
    }

    private ResponseIF handleRequest(Map<String, String> params) {
        var accountID = params.get("account_id");

        if(accountID == null || accountID.isEmpty())
            return new Response("Parameters are in invalid format.", NOT_ACCEPTABLE_FORMAT);

        final Collection<Transaction> transactions;
        try {
            transactions = accounts.transactions(new AccountID(UUID.fromString(accountID)));
        } catch (AccountException e) {
            return new Response(e.msg, e.code);
        } catch (Exception e){
            return new Response(e.getMessage(), BAD_REQUEST);
        }

        return new Response(Arrays.toString(transactions.toArray()), OK);
    }
}
