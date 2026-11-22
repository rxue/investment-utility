package io.github.rxue.investmentutility.v2.marketquote.yahoofinance.chart;

import io.github.rxue.investmentutility.v2.fx.FxRateFetcher;
import io.github.rxue.investmentutility.v2.marketquote.PriceSeriesFetcher;
import io.github.rxue.investmentutility.v2.marketquote.DailyPriceSeries;
import io.github.rxue.investmentutility.v2.util.Result;
import io.github.rxue.investmentutility.v2.vo.InclusiveDateRange;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.function.Function;

import static io.github.rxue.investmentutility.v2.util.Constants.BACKWARD_DAYS;

public class YahooPriceSeriesFetcher implements PriceSeriesFetcher {
    private final Query1V8PriceChartFetcher chartFetcher;
    private final FxRateFetcher fxRateFetcher;
    public YahooPriceSeriesFetcher(Query1V8PriceChartFetcher chartFetcher, FxRateFetcher fxRateFetcher) {
        this.chartFetcher = chartFetcher;
        this.fxRateFetcher = fxRateFetcher;
    }

    private <T> Result<T> fetchDaily(String tickerSymbol, InclusiveDateRange inclusiveDateRange, Function<Chart.Result,T> pricesParser) {
        final Instant start = inclusiveDateRange.from().atStartOfDay(ZoneOffset.UTC).toInstant();
        final Instant end = inclusiveDateRange.to().plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        try {
            Chart chartData = chartFetcher.fetchDaily(tickerSymbol, start.minus(BACKWARD_DAYS, ChronoUnit.DAYS).getEpochSecond(), end.getEpochSecond());
            return new Result.Success<>(pricesParser.apply(chartData.result()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new Result.TechnicalError<>("Fetch interrupted", e);
        } catch (IOException e) {
            return new Result.TechnicalError<>("Network connection problem", e);
        }
    }

    @Override
    public Result<DailyPriceSeries<Double>> fetchAdjustedClosePrices(String tickerSymbol, InclusiveDateRange inclusiveDateRange) {
        Function<Chart.Result, DailyPriceSeries<Double>> adjustedClosePricesParser = result -> {
            long[] timestamps = result.timestamp();
            Double[] adjustedCloses = Arrays.stream(result.indicator().adjClose())
                    .boxed()
                    .toArray(Double[]::new);
            return new YahooDailyPriceSeries<>(result.meta().currency(), timestamps, adjustedCloses, inclusiveDateRange.to());
        };
        return fetchDaily(tickerSymbol, inclusiveDateRange, adjustedClosePricesParser);
    }

    @Override
    public Result<DailyPriceSeries<Long>> fetchClosePrices(String tickerSymbol, InclusiveDateRange inclusiveDateRange) {
        Function<Chart.Result,DailyPriceSeries<Long>> closePricesParser = result -> {
            long[] timestamps = result.timestamp();
            BigDecimal[] closes = result.indicator().quote().close();
            // a day without trades has a null close, kept as null so that it stays aligned with its timestamp
            Long[] closesInCent = Arrays.stream(closes)
                    .map(close -> close.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact())
                    .toArray(Long[]::new);
            return new YahooDailyPriceSeries(result.meta().currency(), timestamps, closesInCent, inclusiveDateRange.to());
        };
        return fetchDaily(tickerSymbol, inclusiveDateRange, closePricesParser);
    }

}
