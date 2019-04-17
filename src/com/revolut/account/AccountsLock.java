package com.revolut.account;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class AccountsLock {
    private final Map<AccountID, Lock> accountLocks = new ConcurrentHashMap<>();

    void lock(AccountID... accountIDs){
        Arrays.sort(accountIDs);
        Arrays.stream(accountIDs).forEachOrdered(this::lock);
    }

    void unlock(AccountID... accountIDs){
        Arrays.sort(accountIDs, Collections.reverseOrder());
        Arrays.stream(accountIDs).forEachOrdered(this::unlock);
    }

    private void lock(AccountID accountID){
        var lock = accountLocks.computeIfAbsent(accountID, id -> new ReentrantLock());
        lock.lock();
    }

    private void unlock(AccountID accountID){
        var lock = accountLocks.get(accountID);

        if(lock == null)
            return;

        accountLocks.remove(accountID);
        lock.unlock();

    }
}
