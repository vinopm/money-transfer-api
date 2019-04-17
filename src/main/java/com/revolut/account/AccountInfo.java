package com.revolut.account;

import com.revolut.transfer.Transaction;
import com.revolut.transfer.TransactionID;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

class AccountInfo {
    private Money balance = new Money();
    private final Map<TransactionID, Transaction> transactionsMap;

    AccountInfo(){
        this.transactionsMap = new LinkedHashMap<>();
    }

    void add(Money amount){
        balance = this.balance.add(amount);
    }

    void minus(Money amount){
        balance = this.balance.minus(amount);
    }

    Money getBalance(){
        return balance;
    }

    Transaction getTransaction(TransactionID transactionID){
        return transactionsMap.get(transactionID);
    }

    Collection<Transaction> getAllTransactions(){
        return new ArrayList<>(transactionsMap.values());
    }

    void addNewTransaction(TransactionID id, Transaction transaction){
        transactionsMap.put(id, transaction);
    }
}
