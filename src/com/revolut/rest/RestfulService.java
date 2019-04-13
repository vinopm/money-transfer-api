package com.revolut.rest;

import com.revolut.account.Accounts;
import com.revolut.account.TransferService;

import java.io.IOException;

public class RestfulService {
    RestfulService(int port) throws IOException {
        Accounts accounts = new Accounts();

        HttpService httpService = new HttpService(port);

        TransferService transferService = new TransferService(accounts, httpService, "/transfer");
        // Additional services to be defined here.
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        new RestfulService(10000);
        Thread.sleep(Long.MAX_VALUE);
    }
}
