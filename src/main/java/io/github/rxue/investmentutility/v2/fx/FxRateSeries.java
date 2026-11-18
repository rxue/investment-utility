package io.github.rxue.investmentutility.v2.fx;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

/**
 * Base currency is EURO by default atm
 */
public final class FxRateSeries {
    private final String quoteCurrency;
    private final LocalDate[] dates;
    private final BigDecimal[] values;
    private final FxRateArray fxRateArray;
    public FxRateSeries(String quoteCurrency, LocalDate[] dates, BigDecimal[] values) {
        this.quoteCurrency = quoteCurrency;
        this.dates = Arrays.copyOf(dates, dates.length);
        this.values = Arrays.copyOf(values, values.length);
        this.fxRateArray = new FxRateArray(dates, values);
    }
    public String quoteCurrency() {
        return quoteCurrency;
    }

    public BigDecimal get(LocalDate date) {
        return fxRateArray.get(date);
    }

    /**
     * Given a list of local dates, convert it to array of epoch  so that it can be easily searched by index
     */
    static class FxRateArray {
        private final LocalDate[] dates;
        private final BigDecimal[] values;
        private final int epochDaysOffset;
        private final BigDecimal[] fxRatesWithFilled;
        private static int between(LocalDate fromInclusive, LocalDate toInclusive) {
            return (int) (ChronoUnit.DAYS.between(fromInclusive, toInclusive));
        }
        FxRateArray(LocalDate[] dates, BigDecimal[] values) {
            this.dates = dates;
            this.values = values;
            this.epochDaysOffset = (int) dates[0].toEpochDay();
            this.fxRatesWithFilled = new BigDecimal[between(dates[0], dates[dates.length-1].plusDays(1))];
            for (int i = 0; i < dates.length-1; i++) {
                fill(fxRatesWithFilled, values[i], dates[i], dates[i+1]);
            }
            fxRatesWithFilled[fxRatesWithFilled.length-1] = values[values.length - 1];
        }

        private void fill(BigDecimal[] allFxRatesToFill, BigDecimal fxRate, LocalDate date, LocalDate nextDateWithUpdatedFxRate) {
            for (int i = (int)(date.toEpochDay() - epochDaysOffset);
                 i < (int)(nextDateWithUpdatedFxRate.toEpochDay() - epochDaysOffset); i++) {
                allFxRatesToFill[i] = fxRate;
            }
        }
        BigDecimal get(LocalDate date) {
            int index = (int)(date.toEpochDay() - epochDaysOffset);
            if (index >= fxRatesWithFilled.length) {
                return fxRatesWithFilled[fxRatesWithFilled.length-1];
            }
            return fxRatesWithFilled[(int)(date.toEpochDay() - epochDaysOffset)];
        }
    }
}
