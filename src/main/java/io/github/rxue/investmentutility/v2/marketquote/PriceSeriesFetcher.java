package io.github.rxue.investmentutility.v2.marketquote;

import io.github.rxue.investmentutility.v2.util.Result;
import io.github.rxue.investmentutility.v2.vo.InclusiveDateRange;

public interface PriceSeriesFetcher {
    /**
     * fetch the daily ajusted close price series of the given period
     * @param tickerSymbol
     * @param inclusiveDateRange
     * @return
     */
    Result<ClosePriceSeries> fetchDailyAdjusted(String tickerSymbol, InclusiveDateRange inclusiveDateRange);
    Result<ClosePriceSeries> fetchDaily(String tickerSymbol, InclusiveDateRange inclusiveDateRange);
    Result<DualCurrencyClosePriceSeries> fetchDailyInEuro(String tickerSymbol, InclusiveDateRange inclusiveDateRange);
}
