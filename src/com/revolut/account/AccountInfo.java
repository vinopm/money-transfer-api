package com.revolut.account;

import com.revolut.transfer.Transaction;
import com.revolut.transfer.TransactionID;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AccountInfo {
    private float balance;
    private final Map<TransactionID, Transaction> transactionsMap;

    AccountInfo(float balance, Map<TransactionID, Transaction> transactionMap){
        this.balance = balance;
        this.transactionsMap = transactionMap;
    }

    AccountInfo(){
        this.transactionsMap = new HashMap<>();
    }

    void add(float amount){
        this.balance += amount;
    }

    void minus(float amount){
        this.balance -= amount;
    }

    float getBalance(){
        return balance;
    }

    boolean hasTransaction(TransactionID transactionID){
        return transactionsMap.containsKey(transactionID);
    }

    Transaction getTransaction(TransactionID transactionID){
        return transactionsMap.get(transactionID);
    }

    Collection<Transaction> getAllTransactions(){
        return transactionsMap.values();
    }

    void addNewTransaction(TransactionID id, Transaction transaction){
        transactionsMap.put(id, transaction);
    }
}
