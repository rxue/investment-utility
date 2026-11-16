package io.github.rxue.investmentutility.v2.marketquote.yahoofinance.chart;

import io.github.rxue.investmentutility.v2.RequestSender;
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
    ChartData fetchDaily(String yahooTickerSymbol, long period1, long period2) throws InterruptedException {
        URI uri = chartUri(yahooTickerSymbol, period1, period2);
        HttpResponse<String> response = requestSender.send(uri);
/*        int responseStatusCode = response.statusCode();
        if (responseStatusCode != 200) {
            throw new QuoteFetchException("Yahoo Finance request for " + uri + " failed: ", responseStatusCode);
        }*/
        return chartDataConverter.toData(response.body());
    }


    private static URI chartUri(String yahooTickerSymbol, long period1, long period2) {
        return URI.create(URL_PREFIX + URLEncoder.encode(yahooTickerSymbol, StandardCharsets.UTF_8)
                + "?period1=" + period1 + "&period2=" + period2 + "&interval=1d");
    }
}
