package io.github.rxue.investmentutility.v2.fx.ecb;

import io.github.rxue.investmentutility.v2.util.JsonContentConverter;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

class ExchangeRatesConverter implements JsonContentConverter<ExchangeRates> {
    private final ObjectMapper objectMapper;

    public ExchangeRatesConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ExchangeRates to(String jsonContent) {
        final JsonNode root = objectMapper.readTree(jsonContent);
        JsonNode observationsNode = root.at("/dataSets/0/series/0:0:0:0:0/observations");
        Map<String,BigDecimal[]> observations = objectMapper
                .readerFor(new TypeReference<Map<String, BigDecimal[]>>() {})
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
