package io.github.rxue.investmentutility.v2.marketquote.yahoofinance.chart;

import io.github.rxue.investmentutility.v2.marketquote.DailyPriceSeries;
import io.github.rxue.investmentutility.v2.util.DailyValues;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Arrays;

final class YahooDailyPriceSeries<T> implements DailyPriceSeries<T> {
    private final String currency;
    private final DailyValues<T> dailyValues;
    public YahooDailyPriceSeries(String currency, long[] utcTimestamps, T[] values, LocalDate inclusiveEndDate) {
        this.currency = currency;
        this.dailyValues = DailyValues.of(
                Arrays.stream(utcTimestamps).boxed().toArray(Long[]::new),
                values,
                inclusiveEndDate,
                YahooDailyPriceSeries::getEpochDay);
    }

    private static Long getEpochDay(Long utcTimestamp) {
        return LocalDate.ofInstant(Instant.ofEpochSecond(utcTimestamp), ZoneOffset.UTC)
                .toEpochDay();
    }

    @Override
    public String currency() {
        return currency;
    }

    @Override
    public T value(LocalDate date) {
        return dailyValues.value(date);
    }
}
