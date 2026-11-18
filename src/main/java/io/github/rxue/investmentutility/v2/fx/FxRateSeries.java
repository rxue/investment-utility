package io.github.rxue.investmentutility.v2.fx;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

/**
 * Base currency is EURO by default atm
 */
public final class FxRateSeries {
    private final String quoteCurrency;
    private final LocalDate[] dates;
    private final BigDecimal[] values;
    public FxRateSeries(String quoteCurrency, LocalDate[] dates, BigDecimal[] values) {
        this.quoteCurrency = quoteCurrency;
        this.dates = Arrays.copyOf(dates, dates.length);
        this.values = Arrays.copyOf(values, values.length);
    }
}
