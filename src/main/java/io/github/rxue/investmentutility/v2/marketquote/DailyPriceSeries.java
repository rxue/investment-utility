package io.github.rxue.investmentutility.v2.marketquote;

import io.github.rxue.investmentutility.v2.util.DailyValues;

public interface DailyPriceSeries<V> extends DailyValues<V> {
    String currency();
}
