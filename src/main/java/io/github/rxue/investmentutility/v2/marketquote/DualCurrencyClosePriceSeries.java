package io.github.rxue.investmentutility.v2.marketquote;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DualCurrencyClosePriceSeries extends ClosePriceSeries {
    String convertedCurrency();
    BigDecimal convertedPrice(LocalDate date);
}
