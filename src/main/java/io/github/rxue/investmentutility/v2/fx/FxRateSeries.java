package io.github.rxue.investmentutility.v2.fx;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Base currency is EURO by default atm
 */
public interface FxRateSeries {
    String quoteCurrency();
    BigDecimal get(LocalDate date);
}
