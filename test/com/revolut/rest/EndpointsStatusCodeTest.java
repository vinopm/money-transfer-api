package com.revolut.rest;

import com.revolut.account.AccountID;
import com.revolut.account.AccountService;
import com.revolut.account.Accounts;
import com.revolut.uuid.UUIDService;
import com.revolut.history.HistoryService;
import com.revolut.transfer.TransactionID;
import com.revolut.transfer.TransferService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;

import static com.revolut.rest.StatusCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EndpointsStatusCodeTest {
    private MockExternalService externalService;

    private TransactionID transactionID;
    private AccountID fromID;
    private AccountID toID;
    private String amount;

    @BeforeEach
    void setUp() throws IOException, Accounts.AccountException {
        externalService = new MockExternalService();
        var accounts = new Accounts();
        TransferService transferService = new TransferService(accounts, externalService, "/transfer");
        AccountService accountService = new AccountService(accounts, externalService, "/account");
        HistoryService historyService = new HistoryService(accounts, externalService, "/history");
        UUIDService uuidService = new UUIDService(externalService, "/uuid");
        externalService.start();

        transactionID = new TransactionID(UUID.randomUUID());
        fromID = new AccountID(UUID.randomUUID());
        toID = new AccountID(UUID.randomUUID());
        amount = "12.34";

        externalService.makeRequest("/account/create", new MockRequest().setMethod("PUT").setRequestBody("account_id="+fromID));
        externalService.makeRequest("/account/create", new MockRequest().setMethod("PUT").setRequestBody("account_id="+toID));

        String depositAmount = "100.00";
        externalService.makeRequest("/transfer/deposit", new MockRequest().setMethod("PUT").setRequestBody("transaction_id="+UUID.randomUUID().toString()+"&account_id="+fromID+"&"+"amount="+depositAmount));
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
        assertEquals(OK.getStatusCode(), resp.statusCode());
    }

    @Test
    void notSufficientFundsTest(){
        float transferAmount = 100.01F;
        Request mockRequest = new MockRequest()
                .setMethod("PUT")
                .setRequestBody("transaction_id="+transactionID+"&from="+fromID+"&to="+toID+"&amount=" + transferAmount);
        Response resp = externalService.makeRequest("/transfer/create", mockRequest);
        assertEquals(BAD_REQUEST.getStatusCode(), resp.statusCode());
    }

    @Test
    void validTransactionsRequestStatusCodeTest() throws IOException, InterruptedException {
        Request mockRequest = new MockRequest()
                .setMethod("GET")
                .setQueryPath("/history?account_id="+fromID);
        Response resp = externalService.makeRequest("/history", mockRequest);
        assertEquals(OK.getStatusCode(), resp.statusCode());
    }

    @Test
    void partialParamsStatusCodeTest() throws IOException, InterruptedException {
        Request mockRequest = new MockRequest()
                .setMethod("PUT")
                .setRequestBody("from="+fromID+"&to="+toID);

        Response resp = externalService.makeRequest("/transfer/create", mockRequest);
        assertEquals(NOT_ACCEPTABLE_FORMAT.getStatusCode(), resp.statusCode());
    }

    @Test
    void emptyRequestStatusCodeTest() throws IOException, InterruptedException {
        Request mockRequest = new MockRequest()
                .setMethod("PUT")
                .setRequestBody("");

        Response resp = externalService.makeRequest("/transfer/create", mockRequest);
        assertEquals(BAD_REQUEST.getStatusCode(), resp.statusCode());
    }

    @Test
    void invalidNestedMethodStatusCodeTest() {
        Request mockRequest = new MockRequest()
                .setMethod("GET")
                .setQueryPath("/history/invalidMethod?param=12345");

        Response resp = externalService.makeRequest("/history/invalidMethod", mockRequest);
        assertEquals(NOT_FOUND.getStatusCode(), resp.statusCode());
    }

    @Test
    void invalidMethodStatusCodeTest() {
        Request mockRequest = new MockRequest()
                .setMethod("GET")
                .setQueryPath("/transfer/invalidMethod?param=12345");

        Response resp = externalService.makeRequest("/transfer/invalidMethod", mockRequest);
        assertEquals(NOT_FOUND.getStatusCode(), resp.statusCode());
    }

    @Test
    void doubleEqualsGetStatusCodeTest() {
        Request mockRequest = new MockRequest()
                .setMethod("GET")
                .setQueryPath("/history?param==12345");

        Response resp = externalService.makeRequest("/history", mockRequest);
        assertEquals(NOT_ACCEPTABLE_FORMAT.getStatusCode(), resp.statusCode());
    }

    @Test
    void doubleEqualsPutStatusCodeTest() {
        Request mockRequest = new MockRequest()
                .setMethod("PUT")
                .setRequestBody("param==12345");

        Response resp = externalService.makeRequest("/transfer/create", mockRequest);
        assertEquals(NOT_ACCEPTABLE_FORMAT.getStatusCode(), resp.statusCode());
    }

    @Test
    void anEmptyParamGetStatusCodeTest() {
        Request mockRequest = new MockRequest()
                .setMethod("GET")
                .setQueryPath("/history?param=12345&");

        Response resp = externalService.makeRequest("/history", mockRequest);
        assertEquals(NOT_ACCEPTABLE_FORMAT.getStatusCode(), resp.statusCode());
    }

    @Test
    void anEmptyParamPutStatusCodeTest() {
        Request mockRequest = new MockRequest()
                .setMethod("PUT")
                .setRequestBody("param=12345&");

        Response resp = externalService.makeRequest("/transfer/create", mockRequest);
        assertEquals(NOT_ACCEPTABLE_FORMAT.getStatusCode(), resp.statusCode());
    }

    @Test
    void emptyParamGetStatusCodeTest() {
        Request mockRequest = new MockRequest()
                .setMethod("GET")
                .setQueryPath("/history?&");

        Response resp = externalService.makeRequest("/history", mockRequest);
        assertEquals(NOT_ACCEPTABLE_FORMAT.getStatusCode(), resp.statusCode());
    }

    @Test
    void onlyAndSignParamPutStatusCodeTest() {
        Request mockRequest = new MockRequest()
                .setMethod("PUT")
                .setRequestBody("&");

        Response resp = externalService.makeRequest("/transfer/create", mockRequest);
        assertEquals(NOT_ACCEPTABLE_FORMAT.getStatusCode(), resp.statusCode());
    }

    @Test
    void emptyParamPutStatusCodeTest() {
        Request mockRequest = new MockRequest()
                .setMethod("PUT")
                .setRequestBody("");

        Response resp = externalService.makeRequest("/transfer/create", mockRequest);
        assertEquals(BAD_REQUEST.getStatusCode(), resp.statusCode());
    }

    @Test
    void putTransactionsTest(){
        Request mockRequest = new MockRequest()
                .setMethod("PUT");

        Response resp = externalService.makeRequest("/history", mockRequest);
        assertEquals(METHOD_NOT_ALLOWED.getStatusCode(), resp.statusCode());
    }

    @Test
    void postTransactionsTest(){
        Request mockRequest = new MockRequest()
                .setMethod("POST");

        Response resp = externalService.makeRequest("/history", mockRequest);
        assertEquals(METHOD_NOT_ALLOWED.getStatusCode(), resp.statusCode());
    }

    @Test
    void deleteTransactionsTest(){
        Request mockRequest = new MockRequest()
                .setMethod("DELETE");

        Response resp = externalService.makeRequest("/history", mockRequest);
        assertEquals(METHOD_NOT_ALLOWED.getStatusCode(), resp.statusCode());
    }

    @Test
    void getCreateTest(){
        Request mockRequest = new MockRequest()
                .setMethod("GET");

        Response resp = externalService.makeRequest("/transfer/create", mockRequest);
        assertEquals(METHOD_NOT_ALLOWED.getStatusCode(), resp.statusCode());
    }

    @Test
    void postCreateTest(){
        Request mockRequest = new MockRequest()
                .setMethod("POST");

        Response resp = externalService.makeRequest("/transfer/create", mockRequest);
        assertEquals(METHOD_NOT_ALLOWED.getStatusCode(), resp.statusCode());
    }

    @Test
    void transferInvalidTransactionIDTest(){
        final String transactionID = "garbage-id";
        Request mockRequest = new MockRequest()
                .setMethod("PUT")
                .setRequestBody("transaction_id="+transactionID+"&from="+fromID+"&to="+toID+"&amount=" + amount);
        Response resp = externalService.makeRequest("/transfer/create", mockRequest);
        assertEquals(NOT_ACCEPTABLE_FORMAT.getStatusCode(), resp.statusCode());
    }

    @Test
    void transferInvalidAccountIDTest(){
        final String fromID = "garbage-id";
        Request mockRequest = new MockRequest()
                .setMethod("PUT")
                .setRequestBody("transaction_id="+transactionID+"&from="+fromID+"&to="+toID+"&amount=" + amount);
        Response resp = externalService.makeRequest("/transfer/create", mockRequest);
        assertEquals(NOT_ACCEPTABLE_FORMAT.getStatusCode(), resp.statusCode());
    }

    @Test
    void historyInvalidAccountIDTest(){
        final String accountID = "garbage-id";
        Request mockRequest = new MockRequest()
                .setMethod("GET")
                .setQueryPath("account_id="+accountID);
        Response resp = externalService.makeRequest("/history", mockRequest);
        assertEquals(NOT_ACCEPTABLE_FORMAT.getStatusCode(), resp.statusCode());
    }

    @Test
    void withdrawTest(){
        Request mockRequest = new MockRequest()
                .setMethod("PUT")
                .setRequestBody("transaction_id="+transactionID+"&account_id="+fromID+"&amount="+amount);
        var resp = externalService.makeRequest("/transfer/withdraw", mockRequest);
        assertEquals(OK.getStatusCode(), resp.statusCode());
    }

    @Test
    void depositTest(){
        Request mockRequest = new MockRequest()
                .setMethod("PUT")
                .setRequestBody("transaction_id="+transactionID+"&account_id="+fromID+"&amount="+amount);
        var resp = externalService.makeRequest("/transfer/deposit", mockRequest);
        assertEquals(OK.getStatusCode(), resp.statusCode());
    }

    @Test
    void deleteAccountTest(){
        Request mockRequest = new MockRequest()
                .setMethod("DELETE")
                .setQueryPath("/account/delete?account_id="+fromID);
        var resp = externalService.makeRequest("/account/delete", mockRequest);
        assertEquals(OK.getStatusCode(), resp.statusCode());
    }
}