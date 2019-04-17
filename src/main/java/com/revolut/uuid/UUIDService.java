package com.revolut.uuid;

import com.revolut.rest.ExternalService;

public class UUIDService {
    public UUIDService(ExternalService externalService, String prefixPath){
        externalService.createEndpoint(prefixPath + "/create", new GetUUID());
    }
}
