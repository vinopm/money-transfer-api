package com.revolut.transfer;

import com.revolut.account.AccountID;

import java.util.StringJoiner;

public class Transaction {
    private final TransactionID transactionID;
    private final AccountID from;
    private final AccountID to;
    private final float amount;
    private final long ts;
    public Transaction(TransactionID transactionID, AccountID from, AccountID to, float amount){
        this.transactionID = transactionID;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.ts = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;
        var that = (Transaction) o;
        return transactionID.equals(that.transactionID);
    }

    @Override
    public int hashCode() {
        return transactionID.hashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "Transaction: " + "[", "]")
                .add("transactionID=" + transactionID)
                .add("from=" + from)
                .add("to=" + to)
                .add("amount=" + amount)
                .add("ts=" + ts)
                .toString();
    }
}
