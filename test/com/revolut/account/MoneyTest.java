package com.revolut.account;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void penniesTest() {
        assertThrows(NumberFormatException.class,() -> new Money(".32"));
    }

    @Test
    void poundsTest() {
        var money = new Money("30");
        assertEquals("30.00", money.toString());
    }

    @Test
    void combinationInputTest() {
        var money = new Money("30.59");
        assertEquals("30.59", money.toString());
    }

    @Test
    void extraPrecisionTest(){
        assertThrows(NumberFormatException.class, () -> new Money("0.001"));
    }

    @Test
    void smallValueTest(){
        var money = new Money("0.01");
        assertEquals("0.01", money.toString());
    }

    @Test
    void zeroTest(){
        var money = new Money("0.00");
        assertEquals("0.00", money.toString());
    }

    @Test
    void oneDecimalPlaceTest(){
        assertThrows(NumberFormatException.class, () -> new Money("5.9"));
    }

    @Test
    void doubleZeroTest(){
        var money = new Money("00.00");
        assertEquals("0.00", money.toString());
    }

    @Test
    void veryLargeTest(){
        var money = new Money("123123456789123.55");
        assertEquals("123123456789123.55", money.toString());
    }

    @Test
    void roundingTest(){
        var money = new Money("1.05");
        assertEquals("1.05", money.toString());
    }

    @Test
    void negativeTest(){
        var money = new Money("-1.05");
        assertEquals("-1.05", money.toString());
    }

    @Test
    void doNotRoundUpTest(){
        var money = new Money("5.99");
        assertEquals("5.99", money.toString());
    }

    @Test
    void add(){
        var money = new Money("5.99");
        var moneyAddition = new Money("5.99");
        assertEquals("11.98", money.add(moneyAddition).toString());
    }

    @Test
    void minusAll(){
        var money = new Money("5.99");
        var moneyAddition = new Money("5.99");
        assertEquals("0.00", money.minus(moneyAddition).toString());
    }

    @Test
    void minusDecimal(){
        var money = new Money("5.99");
        var moneyAddition = new Money("0.99");
        assertEquals("5.00", money.minus(moneyAddition).toString());
    }

    @Test
    void minus(){
        var money = new Money("5.05");
        var moneyAddition = new Money("3.71");
        money.minus(moneyAddition);
        assertEquals("1.34", money.minus(moneyAddition).toString());
    }
}