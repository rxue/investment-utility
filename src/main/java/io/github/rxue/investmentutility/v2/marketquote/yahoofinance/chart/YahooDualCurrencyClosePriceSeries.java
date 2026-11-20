package io.github.rxue.investmentutility.v2.marketquote.yahoofinance.chart;

import io.github.rxue.investmentutility.v2.fx.FxRateSeries;
import io.github.rxue.investmentutility.v2.marketquote.ClosePriceSeries;
import io.github.rxue.investmentutility.v2.marketquote.DualCurrencyClosePriceSeries;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static io.github.rxue.investmentutility.v2.util.Constants.EUR;

record YahooDualCurrencyClosePriceSeries(ClosePriceSeries closePriceSeries, FxRateSeries fxRateSeries) implements DualCurrencyClosePriceSeries {
    @Override
    public String convertedCurrency() {
        return EUR;
    }

    @Override
    public BigDecimal convertedPrice(LocalDate date) {
        BigDecimal originalPrice = price(date);
        return fxRateSeries == null ? originalPrice : originalPrice.divide(fxRateSeries.get(date), 4, RoundingMode.HALF_UP);
    }

    @Override
    public String currency() {
        return closePriceSeries.currency();
    }

    @Override
    public BigDecimal price(LocalDate date) {
        return closePriceSeries.price(date);
    }
}
