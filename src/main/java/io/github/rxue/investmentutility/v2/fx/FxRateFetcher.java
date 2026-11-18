package io.github.rxue.investmentutility.v2.fx;

import io.github.rxue.investmentutility.v2.util.Result;
import io.github.rxue.investmentutility.v2.vo.InclusiveDateRange;

public interface FxRateFetcher {
    /**
     *
     * @param quoteCurrency the quote currency
     * @param dateRange the inclusive date range
     * @return
     */
    Result<FxRateSeries> fetchRateFromEuroTo(String quoteCurrency, InclusiveDateRange dateRange);
}
