package io.github.rxue.investmentutility.v2.marketquote.yahoofinance.chart;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class Query1V8PriceChartFetcherIT {
    private final HttpClient httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final Query1V8PriceChartFetcher fetcher = new Query1V8PriceChartFetcher(httpClient);

    @Test
    void fetchDaily_recentOneDay() throws InterruptedException, IOException {
        // the last 7 calendar days including date, from the start of the first day to the end of date in UTC
        LocalDate startDate = LocalDate.of(2026,11,9);
        final long period1 = startDate.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
        final LocalDate endDate = startDate.plusDays(3);
        final long period2 = endDate.atStartOfDay(ZoneOffset.UTC).toEpochSecond();

        Chart.Result priceChart = fetcher.fetchDaily("PFE", period1, period2)
                .result();

        assertEquals("USD", priceChart.meta().currency());
        Instant regularMarketTime = Instant.ofEpochSecond(priceChart.meta().regularMarketTime());
        assertTrue(regularMarketTime.isAfter(Instant.now().minus(Duration.ofDays(7))),
                "regular market time should be within the last week but was " + regularMarketTime);
        long[] timestamps = priceChart.timestamp();
        // 7 calendar days hold 5 trading days, fewer when they include exchange holidays
        assertTrue(timestamps.length > 0 && timestamps.length <= 1, "there should be 1-7 timestamps but were " + timestamps.length);
        for (long timestamp : timestamps) {
            assertTrue(timestamp >= period1 && timestamp < period2,
                    "timestamp " + Instant.ofEpochSecond(timestamp) + " is outside the requested period");
        }
        BigDecimal[] closes = priceChart.indicator().quote().close();
        assertEquals(timestamps.length, closes.length, "price series has to correspond to timestamps");
    }
    @Test
    void fetchDaily_from_future() throws InterruptedException, IOException {
        // the last 7 calendar days including today, from the start of the first day to the end of today in UTC
        LocalDate today = LocalDate.of(3026,1,2);
        final long period1 = today.minusDays(2).atStartOfDay(ZoneOffset.UTC).toEpochSecond();
        final long period2 = today.minusDays(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond();

        Chart priceChart = fetcher.fetchDaily("PFE", period1, period2);

        assertNull(priceChart.result());
    }
}
