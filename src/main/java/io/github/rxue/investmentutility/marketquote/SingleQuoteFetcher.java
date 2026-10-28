package io.github.rxue.investmentutility.marketquote;

import java.util.Map;
import java.util.Set;

public interface SingleQuoteFetcher {
    /**
     * 
     * @param securityId
     * @param quoteMetrics
     * @return
     * @throws QuoteFetchException if the quote cannot be fetched, e.g. network failure or an error response
     */
    Map<QuoteMetric,Comparable<?>> fetch(String securityId, Set<QuoteMetric> quoteMetrics);
}
