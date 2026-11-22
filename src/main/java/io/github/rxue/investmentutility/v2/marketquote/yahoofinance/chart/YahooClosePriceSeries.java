package io.github.rxue.investmentutility.v2.marketquote.yahoofinance.chart;

import io.github.rxue.investmentutility.v2.marketquote.ClosePriceSeries;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

final class YahooClosePriceSeries<T> implements ClosePriceSeries<T> {
    private final String currency;
    private final long[] utcTimestamps;
    private final long firstEpochDay;
    private final T[] values;
    public YahooClosePriceSeries(String currency, long[] utcTimestamps, T[] values) {
        this.currency = currency;
        this.firstEpochDay = getEpochDay(utcTimestamps[0]);
        this.utcTimestamps = utcTimestamps;
        this.values = values;
    }

    private static long getEpochDay(long utcTimestamp) {
        return LocalDate.ofInstant(Instant.ofEpochSecond(utcTimestamp), ZoneOffset.UTC)
                .toEpochDay();
    }

    @Override
    public String currency() {
        return currency;
    }

    @Override
    public T price(LocalDate date) {
        final long epochDay = date.toEpochDay();
        for (int i = (int)(epochDay - firstEpochDay); i >= 0; i--) {
            if (i < utcTimestamps.length) {
                long currentEpochDay = getEpochDay(utcTimestamps[i]);
                if (epochDay < currentEpochDay && epochDay >= getEpochDay(utcTimestamps[i-1]))
                    return values[i-1];
                else if (epochDay == currentEpochDay)
                    return values[i];
            }
        }
        throw new IllegalStateException();
    }
}
