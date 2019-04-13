package com.revolut.account;

import com.revolut.transaction.Transaction;
import com.revolut.transaction.TransactionID;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.revolut.rest.HttpRequest.BAD_REQUEST;

public class Accounts {
    private final Map<AccountID, AccountInfo> accounts = new ConcurrentHashMap<>();
    private final AccountsLock accountsLock = new AccountsLock();

    public Accounts(){

    }

    public void createAccount(AccountID accountID){
        accountsLock.lock(accountID);

        try {
            accounts.put(accountID, new AccountInfo());
        }finally {
            accountsLock.unlock(accountID);
        }
    }

    Collection<Transaction> transactions(AccountID accountID) throws AccountException {
        accountsLock.lock(accountID);

        try{
            var account = accounts.get(accountID);

            if(account == null)
                throw new AccountException("Accounts does not exist");

            return account.getAllTransactions();
        }finally {
            accountsLock.lock(accountID);
        }

    }

    Transaction transfer(TransactionID transactionID, AccountID from, AccountID to, float amount) throws AccountException {
        accountsLock.lock(from, to);

        try{
            var fromAccountInfo = accounts.get(from);
            var toAccountInfo = accounts.get(to);

            if(fromAccountInfo == null|| toAccountInfo == null){
                throw new AccountException("Accounts does not exist", BAD_REQUEST);
            }

            if(from.equals(to)) {
                throw new AccountException("From and to accounts are identical.", BAD_REQUEST);
            }

            var alreadyExecuted = fromAccountInfo.getTransaction(transactionID);

            if(alreadyExecuted != null){
                return alreadyExecuted;
            }

            //execute the transfer
            var newTransaction = new Transaction(transactionID, from, to, amount);
            fromAccountInfo.addNewTransaction(transactionID, newTransaction);
            fromAccountInfo.getBalance().minus(amount);
            toAccountInfo.addNewTransaction(transactionID, newTransaction);
            toAccountInfo.getBalance().add(amount);
            return newTransaction;
        }finally {
            accountsLock.unlock(from, to);
        }
    }

    class AccountException extends Exception {
        String msg;
        int code;
        AccountException(String msg, int code){
            this.msg = msg;
            this.code = code;
        }
        public AccountException(String s) {
            super(s);
        }
    }
}
