package io.github.rxue.investmentutility.v2.fx.ecb;

import io.github.rxue.investmentutility.v2.fx.FxRateSeries;
import io.github.rxue.investmentutility.v2.util.Result;
import io.github.rxue.investmentutility.v2.vo.InclusiveDateRange;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

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
    void fetch_3_days() {
        LocalDate today = LocalDate.now();

        Result<FxRateSeries> result = fetcher.fetchRateFromEuroTo("USD", new InclusiveDateRange(today.minusDays(7), today));

        Result.Success<FxRateSeries> success = assertInstanceOf(Result.Success.class, result, () -> "unexpected result: " + result);
        assertNotNull(success.value());
    }
}
