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

        accounts.deposit(generateTransactionID(), accountID1, 50);
        accounts.deposit(generateTransactionID(), accountID2, 50);
        accounts.deposit(generateTransactionID(), accountID3, 50);
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
        assertEquals(0, accounts.transactions(accountID1).size());
        assertThrows(AccountException.class, () -> accounts.deposit(transactionID, accountID1, -50.00F));
        assertEquals(0, accounts.transactions(accountID1).size());
    }

    @Test
    void negativeWithdrawTest() throws AccountException {
        var transactionID = generateTransactionID();
        assertEquals(0, accounts.transactions(accountID1).size());
        assertThrows(AccountException.class, () -> accounts.withdraw(transactionID, accountID1, -50.00F));
        assertEquals(0, accounts.transactions(accountID1).size());
    }

    @Test
    void insufficientFundsWithdrawTest() throws AccountException {
        var transactionID = generateTransactionID();
        assertEquals(0, accounts.transactions(accountID1).size());
        assertThrows(AccountException.class, () -> accounts.withdraw(transactionID, accountID1, 500.00F));
        assertEquals(0, accounts.transactions(accountID1).size());
    }

    @Test
    void simpleWithdrawTest() throws AccountException {
        var transactionID = generateTransactionID();

        var beforeBalance = accounts.getBalance(accountID1);
        accounts.withdraw(transactionID, accountID1, 50.00F);
        var afterBalance = accounts.getBalance(accountID1);
        assertEquals(beforeBalance, afterBalance + 50F);
    }

    @Test
    void simpleDepositTest() throws AccountException {
        var transactionID = generateTransactionID();

        var beforeBalance = accounts.getBalance(accountID1);
        accounts.deposit(transactionID, accountID1, 50.00F);
        var afterBalance = accounts.getBalance(accountID1);
        assertEquals(beforeBalance, afterBalance - 50F);
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

    private AccountID copyAccountID(AccountID accountID){
        return new AccountID(UUID.fromString(accountID.toString()));
    }

    @Test
    void repeatTransferTransactionTest() throws AccountException {
        var transactionID = generateTransactionID();

        var account1Balance = accounts.getBalance(accountID1);
        var account2Balance = accounts.getBalance(accountID2);

        accounts.transfer(transactionID, accountID1, accountID2, 25.00F);
        accounts.transfer(transactionID, accountID1, accountID2, 25.00F);

        var account1BalanceAfter = accounts.getBalance(accountID1);
        var account2BalanceAfter = accounts.getBalance(accountID2);

        assertEquals(account1Balance, account1BalanceAfter + 25.00F);
        assertEquals(account2Balance, account2BalanceAfter - 25.00F);
    }

    @Test
    void repeatWithdrawTransactionTest() throws AccountException {
        var transactionID = generateTransactionID();

        var account1Balance = accounts.getBalance(accountID1);

        accounts.withdraw(transactionID, accountID1, 25.00F);

        var account1BalanceAfter = accounts.getBalance(accountID1);

        assertEquals(account1Balance, account1BalanceAfter + 25.00F);
    }

    @Test
    void repeatDepositTransactionTest() throws AccountException {
        var transactionID = generateTransactionID();
        var account1Balance = accounts.getBalance(accountID1);

        accounts.deposit(transactionID, accountID1, 25.00F);

        var account1BalanceAfter = accounts.getBalance(accountID1);

        assertEquals(account1Balance, account1BalanceAfter - 25.00F);
    }

    @Test
    void tooLargeTransferTest() {
        var transactionID = generateTransactionID();
        assertThrows(AccountException.class, () -> accounts.transfer(transactionID, accountID1, accountID2, 500.00F));
    }

    public TransactionID generateTransactionID(){
        var transactionUUID = UUID.randomUUID();
        return new TransactionID(transactionUUID);
    }

    @Test
    void deleteAccountWhichExists(){

    }

    @Test
    void deleteAccountWhichDoesNotExist(){

    }
}
