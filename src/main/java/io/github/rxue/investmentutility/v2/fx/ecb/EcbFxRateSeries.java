package io.github.rxue.investmentutility.v2.fx.ecb;

import io.github.rxue.investmentutility.v2.fx.FxRateSeries;
import io.github.rxue.investmentutility.v2.util.DailyValues;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

class EcbFxRateSeries implements FxRateSeries {
    private final String quoteCurrency;
    private final LocalDate[] dates;
    private final BigDecimal[] values;
    private final DailyValues dailyValues;
    public EcbFxRateSeries(String quoteCurrency, LocalDate[] dates, BigDecimal[] values) {
        this.quoteCurrency = quoteCurrency;
        this.dates = Arrays.copyOf(dates, dates.length);
        this.values = Arrays.copyOf(values, values.length);
        this.dailyValues = DailyValues.generate(dates, values);
    }
    @Override
    public String quoteCurrency() {
        return quoteCurrency;
    }
    @Override
    public BigDecimal get(LocalDate date) {
        return dailyValues.get(date);
    }
}
