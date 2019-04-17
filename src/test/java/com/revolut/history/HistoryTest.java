package com.revolut.history;

import com.revolut.account.AccountID;
import com.revolut.account.Accounts;
import com.revolut.account.Accounts.AccountException;
import com.revolut.account.Money;
import com.revolut.transfer.Transaction;
import com.revolut.transfer.TransactionID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HistoryTest {
    private final Accounts accounts = new Accounts();

    private AccountID accountID1;
    private AccountID accountID2;

    @BeforeEach
    void setUp() throws AccountException {
        this.accountID1 = new AccountID(UUID.randomUUID());
        this.accountID2 = new AccountID(UUID.randomUUID());

        accounts.createAccount(accountID1);
        accounts.createAccount(accountID2);

        accounts.deposit(new TransactionID(UUID.randomUUID()), accountID1, Money.parseMoney("10.00"));
        accounts.transfer(new TransactionID(UUID.randomUUID()), accountID1, accountID2, Money.parseMoney("10.00"));
        accounts.withdraw(new TransactionID(UUID.randomUUID()), accountID2, Money.parseMoney("10.00"));
    }

    @AfterEach
    void tearDown(){

    }

    @Test
    void validAccountsHistoryTest() throws AccountException {

        var transactionsAccount1 = accounts.transactions(accountID1);
        var transactionsAccount2 = accounts.transactions(accountID2);

        assertEquals(2, transactionsAccount1.size());
        assertEquals(2, transactionsAccount2.size());

        {
            var transactions = transactionsAccount1.toArray(new Transaction[0]);

            var firstTransaction = transactions[0];
            assertTrue(firstTransaction.toString().contains("account="+accountID1));
            assertTrue(firstTransaction.toString().contains("amount="+"10.00"));
            assertTrue(firstTransaction.toString().contains("deposit"));

            var secondTransaction = transactions[1];
            assertTrue(secondTransaction.toString().contains("from="+accountID1));
            assertTrue(secondTransaction.toString().contains("to="+accountID2));
            assertTrue(secondTransaction.toString().contains("amount="+"10.00"));
            assertTrue(secondTransaction.toString().contains("transfer"));
        }

        {
            var transactions = transactionsAccount2.toArray(new Transaction[0]);

            var firstTransaction = transactions[0];
            assertTrue(firstTransaction.toString().contains("from="+accountID1));
            assertTrue(firstTransaction.toString().contains("to="+accountID2));
            assertTrue(firstTransaction.toString().contains("amount="+"10.00"));
            assertTrue(firstTransaction.toString().contains("transfer"));

            var secondTransaction = transactions[1];
            assertTrue(secondTransaction.toString().contains("account="+accountID2));
            assertTrue(secondTransaction.toString().contains("amount="+"10.00"));
            assertTrue(secondTransaction.toString().contains("withdraw"));
        }
    }

    @Test
    void ensureReturnedCollectionUnmodifiable() throws AccountException {
        final int originalSize;
        final int latestSize;
        {
            var transactions = accounts.transactions(accountID1);
            originalSize = transactions.size();
            transactions.clear();
        }
        {
            var transactions = accounts.transactions(accountID1);
            latestSize = transactions.size();
        }
        assertEquals(latestSize, originalSize);
    }

    @Test
    void repeatTransactionIDTest() throws AccountException {
        accounts.transactions(accountID1);

        var transactionID = new TransactionID(UUID.randomUUID());
        var initialSize = accounts.transactions(accountID1).size();
        accounts.deposit(transactionID, accountID1, Money.parseMoney("10"));
        accounts.deposit(transactionID, accountID1, Money.parseMoney("10"));
        var finalSize = accounts.transactions(accountID1).size();
        assertEquals(initialSize + 1, finalSize);
    }
}
