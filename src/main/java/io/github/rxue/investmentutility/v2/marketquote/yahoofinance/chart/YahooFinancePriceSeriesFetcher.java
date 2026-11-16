package io.github.rxue.investmentutility.v2.marketquote.yahoofinance.chart;

import io.github.rxue.investmentutility.v2.marketquote.FailureReason;
import io.github.rxue.investmentutility.v2.marketquote.PriceSeriesFetcher;
import io.github.rxue.investmentutility.v2.marketquote.Result;
import io.github.rxue.investmentutility.v2.marketquote.vo.price.ClosePrice;
import io.github.rxue.investmentutility.v2.marketquote.vo.price.Value;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class YahooFinancePriceSeriesFetcher implements PriceSeriesFetcher {
    private final Query1V8PriceChartFetcher chartFetcher;
    public YahooFinancePriceSeriesFetcher(Query1V8PriceChartFetcher chartFetcher) {
        this.chartFetcher = chartFetcher;
    }

    @Override
    public Result<List<ClosePrice>> fetchDaily(String symbol, LocalDate fromInclusive, LocalDate toInclusive) {
        try {
            Instant start = fromInclusive.atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant end = fromInclusive.atStartOfDay(ZoneOffset.UTC).toInstant();
            ChartData chartData = chartFetcher.fetchDaily(symbol, start.getEpochSecond(), end.getEpochSecond());
            return new Result<>(toClosePriceSeries(chartData), null);
        } catch (InterruptedException e) {
            return new Result<>(List.of(), FailureReason.CANCELED);
        }
    }
    private static List<ClosePrice> toClosePriceSeries(ChartData chartData) {
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
    private static long daysFromNow(LocalDate backToInclusiveDate) {
        // Duration.between does not work on LocalDate, which has no time of day to count seconds from
        return ChronoUnit.DAYS.between(backToInclusiveDate, LocalDate.now());
    }

}
