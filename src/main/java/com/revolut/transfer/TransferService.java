package com.revolut.transfer;

import com.revolut.account.Accounts;
import com.revolut.rest.PutDepositAccountRequest;
import com.revolut.rest.PutWithdrawAccountRequest;
import com.revolut.rest.ExternalService;

public class TransferService {
    public TransferService(Accounts accounts, ExternalService externalService, String prefixPath){
        externalService.createEndpoint(prefixPath + "/create", new PutTransferRequest(accounts));
        externalService.createEndpoint(prefixPath + "/deposit", new PutDepositAccountRequest(accounts));
        externalService.createEndpoint(prefixPath + "/withdraw", new PutWithdrawAccountRequest(accounts));
    }
}