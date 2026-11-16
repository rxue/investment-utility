package io.github.rxue.investmentutility.v2.fx.ecb;

import io.github.rxue.investmentutility.v2.util.RequestSender;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.LocalDate;

class EXRDataFetcher {
    private static final String EXR_DATA_URL = "https://data-api.ecb.europa.eu/service/data/EXR/D.%s.EUR.SP00.A";
    private final RequestSender requestSender;

    public EXRDataFetcher(HttpClient httpClient) {
        this.requestSender = new RequestSender(httpClient);
    }

    EXRData fetch(String currency, LocalDate startPeriod, LocalDate endPeriod) throws InterruptedException, IOException {
        URI uri = URI.create(String.format(EXR_DATA_URL, currency)
                + "?startPeriod=" + startPeriod + "&endPeriod=" + endPeriod + "&format=jsondata");
        HttpResponse<String> response = requestSender.send(uri);
        if (response.statusCode() != 200) {
            throw new IllegalStateException("ECB request for " + uri + " failed with status " + response.statusCode() + ": " + response.body());
        }
        //return getLatestRate(objectMapper.readTree(response.body()));
        throw new UnsupportedOperationException();
    }
}