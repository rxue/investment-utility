package io.github.rxue.investmentutility.marketquote;

import io.github.rxue.investmentutility.vo.MetricValues;

import java.util.List;
import java.util.SequencedSet;

public interface BatchQuoteFetcher {
    /**
     *
     * @param securityIds
     * @param quoteMetrics
     * @return one {@link MetricValues} per security id in {@code securityIds}, in the same iteration order as {@code securityIds}
     */
    List<MetricValues> fetchAll(SequencedSet<String> securityIds, SequencedSet<QuoteMetric> quoteMetrics);

}
