package io.github.rxue.investmentutility.v2.marketquote.vo.price;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Value(BigDecimal value, String currency) implements  Comparable<Value> {
    public long valueInCent() {
        return value.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
    }

    @Override
    public String toString() {
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString() + " " + currency;
    }

    @Override
    public int compareTo(Value quotePrice) {
        throw new UnsupportedOperationException();
    }
}
