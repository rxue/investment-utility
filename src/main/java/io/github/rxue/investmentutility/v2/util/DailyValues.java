package io.github.rxue.investmentutility.v2.util;

import java.time.LocalDate;
import java.util.function.Function;

public final class DailyValues<T,V> {
    private final T[] timestamps;
    private final V[] values;
    private final Function<T,Long> toEpochDay;
    private final LocalDate inclusiveEndDate;
    private final long firstEpochDay;
    public DailyValues(T[] timestamps, V[] values, LocalDate inclusiveEndDate, Function<T,Long> toEpochDay) {
        this.timestamps = timestamps;
        this.values = values;
        this.toEpochDay = toEpochDay;
        this.inclusiveEndDate = inclusiveEndDate;
        this.firstEpochDay = toEpochDay.apply(timestamps[0]);
    }
    public V value(LocalDate date) {
        if (date.isAfter(inclusiveEndDate))
            throw new IllegalArgumentException("Given date " + date + " is after the inclusive end date " + inclusiveEndDate);
        final long epochDay = date.toEpochDay();
        for (int i = (int)(epochDay - firstEpochDay); i >= 0; i--) {
            if (i < timestamps.length) {
                long currentEpochDay = toEpochDay.apply(timestamps[i]);
                if (epochDay < currentEpochDay && epochDay >= toEpochDay.apply(timestamps[i-1]))
                    return values[i-1];
                else if (epochDay == currentEpochDay)
                    return values[i];
            } else {
                return values[values.length-1];
            }
        }
        throw new IllegalStateException();
    }
}
