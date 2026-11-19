package io.github.rxue.investmentutility.v2.marketquote;

import io.github.rxue.investmentutility.v2.util.Result;
import io.github.rxue.investmentutility.v2.vo.InclusiveDateRange;

public interface PriceSeriesFetcher {
    Result<ClosePriceSeries> fetchDaily(String symbol, InclusiveDateRange inclusiveDateRange);
}
