package io.github.rxue.investmentutility.v2.marketquote.yahoofinance.chart;

import io.github.rxue.investmentutility.v2.marketquote.vo.price.ClosePriceSeries;
import io.github.rxue.investmentutility.v2.util.Result;
import io.github.rxue.investmentutility.v2.vo.InclusiveDateRange;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDate;

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
        InclusiveDateRange dateRange = new InclusiveDateRange(LocalDate.of(2026, 1, 2), LocalDate.of(2026, 1, 5));
        Result<ClosePriceSeries> result = fetcher.fetchDaily("PFE", dateRange);

        ClosePriceSeries series = switch (result) {
            case Result.Success<ClosePriceSeries>(ClosePriceSeries value) -> value;
            default -> fail("expected a success but got " + result);
        };
        assertEquals("USD", series.currency());
        // the keys are the epoch seconds of the session opens, 9:30 New York time
        assertEquals(series.get(LocalDate.of(2026, 1, 2)), new BigDecimal("25.18"));
        assertEquals(series.get(LocalDate.of(2026, 1, 5)), new BigDecimal("25.17"));
    }
    @Test
    void fetchDaily_start_date_is_last_easter_holiday() {
        // Friday 2.1.2026 and Monday 5.1.2026, the weekend in between has no close
        InclusiveDateRange dateRange = new InclusiveDateRange(LocalDate.of(2026, 4, 4), LocalDate.of(2026, 4, 7));
        Result<ClosePriceSeries> result = fetcher.fetchDaily("PFE", dateRange);

        ClosePriceSeries series = switch (result) {
            case Result.Success<ClosePriceSeries>(ClosePriceSeries value) -> value;
            default -> fail("expected a success but got " + result);
        };
        assertEquals("USD", series.currency());
        // the keys are the epoch seconds of the session opens, 9:30 New York time
        assertEquals(series.get(LocalDate.of(2026, 1, 2)), new BigDecimal("25.18"));
        assertEquals(series.get(LocalDate.of(2026, 1, 5)), new BigDecimal("25.17"));
    }
}
