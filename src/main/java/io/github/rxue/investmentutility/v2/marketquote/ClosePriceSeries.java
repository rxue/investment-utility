package io.github.rxue.investmentutility.v2.marketquote;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ClosePriceSeries {
    String currency();
    BigDecimal get(LocalDate date);
}
