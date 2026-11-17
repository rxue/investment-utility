package io.github.rxue.investmentutility.v2.marketquote;

import io.github.rxue.investmentutility.v2.marketquote.vo.price.ClosePriceSeries;
import io.github.rxue.investmentutility.v2.util.Result;

import java.time.LocalDate;

public interface PriceSeriesFetcher {
    Result<ClosePriceSeries> fetchDaily(String symbol, LocalDate fromInclusive, LocalDate toInclusive);
}
