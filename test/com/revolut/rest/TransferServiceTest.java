package com.revolut.rest;

import com.revolut.account.AccountID;
import com.revolut.account.Accounts;
import com.revolut.account.TransferService;
import com.revolut.transaction.TransactionID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;

import static com.revolut.rest.HttpRequest.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransferServiceTest {
    private MockExternalService externalService;

    private TransactionID transactionID;
    private AccountID fromID;
    private AccountID toID;
    private float amount;

    @BeforeEach
    void setUp() throws IOException {
        externalService = new MockExternalService();
        TransferService transferService = new TransferService(new Accounts(), externalService, "/transfer");
        externalService.start();

        transactionID = new TransactionID(UUID.randomUUID());
        fromID = new AccountID(UUID.randomUUID());
        toID = new AccountID(UUID.randomUUID());
        amount = 12.34F;

        externalService.makeRequest("/transfer/account/create", new MockRequest().setMethod("PUT").setRequestBody("account_id="+fromID));
        externalService.makeRequest("/transfer/account/create", new MockRequest().setMethod("PUT").setRequestBody("account_id="+toID));
    }

    @AfterEach
    void tearDown() throws IOException {
        externalService.stop();
    }

    @Test
    void validCreateRequestStatusCodeTest() throws IOException, InterruptedException {
        Request mockRequest = new MockRequest()
                .setMethod("PUT")
                .setRequestBody("transaction_id="+transactionID+"&from="+fromID+"&to="+toID+"&amount=" + amount);
        Response resp = externalService.makeRequest("/transfer/create", mockRequest);
        assertEquals(OK, resp.statusCode());
    }

    @Test
    void validTransactionsRequestStatusCodeTest() throws IOException, InterruptedException {
        Request mockRequest = new MockRequest()
                .setMethod("GET")
                .setQueryPath("/transfer/transactions?account_id="+fromID);
        Response resp = externalService.makeRequest("/transfer/transactions", mockRequest);
        assertEquals(OK, resp.statusCode());
    }

    @Test
    void partialParamsStatusCodeTest() throws IOException, InterruptedException {
        Request mockRequest = new MockRequest()
                .setMethod("PUT")
                .setRequestBody("from="+fromID+"&to="+toID);

        Response resp = externalService.makeRequest("/transfer/create", mockRequest);
        assertEquals(NOT_ACCEPTABLE_FORMAT, resp.statusCode());
    }

    @Test
    void emptyRequestStatusCodeTest() throws IOException, InterruptedException {
        Request mockRequest = new MockRequest()
                .setMethod("PUT")
                .setRequestBody("");

        Response resp = externalService.makeRequest("/transfer/create", mockRequest);
        assertEquals(BAD_REQUEST, resp.statusCode());
    }

    @Test
    void invalidNestedMethodStatusCodeTest() {
        Request mockRequest = new MockRequest()
                .setMethod("GET")
                .setQueryPath("/transfer/transactions/invalidMethod?param=12345");

        Response resp = externalService.makeRequest("/transfer/transactions/invalidMethod", mockRequest);
        assertEquals(METHOD_NOT_ALLOWED, resp.statusCode());
    }

    @Test
    void invalidMethodStatusCodeTest() {
        Request mockRequest = new MockRequest()
                .setMethod("GET")
                .setQueryPath("/transfer/invalidMethod?param=12345");

        Response resp = externalService.makeRequest("/transfer/invalidMethod", mockRequest);
        assertEquals(METHOD_NOT_ALLOWED, resp.statusCode());
    }

    @Test
    void doubleEqualsGetStatusCodeTest() {
        Request mockRequest = new MockRequest()
                .setMethod("GET")
                .setQueryPath("/transfer/transactions?param==12345");

        Response resp = externalService.makeRequest("/transfer/transactions", mockRequest);
        assertEquals(NOT_ACCEPTABLE_FORMAT, resp.statusCode());
    }

    @Test
    void doubleEqualsPutStatusCodeTest() {
        Request mockRequest = new MockRequest()
                .setMethod("PUT")
                .setRequestBody("param==12345");

        Response resp = externalService.makeRequest("/transfer/create", mockRequest);
        assertEquals(NOT_ACCEPTABLE_FORMAT, resp.statusCode());
    }

    @Test
    void anEmptyParamGetStatusCodeTest() {
        Request mockRequest = new MockRequest()
                .setMethod("GET")
                .setQueryPath("/transfer/transactions?param=12345&");

        Response resp = externalService.makeRequest("/transfer/transactions", mockRequest);
        assertEquals(NOT_ACCEPTABLE_FORMAT, resp.statusCode());
    }

    @Test
    void anEmptyParamPutStatusCodeTest() {
        Request mockRequest = new MockRequest()
                .setMethod("PUT")
                .setRequestBody("param=12345&");

        Response resp = externalService.makeRequest("/transfer/create", mockRequest);
        assertEquals(NOT_ACCEPTABLE_FORMAT, resp.statusCode());
    }

    @Test
    void emptyParamGetStatusCodeTest() {
        Request mockRequest = new MockRequest()
                .setMethod("GET")
                .setQueryPath("/transfer/transactions?&");

        Response resp = externalService.makeRequest("/transfer/transactions", mockRequest);
        assertEquals(NOT_ACCEPTABLE_FORMAT, resp.statusCode());
    }

    @Test
    void onlyAndSignParamPutStatusCodeTest() {
        Request mockRequest = new MockRequest()
                .setMethod("PUT")
                .setRequestBody("&");

        Response resp = externalService.makeRequest("/transfer/create", mockRequest);
        assertEquals(NOT_ACCEPTABLE_FORMAT, resp.statusCode());
    }

    @Test
    void emptyParamPutStatusCodeTest() {
        Request mockRequest = new MockRequest()
                .setMethod("PUT")
                .setRequestBody("");

        Response resp = externalService.makeRequest("/transfer/create", mockRequest);
        assertEquals(BAD_REQUEST, resp.statusCode());
    }

    @Test
    void putTransactionsTest(){
        Request mockRequest = new MockRequest()
                .setMethod("PUT");

        Response resp = externalService.makeRequest("/transfer/transactions", mockRequest);
        assertEquals(METHOD_NOT_ALLOWED, resp.statusCode());
    }

    @Test
    void postTransactionsTest(){
        Request mockRequest = new MockRequest()
                .setMethod("POST");

        Response resp = externalService.makeRequest("/transfer/transactions", mockRequest);
        assertEquals(METHOD_NOT_ALLOWED, resp.statusCode());
    }

    @Test
    void deleteTransactionsTest(){
        Request mockRequest = new MockRequest()
                .setMethod("DELETE");

        Response resp = externalService.makeRequest("/transfer/transactions", mockRequest);
        assertEquals(METHOD_NOT_ALLOWED, resp.statusCode());
    }

    @Test
    void getCreateTest(){
        Request mockRequest = new MockRequest()
                .setMethod("GET");

        Response resp = externalService.makeRequest("/transfer/create", mockRequest);
        assertEquals(METHOD_NOT_ALLOWED, resp.statusCode());
    }

    @Test
    void postCreateTest(){
        Request mockRequest = new MockRequest()
                .setMethod("POST");

        Response resp = externalService.makeRequest("/transfer/create", mockRequest);
        assertEquals(METHOD_NOT_ALLOWED, resp.statusCode());
    }
}
