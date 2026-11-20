package io.github.rxue.investmentutility.v2.marketquote.yahoofinance.chart;

import io.github.rxue.investmentutility.v2.marketquote.ClosePriceSeries;
import io.github.rxue.investmentutility.v2.util.DailyValues;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Arrays;

class YahooClosePriceSeries implements ClosePriceSeries {
    private final String currency;
    private final long[] utcTimestamps;
    private final BigDecimal[] values;
    private final DailyValues dailyValues;
    public YahooClosePriceSeries(String currency, long[] utcTimestamps, BigDecimal[] values) {
        this.currency = currency;
        this.utcTimestamps = utcTimestamps;
        this.values = values;
        this.dailyValues = DailyValues.generate(toLocalDates(this.utcTimestamps), this.values);
    }
    /**
     * Each timestamp is the session open of a trading day, converted here to its UTC date.
     * Correct for exchanges whose session opens after 00:00 UTC (Americas, Europe),
     * not for e.g. Sydney or Tokyo, where the trading date requires the exchange time zone.
     */
    private static LocalDate[] toLocalDates(long[] utcTimeStamps) {
        return Arrays.stream(utcTimeStamps)
                .mapToObj(timestamp -> LocalDate.ofInstant(Instant.ofEpochSecond(timestamp), ZoneOffset.UTC))
                .toArray(LocalDate[]::new);
    }
    @Override
    public String currency() {
        return currency;
    }
    @Override
    public BigDecimal price(LocalDate date) {
        return dailyValues.get(date);
    }
}
