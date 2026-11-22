package io.github.rxue.investmentutility.v2.marketquote.yahoofinance.chart;

import io.github.rxue.investmentutility.v2.marketquote.CloseCentPriceSeries;

import java.time.LocalDate;


final class YahooClosePriceInCentSeries implements CloseCentPriceSeries {
    private final YahooClosePriceSeries<Long> series;
    public YahooClosePriceInCentSeries(String currency, long[] utcTimestamps, Long[] values) {
        this.series = new YahooClosePriceSeries<>(currency, utcTimestamps, values);
    }


    @Override
    public String currency() {
        return series.currency();
    }

    @Override
    public long priceInCent(LocalDate date) {
        return series.price(date);
    }
}
