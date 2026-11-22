package io.github.rxue.investmentutility.v2.util;

import java.time.LocalDate;
import java.util.function.Function;

public interface DailyValues<V> {
    V value(LocalDate date);
    static <T,V> DailyValues<V> of(T[] timestamps, V[] values, LocalDate inclusiveEndDate, Function<T,Long> toEpochDay) {
        return new DailyValuesImpl<>(timestamps, values, inclusiveEndDate, toEpochDay);
    }
}
