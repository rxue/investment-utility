package io.github.rxue.investmentutility.v2.fx.ecb;

import io.github.rxue.investmentutility.v2.fx.FxRateSeries;
import io.github.rxue.investmentutility.v2.util.AbstractDailyValuesGenerator;
import io.github.rxue.investmentutility.v2.util.DailyValues;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

class EcbFxRateSeries extends AbstractDailyValuesGenerator implements FxRateSeries {
    private final String quoteCurrency;
    private final LocalDate[] dates;
    private final BigDecimal[] values;
    private final DailyValues dailyValues;
    public EcbFxRateSeries(String quoteCurrency, LocalDate[] dates, BigDecimal[] values) {
        this.quoteCurrency = quoteCurrency;
        this.dates = Arrays.copyOf(dates, dates.length);
        this.values = Arrays.copyOf(values, values.length);
        this.dailyValues = generate();
    }
    public String quoteCurrency() {
        return quoteCurrency;
    }

    public BigDecimal get(LocalDate date) {
        return dailyValues.get(date);
    }

    @Override
    protected DailyValues generate() {
        long firstEpochDay = dates[0].toEpochDay();
        final int amountOfDays = (int) ChronoUnit.DAYS.between(dates[0], dates[dates.length-1].plusDays(1));
        BigDecimal[] dailyValues = new BigDecimal[amountOfDays];
        for (int i = 0; i < dates.length-1; i++) {
            fill(values[i], firstEpochDay, dates[i], dates[i+1], dailyValues);
        }
        dailyValues[dailyValues.length-1] = values[values.length-1];
        return new DailyValues((int) firstEpochDay, dailyValues);
    }
    private void fill(BigDecimal fxRate, final long firstEpochDay, LocalDate date, LocalDate nextDate, BigDecimal[] valuesToFill) {
        for (int i = (int)(date.toEpochDay() - firstEpochDay);
             i < (int)(nextDate.toEpochDay() - firstEpochDay); i++) {
            valuesToFill[i] = fxRate;
        }
    }
}
