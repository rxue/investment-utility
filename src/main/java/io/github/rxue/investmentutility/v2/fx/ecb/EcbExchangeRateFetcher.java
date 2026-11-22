package io.github.rxue.investmentutility.v2.fx.ecb;

import io.github.rxue.investmentutility.v2.util.JsonContentConverter;
import io.github.rxue.investmentutility.v2.util.RequestSender;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

class EcbExchangeRateFetcher {
    private final RequestSender requestSender;
    private final JsonContentConverter<ExchangeRates> exchangeRatesConverter;
    EcbExchangeRateFetcher(HttpClient httpClient, ObjectMapper objectMapper) {
        this.requestSender = new RequestSender(httpClient);
        this.exchangeRatesConverter = new ExchangeRatesConverter(objectMapper);
    }

    /**
     * fetch the rate from euro to the given currency
     *
     * @param currency
     * @param startPeriod
     * @param endPeriod
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    ExchangeRates fetch(String currency, LocalDate startPeriod, LocalDate endPeriod) throws InterruptedException, IOException {
        URI uri = URI.create(String.format("https://data-api.ecb.europa.eu/service/data/EXR/D.%s.EUR.SP00.A", currency)
                + "?startPeriod=" + startPeriod + "&endPeriod=" + endPeriod + "&format=jsondata");
        HttpResponse<String> response = requestSender.send(uri);
        return exchangeRatesConverter.to(response.body());
    }
    private class ExchangeRatesConverter implements JsonContentConverter<ExchangeRates> {
        private final ObjectMapper objectMapper;

        public ExchangeRatesConverter(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public ExchangeRates to(String jsonContent) {
            final JsonNode root = objectMapper.readTree(jsonContent);
            JsonNode observationsNode = root.at("/dataSets/0/series/0:0:0:0:0/observations");
            Map<String, Double[]> observations = objectMapper
                    .readerFor(new TypeReference<Map<String, Double[]>>() {})
                    .readValue(observationsNode);
            ExchangeRates.DataSet dataSet = new ExchangeRates.DataSet(observations);
            //get date observation from structure
            JsonNode structureValuesNode = root.at("/structure/dimensions/observation/0/values");
            List<ExchangeRates.Structure.ObservationValue> observationValues = structureValuesNode.valueStream()
                    .map(value -> LocalDate.parse(value.path("id").asString()))
                    .map(ExchangeRates.Structure.ObservationValue::new)
                    .toList();
            ExchangeRates.Structure structure = new ExchangeRates.Structure(observationValues);
            return new ExchangeRates(dataSet, structure);
        }
    }
}