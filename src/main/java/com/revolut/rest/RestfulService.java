package com.revolut.rest;

import com.revolut.account.AccountService;
import com.revolut.account.Accounts;
import com.revolut.history.HistoryService;
import com.revolut.transfer.TransferService;
import com.revolut.uuid.UUIDService;

import java.io.IOException;

public class RestfulService {
    RestfulService(int port) throws IOException {
        var accounts = new Accounts();

        var httpService = new HttpService(port);

        // List of all services
        new TransferService(accounts, httpService, "/transfer");
        new AccountService(accounts, httpService, "/account");
        new HistoryService(accounts, httpService, "/history");
        new UUIDService(httpService, "/uuid");
        // Additional services to be defined here.

        httpService.start();

    }
    public static void main(String[] args) throws IOException, InterruptedException {
        if(args.length != 1)
            throw new IllegalArgumentException("Only one argument should be specified for port number.");
        int port = Integer.valueOf(args[0]);

        new RestfulService(port);
    }
}
