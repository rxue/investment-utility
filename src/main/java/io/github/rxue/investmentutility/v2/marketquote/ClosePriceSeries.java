package io.github.rxue.investmentutility.v2.marketquote;

import java.time.LocalDate;

public interface ClosePriceSeries<T> {
    String currency();
    T price(LocalDate date);
}
