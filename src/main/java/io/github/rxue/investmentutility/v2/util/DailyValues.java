package io.github.rxue.investmentutility.v2.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

public class DailyValues{
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
}
