package io.github.rxue.investmentutility.v2.marketquote;

import java.time.LocalDate;

public interface CloseCentPriceSeries {
    String currency();
    long priceInCent(LocalDate date);
}
