package com.revolut.account;

import java.util.UUID;

public class AccountID implements Comparable<AccountID>{
    private final UUID id;
    public AccountID(UUID id){
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountID)) return false;
        var accountID = (AccountID) o;
        return id.equals(accountID.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }


    @Override
    public int compareTo(AccountID o) {
        return id.compareTo(o.id);
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
