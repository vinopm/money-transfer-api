package com.revolut.account;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * For simplicity we create a lock per account and lock each account before accessing.
 * For example, for a transfer we lock both from and to accounts.
 * We always lock in a pre-defined order to avoid deadlock.
 * Locks are created dynamically.
 *
 * For simplicity, locks are not removed from the map even after account is
 * deleted.
 */

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

        lock.unlock();
    }
}