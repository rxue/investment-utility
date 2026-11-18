package io.github.rxue.investmentutility.v2.marketquote.yahoofinance.chart;

import io.github.rxue.investmentutility.v2.marketquote.PriceSeriesFetcher;
import io.github.rxue.investmentutility.v2.marketquote.vo.price.ClosePriceSeries;
import io.github.rxue.investmentutility.v2.util.Result;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class YahooFinancePriceSeriesFetcher implements PriceSeriesFetcher {
    private final Query1V8PriceChartFetcher chartFetcher;
    public YahooFinancePriceSeriesFetcher(Query1V8PriceChartFetcher chartFetcher) {
        this.chartFetcher = chartFetcher;
    }

    @Override
    public Result<ClosePriceSeries> fetchDaily(String symbol, LocalDate fromInclusive, LocalDate toInclusive) {
        final Instant start = fromInclusive.atStartOfDay(ZoneOffset.UTC).toInstant();
        final Instant end = toInclusive.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        try {
            Chart chartData = chartFetcher.fetchDaily(symbol, start.getEpochSecond(), end.getEpochSecond());
            return new Result.Success<>(toClosePriceSeries(chartData.result()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new Result.TechnicalError<>("Fetch interrupted", e);
        } catch (IOException e) {
            return new Result.TechnicalError<>("Network connection problem", e);
        }
    }
    private static ClosePriceSeries toClosePriceSeries(Chart.Result chartResult) {
        long[] timestamps = chartResult.timestamp();
        BigDecimal[] closes = chartResult.indicator().quote().close();
        return new ClosePriceSeries(chartResult.meta().currency(), timestamps, closes);
    }

}
