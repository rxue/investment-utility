package io.github.rxue.investmentutility.v2.marketquote.yahoofinance.chart;

import io.github.rxue.investmentutility.v2.util.RequestSender;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

class Query1V8PriceChartFetcher {
    private static final String URL_PREFIX = "https://query1.finance.yahoo.com/v8/finance/chart/";

    private final RequestSender requestSender;
    private final ChartDataConverter chartDataConverter;
    public Query1V8PriceChartFetcher(HttpClient httpClient) {
        this.requestSender = new RequestSender(httpClient);
        this.chartDataConverter = new ChartDataConverter();
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
        return chartDataConverter.toData(response.body());
    }


}
