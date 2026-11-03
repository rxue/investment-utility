package io.github.rxue.investmentutility.marketquote;

import java.util.SequencedMap;
import java.util.SequencedSet;

public interface SingleQuoteFetcher {
    /**
     * 
     * @param securityId
     * @param quoteMetrics
     * @return
     * @throws QuoteFetchException if the quote cannot be fetched, e.g. network failure or an error response
     */
    SequencedMap<QuoteMetric,Comparable<?>> fetch(String securityId, SequencedSet<QuoteMetric> quoteMetrics);
}
