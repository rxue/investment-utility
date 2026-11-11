
package io.github.rxue.investmentutility.marketquote;

import io.github.rxue.investmentutility.vo.MetricValues;

import java.util.*;

import static java.util.stream.Collectors.toMap;

/**
 * Contract: report currency has to be configured so that the LATEST_MARKET_PRICE_IN_REPORT_CURRENCY can be calculated
 */
public interface QuoteFetcher extends BatchQuoteFetcher, SingleQuoteFetcher {

    @Override
    default List<MetricValues> fetchAll(SequencedSet<String> securityIds, SequencedSet<QuoteMetric> quoteMetrics) {
        return securityIds.stream()
                .map(secId -> toMetricValues(secId, fetch(secId, quoteMetrics)))
                .toList();
    }

    static MetricValues toMetricValues(String securityId, SequencedMap<QuoteMetric,Comparable<?>> quoteMetricValues) {
        return new MetricValues.Builder(securityId)
                .addAll(quoteMetricValues)
                .build();
    }
}
