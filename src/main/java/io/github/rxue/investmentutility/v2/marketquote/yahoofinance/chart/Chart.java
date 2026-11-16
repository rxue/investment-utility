package io.github.rxue.investmentutility.v2.marketquote.yahoofinance.chart;

import java.math.BigDecimal;

/**
 * The {@code chart} node of the v8 chart response; exactly one of result and error is set.
 * The nested records follow the JSON names, nesting keeps {@code Result} and {@code Error} from clashing with
 * {@code util.Result} and {@code java.lang.Error} outside this record.
 */
record Chart(Result result, Error error) {
    record Result(Meta meta, long[] timestamp, Indicators indicator) {
    }

    record Meta(String currency, long regularMarketTime) {
    }

    record Indicators(Quote quote) {
    }

    record Quote(BigDecimal[] close) {
    }

    record Error(String code, String description) {
    }
}
