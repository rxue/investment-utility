package io.github.rxue.investmentutility.vo;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record QuotePrice(BigDecimal value, String currency) implements Comparable<QuotePrice> {
    public long valueInCent() {
        return value.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
    }

    @Override
    public String toString() {
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString() + " " + currency;
    }

    @Override
    public int compareTo(QuotePrice quotePrice) {
        throw new UnsupportedOperationException();
    }
}
