package io.github.rxue.investmentutility.marketquote;

import io.github.rxue.investmentutility.vo.MetricValues;

import java.util.List;
import java.util.SequencedSet;
import java.util.Set;

public interface BatchQuoteFetcher {
    List<MetricValues> fetchAll(Set<String> securityIds, SequencedSet<QuoteMetric> quoteMetrics);

}
