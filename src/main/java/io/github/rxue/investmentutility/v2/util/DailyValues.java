package io.github.rxue.investmentutility.v2.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

public final class DailyValues{
    private final int firstEpochDay;
    private final BigDecimal[] values;

    public DailyValues(int firstEpochDay, BigDecimal[] values) {
        this.firstEpochDay = firstEpochDay;
        this.values = Arrays.copyOf(values, values.length);
    }

    public BigDecimal get(LocalDate date) {
        int index = (int)date.toEpochDay() - firstEpochDay;
        if (index >= values.length) {
            return values[values.length-1];
        }
        return values[(int)date.toEpochDay() - firstEpochDay];
    }
    public static DailyValues generate(LocalDate[] dates, BigDecimal[] values) {
        long firstEpochDay = dates[0].toEpochDay();
        final int amountOfDays = (int) ChronoUnit.DAYS.between(dates[0], dates[dates.length-1].plusDays(1));
        BigDecimal[] dailyValues = new BigDecimal[amountOfDays];
        for (int i = 0; i < dates.length-1; i++) {
            fill(values[i], firstEpochDay, dates[i], dates[i+1], dailyValues);
        }
        dailyValues[dailyValues.length-1] = values[values.length-1];
        return new DailyValues((int) firstEpochDay, dailyValues);
    }
    private static void fill(BigDecimal fxRate, final long firstEpochDay, LocalDate date, LocalDate nextDate, BigDecimal[] valuesToFill) {
        for (int i = (int)(date.toEpochDay() - firstEpochDay);
             i < (int)(nextDate.toEpochDay() - firstEpochDay); i++) {
            valuesToFill[i] = fxRate;
        }
    }
}
