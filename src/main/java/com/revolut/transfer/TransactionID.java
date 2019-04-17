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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionID)) return false;
        var that = (TransactionID) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
