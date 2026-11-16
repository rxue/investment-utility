package io.github.rxue.investmentutility.v2.marketquote.yahoofinance.chart;

import io.github.rxue.investmentutility.v2.util.FailureReason;
import io.github.rxue.investmentutility.v2.marketquote.PriceSeriesFetcher;
import io.github.rxue.investmentutility.v2.util.Result;
import io.github.rxue.investmentutility.v2.marketquote.vo.price.ClosePrice;
import io.github.rxue.investmentutility.v2.marketquote.vo.price.Value;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class YahooFinancePriceSeriesFetcher implements PriceSeriesFetcher {
    private final Query1V8PriceChartFetcher chartFetcher;
    public YahooFinancePriceSeriesFetcher(Query1V8PriceChartFetcher chartFetcher) {
        this.chartFetcher = chartFetcher;
    }

    @Override
    public Result<List<ClosePrice>> fetchDaily(String symbol, LocalDate fromInclusive, LocalDate toInclusive) {
        final Instant start = fromInclusive.atStartOfDay(ZoneOffset.UTC).toInstant();
        final Instant end = toInclusive.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        try {
            Chart chartData = chartFetcher.fetchDaily(symbol, start.getEpochSecond(), end.getEpochSecond());
            return new Result<>(toClosePriceSeries(chartData.result()), null);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new Result<>(List.of(), FailureReason.CANCELED);
        } catch (IOException e) {
            return new Result<>(List.of(), FailureReason.NETWORK_ERROR);
        }
    }
    private static List<ClosePrice> toClosePriceSeries(Chart.Result chartData) {
        long[] timestamps = chartData.timestamp();
        BigDecimal[] closes = chartData.indicator().quote().close();
        if (timestamps.length != closes.length) {
            throw new IllegalStateException("Got " + timestamps.length + " timestamps but " + closes.length + " closes");
        }
        String currency = chartData.meta().currency();
        List<ClosePrice> closePrices = new ArrayList<>(closes.length);
        for (int i = 0; i < closes.length; i++) {
            // the close is null for a day without trades
            if (closes[i] == null) continue;
            // the timestamp is the epoch second of the trading session's open
            LocalDate date = LocalDate.ofInstant(Instant.ofEpochSecond(timestamps[i]), ZoneOffset.UTC);
            closePrices.add(new ClosePrice(date, new Value(closes[i], currency)));
        }
        return List.copyOf(closePrices);
    }

}
