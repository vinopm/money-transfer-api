package com.revolut.transfer;

import java.util.UUID;

public class TransactionID {
    private final UUID id;
    public TransactionID(UUID id){
        this.id = id;
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
