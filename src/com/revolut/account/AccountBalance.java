package com.revolut.account;

public class AccountBalance {
    private float balance;

    AccountBalance(){
        balance = 0F;
    }

    void add(float amount){
        this.balance += amount;
    }

    void minus(float amount){
        this.balance -= amount;
    }
}
