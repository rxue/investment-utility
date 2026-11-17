package io.github.rxue.investmentutility.v2.marketquote.yahoofinance.chart;

import io.github.rxue.investmentutility.v2.util.JsonContentConverter;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;

class ChartDataConverter implements JsonContentConverter<Chart> {
    private final ObjectMapper objectMapper;
    public ChartDataConverter() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Chart to(String jsonContent) {
        JsonNode chart = objectMapper.readTree(jsonContent).path("chart");
        JsonNode error = chart.path("error");
        if (!error.isMissingNode() && !error.isNull()) {
            String code = error.path("code").asString();
            String description = error.path("description").asString();
            return new Chart(null, new Chart.Error(code, description));
        }
        return new Chart(toResult(chart.path("result").path(0)), null);
    }
    private Chart.Result toResult(JsonNode result) {
        JsonNode meta = result.path("meta");
        // epoch seconds of each trading session's open, at the same index as its close
        long[] timestamps = new long[result.path("timestamp").size()];
        for (int i = 0; i < timestamps.length; i++) {
            timestamps[i] = result.path("timestamp").path(i).asLong();
        }
        // closes arrive as doubles such as 342.3699951171875; priceHint is the number of decimals the price is quoted with
        int scale = meta.path("priceHint").asInt(2);
        JsonNode closeNodes = result.path("indicators").path("quote").path(0).path("close");
        BigDecimal[] closes = new BigDecimal[closeNodes.size()];
        for (int i = 0; i < closes.length; i++) {
            JsonNode close = closeNodes.path(i);
            // the close is null for a day without trades; it is kept as null so that it stays aligned with its timestamp
            closes[i] = close.isNumber() ? close.decimalValue().setScale(scale, RoundingMode.HALF_UP) : null;
        }
        return new Chart.Result(new Chart.Meta(meta.path("currency").asString(), meta.path("regularMarketTime").asLong()),
                timestamps,
                new Chart.Indicators(new Chart.Quote(closes)));
    }
}
