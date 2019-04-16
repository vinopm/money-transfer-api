package com.revolut.account;

import com.revolut.rest.DeleteAccountRequest;
import com.revolut.rest.ExternalService;
import com.revolut.rest.PutNewAccountRequest;

public class AccountService {
    public AccountService(Accounts accounts, ExternalService externalService, String prefixPath){
        externalService.createEndpoint(prefixPath + "/create", new PutNewAccountRequest(accounts));
        externalService.createEndpoint(prefixPath + "/delete", new DeleteAccountRequest(accounts));
    }
}
