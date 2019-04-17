package com.revolut.transfer;

import com.revolut.account.AccountID;
import com.revolut.account.Money;

import java.util.StringJoiner;

public class WithdrawTransaction implements Transaction {
    private final TransactionID transactionID;
    private final AccountID accountID;
    private final Money amount;
    private final long ts;

    public WithdrawTransaction(TransactionID transactionID, AccountID accountID, Money amount) {
        this.transactionID = transactionID;
        this.accountID = accountID;
        this.amount = amount;
        this.ts = System.currentTimeMillis();
    }

    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof WithdrawTransaction)) return false;
        var that = (WithdrawTransaction) o;
        return transactionID.equals(that.transactionID);
    }

    public int hashCode(){
        return transactionID.hashCode();
    }

    public String toString(){
        return new StringJoiner(", ", "withdraw: " + "{", "}")
                .add("transaction=" + transactionID)
                .add("account=" + accountID)
                .add("amount=" + amount)
                .add("ts=" + ts)
                .toString();
    }
}
