package com.shrawan.revolut.api;


import java.math.BigDecimal;

public class TransferRequest {

    private String accountFrom;
    private String accountTo;
    private BigDecimal amount;

    public TransferRequest(String accountFrom, String accountTo, BigDecimal amount) {
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
    }

    public String getAccountFrom() {
        return accountFrom;
    }

    public String getAccountTo() {
        return accountTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
