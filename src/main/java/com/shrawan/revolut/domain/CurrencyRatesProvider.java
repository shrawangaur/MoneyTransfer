package com.shrawan.revolut.domain;

import com.shrawan.revolut.domain.model.Currency;

import java.math.BigDecimal;

public interface CurrencyRatesProvider {

    BigDecimal getCurrencyRate(Currency from, Currency to);
}
