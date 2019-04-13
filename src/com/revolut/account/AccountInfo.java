package com.revolut.account;

import com.revolut.transaction.Transaction;
import com.revolut.transaction.TransactionID;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AccountInfo {
    private final AccountBalance balance;
    private final Map<TransactionID, Transaction> transactionsMap;

    AccountInfo(AccountBalance balance, Map<TransactionID, Transaction> transactionMap){
        this.balance = balance;
        this.transactionsMap = transactionMap;
    }

    AccountInfo(){
        this.balance = new AccountBalance();
        this.transactionsMap = new HashMap<>();
    }

    AccountBalance getBalance(){
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
