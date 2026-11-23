package io.github.rxue.investmentutility.v2.marketquote;

import io.github.rxue.investmentutility.v2.util.DailyValue;

import java.util.List;

public interface DailyPriceSeries<V> {
    String currency();
    List<DailyValue<V>> dailyValues();
}
