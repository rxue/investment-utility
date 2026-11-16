package io.github.rxue.investmentutility.v2.fx;

import io.github.rxue.investmentutility.v2.util.Result;

import java.time.LocalDate;

public interface FxRateFetcher {
    /**
     *
     * @param baseCurrency
     * @param quoteCurrency
     * @param date
     * @return Foreign exchange rate of baseCurrency:quoteCurrency
     */
    Result<FxRate> fetchRate(String baseCurrency, String quoteCurrency, LocalDate date);
}
