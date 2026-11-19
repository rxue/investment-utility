package io.github.rxue.investmentutility.v2.fx.ecb;

import io.github.rxue.investmentutility.v2.fx.FxRateFetcher;
import io.github.rxue.investmentutility.v2.fx.FxRateSeries;
import io.github.rxue.investmentutility.v2.util.Result;
import io.github.rxue.investmentutility.v2.vo.InclusiveDateRange;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.time.LocalDate;
import java.util.Map;

public class EcbFxRateFetcher implements FxRateFetcher {
    private final EcbExchangeRateFetcher exchangeRateFetcher;
    public EcbFxRateFetcher(HttpClient httpClient, ObjectMapper objectMapper) {
        exchangeRateFetcher = new EcbExchangeRateFetcher(httpClient, objectMapper);
    }

    @Override
    public Result<FxRateSeries> fetchRateFromEuroTo(String quoteCurrency, InclusiveDateRange inclusiveDateRange) {
        try {
            LocalDate startDate = inclusiveDateRange.from();
            ExchangeRates exchangeRates = exchangeRateFetcher.fetch(quoteCurrency, startDate.minusDays(10), inclusiveDateRange.to());
            return new Result.Success<>(toFxRateSeries(quoteCurrency, exchangeRates));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new Result.TechnicalError<>("Task canceled", e);
        } catch (IOException e) {
            return new Result.TechnicalError<>("Network connection error", e);
        }
    }
    private static FxRateSeries toFxRateSeries(String quoteCurrency, ExchangeRates exchangeRates) {
        LocalDate[] dates = exchangeRates.structure().observationValues().stream()
                .map(ExchangeRates.Structure.ObservationValue::id)
                .toArray(LocalDate[]::new);
        Map<String,BigDecimal[]> series = exchangeRates.dataSet().seriesObservations();
        BigDecimal[] values = new BigDecimal[series.size()];
        for (int i = 0; i < series.size(); i++) {
            values[i] = series.get(i + "")[0];
        }
        return new EcbFxRateSeries(quoteCurrency, dates, values);
    }

}
