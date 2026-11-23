package io.github.rxue.investmentutility.v2.marketquote.yahoofinance.chart;

import io.github.rxue.investmentutility.v2.fx.ecb.EcbFxRateFetcher;
import io.github.rxue.investmentutility.v2.marketquote.DailyPriceSeries;
import io.github.rxue.investmentutility.v2.util.DailyValue;
import io.github.rxue.investmentutility.v2.util.Result;
import io.github.rxue.investmentutility.v2.vo.InclusiveDateRange;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

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
    void fetchClosePrices_InCent_2_trading_days() {
        // Friday 2.1.2026 and Monday 5.1.2026, the weekend in between has no close
        InclusiveDateRange dateRange = new InclusiveDateRange(LocalDate.of(2026, 1, 2), LocalDate.of(2026, 1, 5));
        Result<DailyPriceSeries<Long>> result = fetcher.fetchClosePricesInCent("PFE", dateRange);

        DailyPriceSeries<Long> series = switch (result) {
            case Result.Success<DailyPriceSeries<Long>>(DailyPriceSeries<Long> value) -> value;
            default -> fail("expected a success but got " + result);
        };
        assertEquals("USD", series.currency());
        // the keys are the epoch seconds of the session opens, 9:30 New York time; prices in cent
        List<DailyValue<Long>> dailyValueList = series.dailyValues();

        assertEquals(new DailyValue<>(LocalDate.of(2026, 1, 2),2518L), dailyValueList.get(0));
        assertEquals(new DailyValue<>(LocalDate.of(2026, 1, 5),2517L), dailyValueList.get(1));
    }

}
