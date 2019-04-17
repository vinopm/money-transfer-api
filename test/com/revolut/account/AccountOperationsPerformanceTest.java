package com.revolut.account;

import com.revolut.transfer.TransactionID;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class AccountOperationsPerformanceTest {
    private final Accounts accounts;
    private final Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);

    AccountOperationsPerformanceTest(){
        this.accounts = new Accounts();
    }

    @Test
    void simpleCreateAccountTest(){
        final var account = new AccountID(UUID.randomUUID());

        var tsBefore = System.currentTimeMillis();
        accounts.createAccount(account);
        var tsAfter = System.currentTimeMillis();

        System.out.println(tsAfter - tsBefore + " ms");
    }

    @Test
    void manyCreateAccountTest() throws InterruptedException {
        var latch = new CountDownLatch(1000);
        var tsBefore = System.currentTimeMillis();
        for(var x = 0; x < 1000; x++) {
            executor.execute(() -> {
                final var account = new AccountID(UUID.randomUUID());
                accounts.createAccount(account);
                latch.countDown();
            });
        }

        latch.await();

        var tsAfter = System.currentTimeMillis();

        System.out.println(tsAfter - tsBefore + " ms");
    }

    @Test
    void manyTransfersTest() throws InterruptedException, Accounts.AccountException {
        List<AccountID> setOfAccounts = new ArrayList<>();
        for(var x = 0; x < 100; x++){
            final var account = new AccountID(UUID.randomUUID());
            accounts.createAccount(account);
            accounts.deposit(new TransactionID(UUID.randomUUID()), account, Money.parseMoney("10000000.00"));
            setOfAccounts.add(account);
        }


        var latch = new CountDownLatch(1000);
        var tsBefore = System.currentTimeMillis();
        var random = new Random();
        for(var x = 0; x < 1000; x++) {
            executor.execute(() -> {
                try {
                    accounts.transfer(new TransactionID(UUID.randomUUID()), setOfAccounts.get(random.nextInt(setOfAccounts.size())), setOfAccounts.get(random.nextInt(setOfAccounts.size())), new Money("1.50"));
                } catch (Accounts.AccountException e) {
                    //ignore for purpose of performance test
                }finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        var tsAfter = System.currentTimeMillis();

        System.out.println(tsAfter - tsBefore + " ms");
    }
}
