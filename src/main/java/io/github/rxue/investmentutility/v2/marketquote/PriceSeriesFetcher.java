package io.github.rxue.investmentutility.v2.marketquote;

import io.github.rxue.investmentutility.v2.marketquote.vo.price.ClosePrice;

import java.time.LocalDate;
import java.util.List;

public interface PriceSeriesFetcher {
    Result<List<ClosePrice>> fetchDaily(String symbol, LocalDate fromInclusive, LocalDate toInclusive);
}
