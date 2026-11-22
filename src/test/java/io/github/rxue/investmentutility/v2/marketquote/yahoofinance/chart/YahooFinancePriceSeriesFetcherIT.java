package io.github.rxue.investmentutility.v2.marketquote.yahoofinance.chart;

import io.github.rxue.investmentutility.v2.fx.ecb.EcbFxRateFetcher;
import io.github.rxue.investmentutility.v2.marketquote.DailyPriceSeries;
import io.github.rxue.investmentutility.v2.util.Result;
import io.github.rxue.investmentutility.v2.vo.InclusiveDateRange;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class YahooFinancePriceSeriesFetcherIT {
    // Yahoo computes adjusted closes in float32, the last digits vary between requests
    private static final double DELTA = .00001;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final YahooPriceSeriesFetcher fetcher =
            new YahooPriceSeriesFetcher(new Query1V8PriceChartFetcher(httpClient, objectMapper),
                    new EcbFxRateFetcher(httpClient, objectMapper));

    @Test
    void fetchClosePrices_2_trading_days() {
        // Friday 2.1.2026 and Monday 5.1.2026, the weekend in between has no close
        InclusiveDateRange dateRange = new InclusiveDateRange(LocalDate.of(2026, 1, 2), LocalDate.of(2026, 1, 5));
        Result<DailyPriceSeries<Long>> result = fetcher.fetchClosePrices("PFE", dateRange);

        DailyPriceSeries<Long> series = switch (result) {
            case Result.Success<DailyPriceSeries<Long>>(DailyPriceSeries<Long> value) -> value;
            default -> fail("expected a success but got " + result);
        };
        assertEquals("USD", series.currency());
        // the keys are the epoch seconds of the session opens, 9:30 New York time; prices in cents
        assertEquals(2518L, series.value(LocalDate.of(2026, 1, 2)));
        assertEquals(2517L, series.value(LocalDate.of(2026, 1, 5)));
    }
    @Test
    void fetchClosePrices_start_date_is_last_easter_holiday() {
        // Saturday 4.4.2026 and Sunday 5.4.2026 fall back to Thursday 2.4.2026, Good Friday 3.4.2026 has no session
        InclusiveDateRange dateRange = new InclusiveDateRange(LocalDate.of(2026, 4, 4), LocalDate.of(2026, 4, 7));
        Result<DailyPriceSeries<Long>> result = fetcher.fetchClosePrices("PFE", dateRange);

        DailyPriceSeries<Long> series = switch (result) {
            case Result.Success<DailyPriceSeries<Long>>(DailyPriceSeries<Long> value) -> value;
            default -> fail("expected a success but got " + result);
        };
        assertEquals("USD", series.currency());
        // the keys are the epoch seconds of the session opens, 9:30 New York time; prices in cents
        assertEquals(2832L, series.value(LocalDate.of(2026, 4, 4)));
        assertEquals(2832L, series.value(LocalDate.of(2026, 4, 5)));
        assertEquals(2783L, series.value(LocalDate.of(2026, 4, 6)));
        assertEquals(2710L, series.value(LocalDate.of(2026, 4, 7)));
    }
    @Test
    void fetchClosePricesAdjusted_2_trading_days() {
        // Friday 2.1.2026 and Monday 5.1.2026, the weekend in between has no close
        InclusiveDateRange dateRange = new InclusiveDateRange(LocalDate.of(2026, 1, 2), LocalDate.of(2026, 1, 5));
        Result<DailyPriceSeries<Double>> result = fetcher.fetchAdjustedClosePrices("PFE", dateRange);

        DailyPriceSeries<Double> series = switch (result) {
            case Result.Success<DailyPriceSeries<Double>>(DailyPriceSeries<Double> value) -> value;
            default -> fail("expected a success but got " + result);
        };
        assertEquals("USD", series.currency());
        assertEquals(23.59955, series.value(LocalDate.of(2026, 1, 2)), DELTA);
        assertEquals(23.59017, series.value(LocalDate.of(2026, 1, 5)), DELTA);
    }
    @Test
    void fetchClosePricesAdjusted_start_date_is_last_easter_holiday() {
        // Saturday 4.4.2026 and Sunday 5.4.2026 fall back to Thursday 2.4.2026, Good Friday 3.4.2026 has no session
        InclusiveDateRange dateRange = new InclusiveDateRange(LocalDate.of(2026, 4, 4), LocalDate.of(2026, 4, 7));
        Result<DailyPriceSeries<Double>> result = fetcher.fetchAdjustedClosePrices("PFE", dateRange);

        DailyPriceSeries<Double> series = switch (result) {
            case Result.Success<DailyPriceSeries<Double>>(DailyPriceSeries<Double> value) -> value;
            default -> fail("expected a success but got " + result);
        };
        assertEquals("USD", series.currency());
        assertEquals(26.98707, series.value(LocalDate.of(2026, 4, 4)), DELTA);
        assertEquals(26.98707, series.value(LocalDate.of(2026, 4, 5)), DELTA);
        assertEquals(26.52014, series.value(LocalDate.of(2026, 4, 6)), DELTA);
        assertEquals(25.82450, series.value(LocalDate.of(2026, 4, 7)), DELTA);
    }
}
