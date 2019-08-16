package com.shrawan.revolut.domain.model;

public enum Currency {


    RUB("RUB", 2),
    EUR("EUR", 3), // 3 is to make it interesting
    USD("USD", 2);

    private final String code;
    private final int minorDigits; // number of cents in dollar, kopecks in rub, etc.


    Currency(String code, int minorDigits) {
        this.code = code;
        this.minorDigits = minorDigits;
    }

    public String getCode() {
        return code;
    }

    public int getMinorDigits() {
        return minorDigits;
    }
}
