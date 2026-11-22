package io.github.rxue.investmentutility.v2.fx.ecb;

import io.github.rxue.investmentutility.v2.fx.FxRateFetcher;
import io.github.rxue.investmentutility.v2.util.DailyValues;
import io.github.rxue.investmentutility.v2.util.Result;
import io.github.rxue.investmentutility.v2.vo.InclusiveDateRange;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.LocalDate;
import java.util.Map;

import static io.github.rxue.investmentutility.v2.util.Constants.BACKWARD_DAYS;

public class EcbFxRateFetcher implements FxRateFetcher {
    private final EcbExchangeRateFetcher exchangeRateFetcher;
    public EcbFxRateFetcher(HttpClient httpClient, ObjectMapper objectMapper) {
        exchangeRateFetcher = new EcbExchangeRateFetcher(httpClient, objectMapper);
    }

    @Override
    public Result<DailyValues<Double>> fetchRateFromEuroTo(String quoteCurrency, InclusiveDateRange inclusiveDateRange) {
        try {
            LocalDate startDate = inclusiveDateRange.from();
            ExchangeRates exchangeRates = exchangeRateFetcher.fetch(quoteCurrency, startDate.minusDays(BACKWARD_DAYS), inclusiveDateRange.to());
            return new Result.Success<>(toValueSeries(exchangeRates, inclusiveDateRange.to()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new Result.TechnicalError<>("Task canceled", e);
        } catch (IOException e) {
            return new Result.TechnicalError<>("Network connection error", e);
        }
    }
    private DailyValues<Double> toValueSeries(ExchangeRates exchangeRates, LocalDate inclusiveEndDate) {
        LocalDate[] dates = exchangeRates.structure().observationValues().stream()
                .map(ExchangeRates.Structure.ObservationValue::id)
                .toArray(LocalDate[]::new);
        Map<String,Double[]> series = exchangeRates.dataSet().seriesObservations();
        Double[] values = new Double[series.size()];
        for (int i = 0; i < series.size(); i++) {
            values[i] = series.get(i + "")[0];
        }
        return DailyValues.of(dates, values, inclusiveEndDate, LocalDate::toEpochDay);
    }

}
