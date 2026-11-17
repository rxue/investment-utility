package io.github.rxue.investmentutility.v2.marketquote.yahoofinance.chart;

import io.github.rxue.investmentutility.v2.marketquote.vo.price.ClosePriceSeries;
import io.github.rxue.investmentutility.v2.util.Result;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class YahooFinancePriceSeriesFetcherIT {
    private final HttpClient httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final YahooFinancePriceSeriesFetcher fetcher =
            new YahooFinancePriceSeriesFetcher(new Query1V8PriceChartFetcher(httpClient));

    @Test
    void fetchDaily_twoTradingDays() {
        // Friday 2.1.2026 and Monday 5.1.2026, the weekend in between has no close
        Result<ClosePriceSeries> result = fetcher.fetchDaily("PFE", LocalDate.of(2026, 1, 2), LocalDate.of(2026, 1, 5));

        ClosePriceSeries series = switch (result) {
            case Result.Success<ClosePriceSeries>(ClosePriceSeries value) -> value;
            default -> fail("expected a success but got " + result);
        };
        assertEquals("USD", series.currency());
        // the keys are the epoch seconds of the session opens, 9:30 New York time
        assertEquals(series.priceOf(LocalDate.of(2026, 1, 2)), new BigDecimal("25.18"));
        assertEquals(series.priceOf(LocalDate.of(2026, 1, 5)), new BigDecimal("25.17"));
    }

    private static long sessionOpen(LocalDate date) {
        return date.atTime(14, 30).toEpochSecond(ZoneOffset.UTC);
    }
}
