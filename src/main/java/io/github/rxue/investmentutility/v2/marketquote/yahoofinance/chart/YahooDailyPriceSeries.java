package io.github.rxue.investmentutility.v2.marketquote.yahoofinance.chart;

import io.github.rxue.investmentutility.v2.marketquote.DailyPriceSeries;
import io.github.rxue.investmentutility.v2.util.DailyValue;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class YahooDailyPriceSeries<T> implements DailyPriceSeries<T> {
    private final String currency;
    private final long[] utcTimestamps;
    private final T[] values;
    public YahooDailyPriceSeries(String currency, long[] utcTimestamps, T[] values) {
        this.currency = currency;
        this.utcTimestamps = utcTimestamps;
        this.values = values;
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
    public List<DailyValue<T>> dailyValues() {
        List<DailyValue<T>> result = new ArrayList<>();
        for (int i = 0; i < utcTimestamps.length; i++) {
            long epochDay = getEpochDay(utcTimestamps[i]);
            result.add(new DailyValue<>(LocalDate.ofEpochDay(epochDay), values[i]));
        }
        return Collections.unmodifiableList(result);
    }

}
