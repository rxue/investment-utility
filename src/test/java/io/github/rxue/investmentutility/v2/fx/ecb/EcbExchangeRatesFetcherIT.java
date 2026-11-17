package io.github.rxue.investmentutility.v2.fx.ecb;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EcbExchangeRatesFetcherIT {
    private final HttpClient httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final EcbExchangeRatesFetcher fetcher = new EcbExchangeRatesFetcher(httpClient, new ObjectMapper());

    @Test
    void fetch_3_days() throws IOException, InterruptedException {
        LocalDate today = LocalDate.now();

        ExchangeRates exchangeRates = fetcher.fetch("USD", today.minusDays(3), today);

        ExchangeRates.DataSet actualDataSet = exchangeRates.dataSet();
        assertNotNull(actualDataSet, "data set");
        Map<String,BigDecimal[]> observations = actualDataSet.seriesObservations();
        assertNotNull(observations, "series observations");
        assertTrue(observations.size() > 0 && observations.size() <=3);
        assertTrue(observations.size() == exchangeRates.structure().observationValues().size());

    }
}
