package io.github.rxue.investmentutility.v2.marketquote.yahoofinance.chart;

import io.github.rxue.investmentutility.v2.fx.FxRateFetcher;
import io.github.rxue.investmentutility.v2.fx.FxRateSeries;
import io.github.rxue.investmentutility.v2.marketquote.DualCurrencyClosePriceSeries;
import io.github.rxue.investmentutility.v2.marketquote.PriceSeriesFetcher;
import io.github.rxue.investmentutility.v2.marketquote.ClosePriceSeries;
import io.github.rxue.investmentutility.v2.util.Result;
import io.github.rxue.investmentutility.v2.vo.InclusiveDateRange;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

import static io.github.rxue.investmentutility.v2.util.Constants.BACKWARD_DAYS;
import static io.github.rxue.investmentutility.v2.util.Constants.EUR;

public class YahooFinancePriceSeriesFetcher implements PriceSeriesFetcher {
    private final Query1V8PriceChartFetcher chartFetcher;
    private final FxRateFetcher fxRateFetcher;
    public YahooFinancePriceSeriesFetcher(Query1V8PriceChartFetcher chartFetcher, FxRateFetcher fxRateFetcher) {
        this.chartFetcher = chartFetcher;
        this.fxRateFetcher = fxRateFetcher;
    }

    private Result<ClosePriceSeries> fetchDaily(String tickerSymbol, InclusiveDateRange inclusiveDateRange, Function<Chart.Result,ClosePriceSeries> pricesParser) {
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
    public Result<ClosePriceSeries> fetchDailyAdjusted(String tickerSymbol, InclusiveDateRange inclusiveDateRange) {
        Function<Chart.Result,ClosePriceSeries> adjustedClosePricesParser = result -> {
            long[] timestamps = result.timestamp();
            return new YahooClosePriceSeries(result.meta().currency(), timestamps, result.indicator().adjClose());
        };
        return fetchDaily(tickerSymbol, inclusiveDateRange, adjustedClosePricesParser);
    }

    @Override
    public Result<ClosePriceSeries> fetchDaily(String tickerSymbol, InclusiveDateRange inclusiveDateRange) {
        Function<Chart.Result,ClosePriceSeries> closePricesParser = result -> {
            long[] timestamps = result.timestamp();
            BigDecimal[] closes = result.indicator().quote().close();
            return new YahooClosePriceSeries(result.meta().currency(), timestamps, closes);
        };
        return fetchDaily(tickerSymbol, inclusiveDateRange, closePricesParser);
    }

    @Override
    public Result<DualCurrencyClosePriceSeries> fetchDailyInEuro(String tickerSymbol, InclusiveDateRange inclusiveDateRange) {
        Result<ClosePriceSeries> originalPriceSeries = fetchDaily(tickerSymbol, inclusiveDateRange);
        return switch(originalPriceSeries) {
            case Result.Success<ClosePriceSeries>(ClosePriceSeries closePriceSeries) -> {
                yield getDualCurrencyClosePriceSeriesResult(closePriceSeries, inclusiveDateRange);
            }
            case Result.ResponseError<ClosePriceSeries>(int status, String description) ->
                    new Result.ResponseError<>(status, description);
            case Result.TechnicalError<ClosePriceSeries>(String description, Throwable cause) ->
                    new Result.TechnicalError<>(description, cause);
        };
    }

    private Result<DualCurrencyClosePriceSeries> getDualCurrencyClosePriceSeriesResult(
            ClosePriceSeries closePriceSeries,
            InclusiveDateRange inclusiveDateRange) {
        if (EUR.equals(closePriceSeries.currency())) {
            return new Result.Success<>(new YahooDualCurrencyClosePriceSeries(closePriceSeries, null));
        }
        return switch (fxRateFetcher.fetchRateFromEuroTo(closePriceSeries.currency(), inclusiveDateRange)) {
            case Result.Success<FxRateSeries>(FxRateSeries fxRateSeries) ->
                    new Result.Success<>(new YahooDualCurrencyClosePriceSeries(closePriceSeries, fxRateSeries));
            case Result.ResponseError<FxRateSeries>(int status, String description) ->
                    new Result.ResponseError<>(status, description);
            case Result.TechnicalError<FxRateSeries>(String description, Throwable cause) ->
                    new Result.TechnicalError<>(description, cause);
        };
    }

}
