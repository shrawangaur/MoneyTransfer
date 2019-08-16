package com.shrawan.revolut;

import com.shrawan.revolut.domain.AccountRepository;
import com.shrawan.revolut.domain.model.Account;
import com.shrawan.revolut.domain.model.Currency;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.stream.IntStream;

/**
 * Populate the DB with some 256 accounts to make app life-like.<br/>
 * First 150 are RUB accounts, the rest are in EUR.<br/>
 */
public class TestAccountsPopulation {

    public static void populateAccounts(AccountRepository rep) {

        IntStream.range(1, 256)
                .mapToObj(i -> {
                    Currency currency = i < 150 ? Currency.RUB : Currency.EUR;
                    return new Account("GB" + StringUtils.leftPad(String.valueOf(i), 9, '0'),
                            currency,
                            BigInteger.valueOf(i)
                                    .multiply(BigInteger.TEN.pow(currency.getMinorDigits())));
                })
                .forEach(rep::save);
    }
}
