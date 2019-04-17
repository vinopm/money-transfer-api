package com.revolut.account;

import com.revolut.account.Accounts.AccountException;
import com.revolut.transfer.TransactionID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    //test if creating same account again overwrites or keeps existing data.
    private final Accounts accounts = new Accounts();

    private AccountID accountID1;
    private AccountID accountID2;
    private AccountID accountID3;

    @BeforeEach
    void setUp() throws AccountException {
        this.accountID1 = new AccountID(UUID.randomUUID());
        this.accountID2 = new AccountID(UUID.randomUUID());
        this.accountID3 = new AccountID(UUID.randomUUID());

        accounts.createAccount(accountID1);
        accounts.createAccount(accountID2);
        accounts.createAccount(accountID3);

        accounts.deposit(generateTransactionID(), accountID1, Money.parseMoney("50.00"));
        accounts.deposit(generateTransactionID(), accountID2, Money.parseMoney("50.00"));
        accounts.deposit(generateTransactionID(), accountID3, Money.parseMoney("50.00"));
    }

    @AfterEach
    void tearDown() throws AccountException {
        accounts.deleteAccount(accountID1);
        accounts.deleteAccount(accountID2);
        accounts.deleteAccount(accountID3);
    }

    @Test
    void negativeDepositTest() throws AccountException {
        var transactionID = generateTransactionID();
        assertEquals(1, accounts.transactions(accountID1).size());
        assertThrows(AccountException.class, () -> accounts.deposit(transactionID, accountID1, Money.parseMoney("-50.00")));
        assertEquals(1, accounts.transactions(accountID1).size());
    }

    @Test
    void negativeWithdrawTest() throws AccountException {
        var transactionID = generateTransactionID();
        assertEquals(1, accounts.transactions(accountID1).size());
        assertThrows(AccountException.class, () -> accounts.withdraw(transactionID, accountID1, Money.parseMoney("-50.00")));
        assertEquals(1, accounts.transactions(accountID1).size());
    }

    @Test
    void insufficientFundsWithdrawTest() throws AccountException {
        var transactionID = generateTransactionID();
        assertEquals(1, accounts.transactions(accountID1).size());
        assertThrows(AccountException.class, () -> accounts.withdraw(transactionID, accountID1, Money.parseMoney("500.00")));
        assertEquals(1, accounts.transactions(accountID1).size());
    }

    @Test
    void simpleWithdrawTest() throws AccountException {
        var transactionID = generateTransactionID();

        var beforeBalance = accounts.getBalance(accountID1);
        accounts.withdraw(transactionID, accountID1, Money.parseMoney("50.00"));
        var afterBalance = accounts.getBalance(accountID1);
        assertEquals(beforeBalance, afterBalance.add(Money.parseMoney("50.00")));
    }

    @Test
    void depositToNonExistentAccount() {
        var accountID = new AccountID(UUID.randomUUID());
        assertThrows(AccountException.class, () -> accounts.deposit(generateTransactionID(), accountID, Money.parseMoney("10.00")));
    }

    @Test
    void simpleDepositTest() throws AccountException {
        var transactionID = generateTransactionID();

        var beforeBalance = accounts.getBalance(accountID1);
        accounts.deposit(transactionID, accountID1, Money.parseMoney("50.00"));
        var afterBalance = accounts.getBalance(accountID1);
        assertEquals(beforeBalance, afterBalance.minus(Money.parseMoney("50.00")));
    }

    @Test
    void createAccountDuplicateTest() throws AccountException {
        var beforeTransactions = accounts.transactions(accountID1);
        var copiedAccountID = copyAccountID(accountID1);
        accounts.createAccount(copiedAccountID);
        var afterTransactions = accounts.transactions(copiedAccountID);

        assertTrue(beforeTransactions.containsAll(afterTransactions));
        assertTrue(afterTransactions.containsAll(beforeTransactions));
    }

    @Test
    void getTransactionsFromNonexistentAccount(){
        var accountID = new AccountID(UUID.randomUUID());
        assertThrows(AccountException.class, () -> accounts.transactions(accountID));
    }

    @Test
    void transferFromNonexistentAccount(){
        var accountID = new AccountID(UUID.randomUUID());
        assertThrows(AccountException.class, () -> accounts.transfer(generateTransactionID(), accountID1, accountID, Money.parseMoney("10.00")));
    }

    @Test
    void transferFromAndToSameAccount(){
        assertThrows(AccountException.class, () -> accounts.transfer(generateTransactionID(), accountID1, accountID1, Money.parseMoney("10.00")));
    }

    private AccountID copyAccountID(AccountID accountID){
        return new AccountID(UUID.fromString(accountID.toString()));
    }

    @Test
    void repeatTransferTransactionTest() throws AccountException {
        var transactionID = generateTransactionID();

        var account1Balance = accounts.getBalance(accountID1);
        var account2Balance = accounts.getBalance(accountID2);

        accounts.transfer(transactionID, accountID1, accountID2, Money.parseMoney("25.00"));
        accounts.transfer(transactionID, accountID1, accountID2, Money.parseMoney("25.00"));

        var account1BalanceAfter = accounts.getBalance(accountID1);
        var account2BalanceAfter = accounts.getBalance(accountID2);

        assertEquals(account1Balance, account1BalanceAfter.add(Money.parseMoney("25.00")));
        assertEquals(account2Balance, account2BalanceAfter.minus(Money.parseMoney("25.00")));
    }

    @Test
    void repeatWithdrawTransactionTest() throws AccountException {
        var transactionID = generateTransactionID();

        var account1Balance = accounts.getBalance(accountID1);

        accounts.withdraw(transactionID, accountID1, Money.parseMoney("25.00"));

        var account1BalanceAfter = accounts.getBalance(accountID1);

        assertEquals(account1Balance, account1BalanceAfter.add(Money.parseMoney("25.00")));
    }

    @Test
    void repeatDepositTransactionTest() throws AccountException {
        var transactionID = generateTransactionID();
        var account1Balance = accounts.getBalance(accountID1);

        accounts.deposit(transactionID, accountID1, Money.parseMoney("25.00"));

        var account1BalanceAfter = accounts.getBalance(accountID1);

        assertEquals(account1Balance, account1BalanceAfter.minus(Money.parseMoney("25.00")));
    }

    @Test
    void tooLargeTransferTest() {
        var transactionID = generateTransactionID();
        assertThrows(AccountException.class, () -> accounts.transfer(transactionID, accountID1, accountID2, Money.parseMoney("500.00")));
    }

    public TransactionID generateTransactionID(){
        var transactionUUID = UUID.randomUUID();
        return new TransactionID(transactionUUID);
    }

    @Test
    void deleteAccountWhichDoesNotExist() {
        var accountID = new AccountID(UUID.randomUUID());
        assertThrows(AccountException.class, () -> accounts.deleteAccount(accountID));
    }

    @Test
    void deleteAccountWhichExists() throws AccountException {
        var accountID = new AccountID(UUID.randomUUID());
        accounts.createAccount(accountID);
        accounts.deleteAccount(accountID);
        assertThrows(AccountException.class, () -> accounts.getBalance(accountID));
    }
}
