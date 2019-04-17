package com.revolut.transfer;

import com.revolut.account.AccountID;
import com.revolut.account.Money;

import java.util.StringJoiner;

public class TransferTransaction implements Transaction {
    private final TransactionID transactionID;
    private final AccountID from;
    private final AccountID to;
    private final Money amount;
    private final long ts;

    public TransferTransaction(TransactionID transactionID, AccountID from, AccountID to, Money amount){
        this.transactionID = transactionID;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.ts = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransferTransaction)) return false;
        var that = (TransferTransaction) o;
        return transactionID.equals(that.transactionID);
    }

    @Override
    public int hashCode() {
        return transactionID.hashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "transfer: " + "{", "}")
                .add("transaction=" + transactionID)
                .add("from=" + from)
                .add("to=" + to)
                .add("amount=" + amount)
                .add("ts=" + ts)
                .toString();
    }
}
