package io.github.rxue.investmentutility.v2.fx.ecb;

import io.github.rxue.investmentutility.v2.fx.FxRateSeries;
import io.github.rxue.investmentutility.v2.util.Result;
import io.github.rxue.investmentutility.v2.vo.InclusiveDateRange;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EcbFxRateFetcherIT {
    private final HttpClient httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final EcbFxRateFetcher fetcher = new EcbFxRateFetcher(httpClient, new ObjectMapper());

    @Test
    void fetch_all_6_days_with_saturday_as_the_last_day() {
        LocalDate date = LocalDate.of(2026,1,5);

        Result<FxRateSeries> result = fetcher.fetchRateFromEuroTo("USD", new InclusiveDateRange(date, date.plusDays(5)));
        if (result instanceof Result.TechnicalError<FxRateSeries>) {
            fail("Unexpected execution failure");
        }
        Result.Success<FxRateSeries> success = assertInstanceOf(Result.Success.class, result, () -> "unexpected result: " + result);
        FxRateSeries fxRateSeries= success.value();
        assertEquals(new BigDecimal("1.1664"), fxRateSeries.get(LocalDate.of(2026,1,5)));
        assertEquals(new BigDecimal("1.1707"), fxRateSeries.get(LocalDate.of(2026,1,6)));
        assertEquals(new BigDecimal("1.1684"), fxRateSeries.get(LocalDate.of(2026,1,7)));
        assertEquals(new BigDecimal("1.1675"), fxRateSeries.get(LocalDate.of(2026,1,8)));
        BigDecimal fridayFxRate = new BigDecimal("1.1642");
        assertEquals(fridayFxRate, fxRateSeries.get(LocalDate.of(2026,1,9)));
        assertEquals(fridayFxRate, fxRateSeries.get(LocalDate.of(2026,1,10)));

    }
    @Test
    void fetch_first_day_is_the_last_eastern_holiday() {
        LocalDate easterMonday = LocalDate.of(2026,4,6);

        Result<FxRateSeries> result = fetcher.fetchRateFromEuroTo("USD", new InclusiveDateRange(easterMonday, easterMonday.plusDays(1)));
        if (result instanceof Result.TechnicalError<FxRateSeries>) {
            fail("Unexpected execution failure");
        }
        Result.Success<FxRateSeries> success = assertInstanceOf(Result.Success.class, result, () -> "unexpected result: " + result);
        FxRateSeries fxRateSeries= success.value();
        BigDecimal fxRateOnTheDayBeforeEaster = new BigDecimal("1.1525");
        assertEquals(fxRateOnTheDayBeforeEaster, fxRateSeries.get(LocalDate.of(2026,4,6)));

    }
}
