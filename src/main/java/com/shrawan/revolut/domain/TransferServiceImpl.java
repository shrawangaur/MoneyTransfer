package com.shrawan.revolut.domain;

import com.shrawan.revolut.domain.model.Account;

import java.math.BigDecimal;
import java.math.BigInteger;

public class TransferServiceImpl implements TransferService {

    private  AccountRepository accountRepository;
    private  CurrencyRatesProvider ratesProvider;
    private  AccountTransactionService transactionService;

    public TransferServiceImpl(){}

    public TransferServiceImpl(AccountRepository accountRepository, CurrencyRatesProvider ratesProvider, AccountTransactionService transactionService) {
        this.accountRepository = accountRepository;
        this.ratesProvider = ratesProvider;
        this.transactionService = transactionService;
    }

    @Override
    public void makeTransfer(String accountFromNum, String accountToNum, BigDecimal amount) {

        Account accountFrom = getAccountByNumber(accountFromNum);
        Account accountTo = getAccountByNumber(accountToNum);

        BigDecimal currencyRate = ratesProvider.getCurrencyRate(accountFrom.getCurrency(), accountTo.getCurrency());

        BigInteger transferedAmountFrom = amount.multiply(BigDecimal.TEN.pow(accountFrom.getCurrency().getMinorDigits())).toBigInteger();

        BigInteger transferedAmountTo = amount.multiply(currencyRate)
                .multiply((BigDecimal.TEN.pow(accountTo.getCurrency().getMinorDigits()))).toBigInteger();

        transactionService.doInTransaction(accountFrom, accountTo, (from, to) -> {

            if (from.getAccountBalance().compareTo(transferedAmountFrom) < 0) {
                throw new IllegalArgumentException("Account " + accountFromNum + " has insufficient funds (req: " + transferedAmountFrom + "; actual: " + from.getAccountBalance());
            }

            from.setAccountBalance(from.getAccountBalance().subtract(transferedAmountFrom));
            to.setAccountBalance(to.getAccountBalance().add(transferedAmountTo));
        });

    }

    private Account getAccountByNumber(String accountNum) {
        return accountRepository.findByAccountNumber(accountNum)
                .orElseThrow(() -> new IllegalArgumentException("Account " + accountNum + " not found!"));
    }
}
