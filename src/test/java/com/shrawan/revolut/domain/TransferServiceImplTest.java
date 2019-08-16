package com.shrawan.revolut.domain;

import com.shrawan.revolut.domain.model.Account;
import com.shrawan.revolut.domain.model.Currency;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TransferServiceImplTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CurrencyRatesProvider currencyRatesProvider;
    private AccountTransactionService transactionService = (account1, account2, action) -> action.accept(account1, account2); // mock
    private TransferService transferService;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        transferService = new TransferServiceImpl(accountRepository, currencyRatesProvider, transactionService);
        Mockito.when(currencyRatesProvider.getCurrencyRate(Currency.RUB, Currency.RUB))
                .thenReturn(BigDecimal.ONE);
        Mockito.when(currencyRatesProvider.getCurrencyRate(Currency.USD, Currency.EUR))
                .thenReturn(new BigDecimal("0.92"));
    }


    @Test
    public void checkSimpleTransfer() {
        Account account1 = new Account("acc1", Currency.RUB, BigInteger.valueOf(20000));
        Account account2 = new Account("acc2", Currency.RUB, BigInteger.valueOf(40000));
        Mockito.when(accountRepository.findByAccountNumber("acc1"))
                .thenReturn(Optional.of(account1));
        Mockito.when(accountRepository.findByAccountNumber("acc2"))
                .thenReturn(Optional.of(account2));

        transferService.makeTransfer("acc1", "acc2", new BigDecimal("125.0"));

        assertThat(account1.getAccountBalance(), is(BigInteger.valueOf(7500)));
        assertThat(account2.getAccountBalance(), is(BigInteger.valueOf(52500)));

    }

    @Test
    public void checkConversionTransfer() {
        Account account1 = new Account("acc1", Currency.USD, BigInteger.valueOf(14000));
        Account account2 = new Account("acc2", Currency.EUR, BigInteger.valueOf(40000));
        Mockito.when(accountRepository.findByAccountNumber("acc1"))
                .thenReturn(Optional.of(account1));
        Mockito.when(accountRepository.findByAccountNumber("acc2"))
                .thenReturn(Optional.of(account2));

        transferService.makeTransfer("acc1", "acc2", new BigDecimal("20"));

        assertThat(account1.getAccountBalance(), is(BigInteger.valueOf(12000)));
        assertThat(account2.getAccountBalance(), is(BigInteger.valueOf(58400)));

    }

}