package io.github.rxue.investmentutility.v2.fx.ecb;

import io.github.rxue.investmentutility.v2.util.JsonContentConverter;
import io.github.rxue.investmentutility.v2.util.RequestSender;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.LocalDate;

class EcbExchangeRatesFetcher {
    private static final String EXR_DATA_URL = "https://data-api.ecb.europa.eu/service/data/EXR/D.%s.EUR.SP00.A";
    private final RequestSender requestSender;
    private final JsonContentConverter<ExchangeRates> exchangeRatesConverter;
    EcbExchangeRatesFetcher(HttpClient httpClient, ObjectMapper objectMapper) {
        this.requestSender = new RequestSender(httpClient);
        this.exchangeRatesConverter = new ExchangeRatesConverter(objectMapper);
    }

    ExchangeRates fetch(String currency, LocalDate startPeriod, LocalDate endPeriod) throws InterruptedException, IOException {
        URI uri = URI.create(String.format(EXR_DATA_URL, currency)
                + "?startPeriod=" + startPeriod + "&endPeriod=" + endPeriod + "&format=jsondata");
        HttpResponse<String> response = requestSender.send(uri);
        return exchangeRatesConverter.to(response.body());
    }
}