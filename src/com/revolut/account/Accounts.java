package com.revolut.account;

import com.revolut.transfer.*;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.revolut.rest.StatusCode.*;

public class Accounts {
    private final Map<AccountID, AccountInfo> accounts = new ConcurrentHashMap<>();
    private final AccountsLock accountsLock = new AccountsLock();

    public Accounts(){

    }

    public void createAccount(AccountID accountID){
        accountsLock.lock(accountID);

        try {
            accounts.putIfAbsent(accountID, new AccountInfo());
        }finally {
            accountsLock.unlock(accountID);
        }
    }

    public Collection<Transaction> transactions(AccountID accountID) throws AccountException {
        accountsLock.lock(accountID);

        try{
            var account = accounts.get(accountID);

            if(account == null)
                throw new AccountException("Accounts does not exist", BAD_REQUEST.getStatusCode());

            return account.getAllTransactions();
        }finally {
            accountsLock.lock(accountID);
        }

    }

    public Transaction transfer(TransactionID transactionID, AccountID from, AccountID to, Money amount) throws AccountException {
        accountsLock.lock(from, to);

        try{
            var fromAccountInfo = accounts.get(from);
            var toAccountInfo = accounts.get(to);

            if(fromAccountInfo == null|| toAccountInfo == null){
                throw new AccountException("Accounts does not exist", BAD_REQUEST.getStatusCode());
            }

            if(from.equals(to)) {
                throw new AccountException("From and to accounts are identical.", BAD_REQUEST.getStatusCode());
            }

            var alreadyExecuted = fromAccountInfo.getTransaction(transactionID);

            if(alreadyExecuted != null){
                return alreadyExecuted;
            }

            //check if from account has enough funds for transfer
            var fromAmount = fromAccountInfo.getBalance();
            if(amount.compareTo(fromAmount) > 0){
                throw new AccountException("Account does not have enough funds for transfer.", BAD_REQUEST.getStatusCode());
            }

            //execute the transfer
            var newTransaction = new TransferTransaction(transactionID, from, to, amount);
            fromAccountInfo.addNewTransaction(transactionID, newTransaction);
            fromAccountInfo.minus(amount);
            toAccountInfo.addNewTransaction(transactionID, newTransaction);
            toAccountInfo.add(amount);
            return newTransaction;
        }finally {
            accountsLock.unlock(from, to);
        }
    }

    public void deposit(TransactionID transactionID, AccountID accountID, Money amount) throws AccountException {
        final var zeroMoney = new Money("0.00");
        if(amount.compareTo(zeroMoney) <= 0){
            throw new AccountException("Deposit amount must be greater than 0", BAD_REQUEST.getStatusCode());
        }

        accountsLock.lock(accountID);

        try{
            depositWithoutLocking(transactionID, accountID, amount);
        }
        finally {
            accountsLock.unlock(accountID);
        }
    }

    private void depositWithoutLocking(TransactionID transactionID, AccountID accountID, Money amount) throws AccountException {
        var account = accounts.get(accountID);

        if(account == null)
            throw new AccountException("Account does not exist.");

        var alreadyExecuted = account.getTransaction(transactionID);

        if(alreadyExecuted != null){
            return;
        }

        account.add(amount);

        var newTransaction = new DepositTransaction(transactionID, accountID, amount);
        account.addNewTransaction(transactionID, newTransaction);
    }

    public void withdraw(TransactionID transactionID, AccountID accountID, Money amount) throws AccountException {
        final var zeroMoney = new Money("0.00");
        if(amount.compareTo(zeroMoney) <= 0){
            throw new AccountException("Withdraw amount must be greater than 0", BAD_REQUEST.getStatusCode());
        }

        accountsLock.lock(accountID);

        try{
            withdrawWithoutLocking(transactionID, accountID, amount);
        }
        finally {
            accountsLock.unlock(accountID);
        }
    }

    private void withdrawWithoutLocking(TransactionID transactionID, AccountID accountID, Money amount) throws AccountException {
        AccountInfo account = accounts.get(accountID);

        if(account == null)
            throw new AccountException("Account does not exist.", BAD_REQUEST.getStatusCode());

        if(amount.compareTo(account.getBalance()) > 0)
            throw new AccountException("Account does not have sufficient funds to withdraw.", BAD_REQUEST.getStatusCode());

        account.minus(amount);

        var newTransaction = new WithdrawTransaction(transactionID, accountID, amount);
        account.addNewTransaction(transactionID, newTransaction);
    }

    public void deleteAccount(AccountID accountID) throws AccountException {
        accountsLock.lock(accountID);
        try{
            AccountInfo account = accounts.get(accountID);

            if(account == null)
                throw new AccountException("Account does not exist", BAD_REQUEST.getStatusCode());

            accounts.remove(accountID);
        }finally {
            accountsLock.unlock(accountID);
        }
    }

    Money getBalance(AccountID accountID) throws AccountException {
        accountsLock.lock(accountID);
        try{
            AccountInfo account = accounts.get(accountID);

            if(account == null)
                throw new AccountException("Account does not exist", BAD_REQUEST.getStatusCode());

            return account.getBalance();
        }finally {
            accountsLock.unlock(accountID);
        }
    }

    public class AccountException extends Exception {
        public String msg;
        public int code;
        AccountException(String msg, int code){
            this.msg = msg;
            this.code = code;
        }
        public AccountException(String s) {
            super(s);
            this.msg = s;
            this.code = INTERNAL_SERVER_ERROR.getStatusCode();
        }
    }
}
