package com.revolut.history;

import com.revolut.account.Accounts;
import com.revolut.rest.ExternalService;

public class HistoryService {
    public HistoryService(Accounts accounts, ExternalService externalService, String prefixPath){
        externalService.createEndpoint(prefixPath, new GetAccountHistoryRequest(accounts));
    }
}
