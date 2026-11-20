package io.github.rxue.investmentutility.v2.marketquote.yahoofinance.chart;

import io.github.rxue.investmentutility.v2.fx.ecb.EcbFxRateFetcher;
import io.github.rxue.investmentutility.v2.marketquote.ClosePriceSeries;
import io.github.rxue.investmentutility.v2.marketquote.DualCurrencyClosePriceSeries;
import io.github.rxue.investmentutility.v2.util.Result;
import io.github.rxue.investmentutility.v2.vo.InclusiveDateRange;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

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
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final YahooFinancePriceSeriesFetcher fetcher =
            new YahooFinancePriceSeriesFetcher(new Query1V8PriceChartFetcher(httpClient, objectMapper),
                    new EcbFxRateFetcher(httpClient, objectMapper));

    @Test
    void fetchDaily_2_trading_days() {
        // Friday 2.1.2026 and Monday 5.1.2026, the weekend in between has no close
        InclusiveDateRange dateRange = new InclusiveDateRange(LocalDate.of(2026, 1, 2), LocalDate.of(2026, 1, 5));
        Result<ClosePriceSeries> result = fetcher.fetchDaily("PFE", dateRange);

        ClosePriceSeries series = switch (result) {
            case Result.Success<ClosePriceSeries>(ClosePriceSeries value) -> value;
            default -> fail("expected a success but got " + result);
        };
        assertEquals("USD", series.currency());
        // the keys are the epoch seconds of the session opens, 9:30 New York time
        assertEquals(new BigDecimal("25.18"), series.price(LocalDate.of(2026, 1, 2)));
        assertEquals(new BigDecimal("25.17"), series.price(LocalDate.of(2026, 1, 5)));
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
        assertEquals(new BigDecimal("28.32"), series.price(LocalDate.of(2026, 4, 4)));
        assertEquals(new BigDecimal("28.32"), series.price(LocalDate.of(2026, 4, 5)));
        assertEquals(new BigDecimal("27.83"), series.price(LocalDate.of(2026, 4, 6)));
        assertEquals(new BigDecimal("27.10"), series.price(LocalDate.of(2026, 4, 7)));
    }
    @Test
    void fetchDailyInEuro_with_currency_and_2_trading_days() {
        // Friday 2.1.2026 and Monday 5.1.2026, the weekend in between has no close
        InclusiveDateRange dateRange = new InclusiveDateRange(LocalDate.of(2026, 1, 2), LocalDate.of(2026, 1, 5));
        Result<DualCurrencyClosePriceSeries> result = fetcher.fetchDailyInEuro("PFE", dateRange);

        DualCurrencyClosePriceSeries series = switch (result) {
            case Result.Success<DualCurrencyClosePriceSeries>(DualCurrencyClosePriceSeries value) -> value;
            default -> fail("expected a success but got " + result);
        };
        assertEquals("USD", series.currency());
        assertEquals("EUR", series.convertedCurrency());
        // the keys are the epoch seconds of the session opens, 9:30 New York time
        assertEquals(new BigDecimal("25.18"), series.price(LocalDate.of(2026, 1, 2)));
        assertEquals(new BigDecimal("25.18"), series.price(LocalDate.of(2026, 1, 3)));
        assertEquals(new BigDecimal("25.18"), series.price(LocalDate.of(2026, 1, 4)));
        assertEquals(new BigDecimal("25.17"), series.price(LocalDate.of(2026, 1, 5)));
        assertEquals(new BigDecimal("21.4828"), series.convertedPrice(LocalDate.of(2026, 1, 2)));
        assertEquals(new BigDecimal("21.4828"), series.convertedPrice(LocalDate.of(2026, 1, 3)));
        assertEquals(new BigDecimal("21.4828"), series.convertedPrice(LocalDate.of(2026, 1, 4)));
        assertEquals(new BigDecimal("21.5792"), series.convertedPrice(LocalDate.of(2026, 1, 5)));
    }
    @Test
    void fetchDailyInEuro_with_euro_as_original_currency() {
        // Friday 2.1.2026 and Monday 5.1.2026, the weekend in between has no close
        InclusiveDateRange dateRange = new InclusiveDateRange(LocalDate.of(2026, 1, 2), LocalDate.of(2026, 1, 5));
        Result<DualCurrencyClosePriceSeries> result = fetcher.fetchDailyInEuro("ELISA.HE", dateRange);

        DualCurrencyClosePriceSeries series = switch (result) {
            case Result.Success<DualCurrencyClosePriceSeries>(DualCurrencyClosePriceSeries value) -> value;
            default -> fail("expected a success but got " + result);
        };
        assertEquals("EUR", series.currency());
        assertEquals("EUR", series.convertedCurrency());
        assertEquals(new BigDecimal("37.12"), series.price(LocalDate.of(2026, 1, 2)));
        assertEquals(new BigDecimal("37.12"), series.convertedPrice(LocalDate.of(2026, 1, 2)));
    }
}
