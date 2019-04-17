package com.revolut.history;

import com.revolut.account.AccountID;
import com.revolut.account.Accounts;
import com.revolut.account.Money;
import com.revolut.transfer.Transaction;
import com.revolut.transfer.TransactionID;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HistoryTest {
    private final Accounts accounts = new Accounts();

    @Test
    void validAccountsHistoryTest() throws Accounts.AccountException {
        var accountID1 = new AccountID(UUID.randomUUID());
        var accountID2 = new AccountID(UUID.randomUUID());

        accounts.createAccount(accountID1);
        accounts.createAccount(accountID2);

        accounts.deposit(new TransactionID(UUID.randomUUID()), accountID1, Money.parseMoney("10.00"));
        accounts.transfer(new TransactionID(UUID.randomUUID()), accountID1, accountID2, Money.parseMoney("10.00"));
        accounts.withdraw(new TransactionID(UUID.randomUUID()), accountID2, Money.parseMoney("10.00"));
        var transactionsAccount1 = accounts.transactions(accountID1);
        var transactionsAccount2 = accounts.transactions(accountID2);

        assertEquals(2, transactionsAccount1.size());
        assertEquals(2, transactionsAccount2.size());

        {
            Transaction[] transactions = transactionsAccount1.toArray(new Transaction[0]);

            Transaction firstTransaction = transactions[0];
            assertTrue(firstTransaction.toString().contains("account="+accountID1));
            assertTrue(firstTransaction.toString().contains("amount="+"10.00"));
            assertTrue(firstTransaction.toString().contains("deposit"));

            Transaction secondTransaction = transactions[1];
            assertTrue(secondTransaction.toString().contains("from="+accountID1));
            assertTrue(secondTransaction.toString().contains("to="+accountID2));
            assertTrue(secondTransaction.toString().contains("amount="+"10.00"));
            assertTrue(secondTransaction.toString().contains("transfer"));
        }

        {
            Transaction[] transactions = transactionsAccount2.toArray(new Transaction[0]);

            Transaction firstTransaction = transactions[0];
            assertTrue(firstTransaction.toString().contains("from="+accountID1));
            assertTrue(firstTransaction.toString().contains("to="+accountID2));
            assertTrue(firstTransaction.toString().contains("amount="+"10.00"));
            assertTrue(firstTransaction.toString().contains("transfer"));

            Transaction secondTransaction = transactions[1];
            assertTrue(secondTransaction.toString().contains("account="+accountID2));
            assertTrue(secondTransaction.toString().contains("amount="+"10.00"));
            assertTrue(secondTransaction.toString().contains("withdraw"));
        }
    }
}
