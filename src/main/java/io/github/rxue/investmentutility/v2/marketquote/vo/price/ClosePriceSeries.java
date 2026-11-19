package io.github.rxue.investmentutility.v2.marketquote.vo.price;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ClosePriceSeries {
    private final String currency;
    private final long startTimestamp;
    private final long[] utcTimestamps;
    private final BigDecimal[] values;
    private final Map<Long,BigDecimal> valueMap;
    public ClosePriceSeries(String currency, long[] utcTimestamps, BigDecimal[] values) {
        this.currency = currency;
        this.utcTimestamps = utcTimestamps;
        this.startTimestamp = utcTimestamps[0];
        this.values = values;
        this.valueMap = toValueMap(utcTimestamps, values);
    }
    public String currency() {
        return currency;
    }

    /**
     * @return the close of each trading day keyed by the day's UTC epoch second, unmodifiable
     */
    public Map<Long,BigDecimal> valueMap() {
        return valueMap;
    }

    private long utcTimestamp(LocalDate date) {
        long secondsPassed = startTimestamp % (24*60*60);
        Instant startOfTheDay = date.atStartOfDay(ZoneOffset.UTC).toInstant();
        return startOfTheDay.plusSeconds(secondsPassed).getEpochSecond();
    }

    public BigDecimal get(LocalDate date) {
        BigDecimal directResult = valueMap.get(utcTimestamp(date));
        if (directResult == null) {
            final LocalDate startDate = LocalDate.ofInstant(Instant.ofEpochSecond(startTimestamp), ZoneOffset.UTC);
            for (LocalDate currentDate = date.minusDays(1); !currentDate.isBefore(startDate); currentDate = currentDate.minusDays(1)) {
                BigDecimal result = valueMap.get(utcTimestamp(currentDate));
                if (result != null) return result;
            }
        }
        return directResult;
    }

    private static Map<Long,BigDecimal> toValueMap(long[] utcTimestamps, BigDecimal[] values) {
        Map<Long,BigDecimal> map = new HashMap<>();
        for (int i = 0; i < utcTimestamps.length; i++) {
            map.put(utcTimestamps[i], values[i]);
        }
        return Collections.unmodifiableMap(map);
    }
}
