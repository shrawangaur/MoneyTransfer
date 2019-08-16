package com.shrawan.revolut.api;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.shrawan.revolut.domain.model.Account;

import java.math.BigDecimal;

@JsonPropertyOrder({"accountNumber", "currency", "accountBalance"})
public class AccountDto {

    private String accountNumber;

    private String currency;
    private BigDecimal accountBalance;

    public AccountDto(String accountNumber, String currency, BigDecimal accountBalance) {
        this.accountNumber = accountNumber;
        this.currency = currency;
        this.accountBalance = accountBalance;
    }

    public static AccountDto fromDomain(Account account) {
        return new AccountDto(account.getAccountNumber(), account.getCurrency().getCode(), account.getDecimalBalance());
    }

    public String getAccountNumber() {
        return accountNumber;
    }


}
