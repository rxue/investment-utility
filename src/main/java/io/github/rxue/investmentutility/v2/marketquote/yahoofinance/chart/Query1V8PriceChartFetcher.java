package io.github.rxue.investmentutility.v2.marketquote.yahoofinance.chart;

import io.github.rxue.investmentutility.v2.util.JsonContentConverter;
import io.github.rxue.investmentutility.v2.util.RequestSender;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

class Query1V8PriceChartFetcher {
    private static final String URL_PREFIX = "https://query1.finance.yahoo.com/v8/finance/chart/";

    private final RequestSender requestSender;
    private final ChartDataConverter chartDataConverter;
    public Query1V8PriceChartFetcher(HttpClient httpClient, ObjectMapper objectMapper) {
        this.requestSender = new RequestSender(httpClient);
        this.chartDataConverter = new ChartDataConverter(objectMapper);
    }

    /**
     * @param period1
     * @param period2
     * @return the daily timestamps and close prices, oldest first; the close of a day without trades is null
     */
    Chart fetchDaily(String yahooTickerSymbol, long period1, long period2) throws InterruptedException, IOException {
        URI uri = URI.create(URL_PREFIX + URLEncoder.encode(yahooTickerSymbol, StandardCharsets.UTF_8)
                + "?period1=" + period1 + "&period2=" + period2 + "&interval=1d");
        HttpResponse<String> response = requestSender.send(uri);
        return chartDataConverter.to(response.body());
    }

    private static class ChartDataConverter implements JsonContentConverter<Chart> {
        private final ObjectMapper objectMapper;
        ChartDataConverter(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
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

            JsonNode indicators = result.path("indicators");
            JsonNode closeNodes = indicators.path("quote").path(0).path("close");
            BigDecimal[] closes = new BigDecimal[closeNodes.size()];
            for (int i = 0; i < closes.length; i++) {
                JsonNode close = closeNodes.path(i);
                // the close is null for a day without trades; it is kept as null so that it stays aligned with its timestamp
                closes[i] = close.isNumber() ? close.decimalValue().setScale(scale, RoundingMode.HALF_UP) : null;
            }
            return new Chart.Result(new Chart.Meta(meta.path("currency").asString(), meta.path("regularMarketTime").asLong()),
                    timestamps,
                    new Chart.Indicators(new Chart.Quote(closes), parseAdjustedClosePrices(indicators.path("adjclose"))));
        }
        /**
         * @param adjCloses the {@code indicators.adjclose} node, shaped {@code [ { "adjclose": [ ... ] } ] }
         * @return the split- and dividend-adjusted closes at full precision, null for a day without trades so that
         * they stay aligned with their timestamps; empty if the response has no adjusted closes
         */
        private static double[] parseAdjustedClosePrices(JsonNode adjCloses) {
            JsonNode adjCloseNodes = adjCloses.path(0).path("adjclose");
            double[] adjustedCloses = new double[adjCloseNodes.size()];
            for (int i = 0; i < adjustedCloses.length; i++) {
                JsonNode adjClose = adjCloseNodes.path(i);
                adjustedCloses[i] = adjClose.isNumber() ? adjClose.doubleValue() : null;
            }
            return adjustedCloses;
        }
    }
}
