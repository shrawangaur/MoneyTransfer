package com.shrawan.revolut.domain;

import com.shrawan.revolut.domain.model.Currency;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class CurrencyRatesProviderImpl implements CurrencyRatesProvider {

    private static class CacheKey {
        String curFrom;
        String curTo;

        public CacheKey(String curFrom, String curTo) {
            this.curFrom = curFrom;
            this.curTo = curTo;
        }
    }

    private final Map<CacheKey, BigDecimal> currentyRates;

    {
        currentyRates = new HashMap<>();
        currentyRates.put(new CacheKey("RUB", "USD"), new BigDecimal("56.12"));
        currentyRates.put(new CacheKey("USD", "RUB"), new BigDecimal("0.0174"));
        currentyRates.put(new CacheKey("RUB", "EUR"), new BigDecimal("62.32"));
        currentyRates.put(new CacheKey("EUR", "RUB"), new BigDecimal("0.0161"));
        currentyRates.put(new CacheKey("EUR", "USD"), new BigDecimal("1.12"));
        currentyRates.put(new CacheKey("USD", "EUR"), new BigDecimal("0.92"));
    }

    @Override
    public BigDecimal getCurrencyRate(Currency from, Currency to) {
        if (from.equals(to)) {
            return BigDecimal.ONE;
        }
        BigDecimal rate = currentyRates.get(new CacheKey(from.getCode(), to.getCode()));
        if (rate == null) {
            throw new IllegalArgumentException("No conversion rates found for (" + from.getCode() + "," + to.getCode() + ")");
        }
        return rate;
    }
}
