package io.github.rxue.investmentutility.fx.ecb;

import io.github.rxue.investmentutility.vo.Pair;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ECBFxRateFetcherIT {
    private final HttpClient httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ECBFxRateFetcher fetcher = new ECBFxRateFetcher(httpClient);

    @Test
    void fetchRate_publishingDay() {
        LocalDate tuesday = LocalDate.of(2026, 9, 1);

        Pair<LocalDate,BigDecimal> rate = fetcher.fetchRate("EUR", "USD", tuesday);

        assertEquals(tuesday, rate.left());
        assertEquals(0, new BigDecimal("1.159").compareTo(rate.right()), "rate was " + rate.right());
    }
}
