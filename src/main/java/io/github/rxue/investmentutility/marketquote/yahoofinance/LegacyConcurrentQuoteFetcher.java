package io.github.rxue.investmentutility.marketquote.yahoofinance;

import io.github.rxue.investmentutility.marketquote.BatchQuoteFetcher;
import io.github.rxue.investmentutility.marketquote.QuoteMetric;
import io.github.rxue.investmentutility.marketquote.QuoteFetcher;
import io.github.rxue.investmentutility.vo.MetricValues;

import java.util.*;
import java.util.concurrent.*;

/**
 *
 * Currently, each thread has its own http client, separate Yahoo login on each worker thread. There is potent concurrency issues:
 *
 * Many threads, each fetches a cookie and a crumb before fetching any quotes.
 * Bursts of crumb requests are what Yahoo tends to rate-limit (HTTP 429 ERROR response code). One rejected crumb fails the whole batch,
 * and since each thread maintains its own crumb, you can't tell which ticker hit it
 *
 */
public class LegacyConcurrentQuoteFetcher implements BatchQuoteFetcher {
    private final ThreadLocal<QuoteFetcher> worker;
    private final ExecutorService executorService;
    public LegacyConcurrentQuoteFetcher(String reportCurrency, ExecutorService executorService) {
        this.executorService = executorService;
        this.worker = ThreadLocal.withInitial(() -> new YahooFinanceQuoteFetcher(reportCurrency));
    }

    @Override
    public List<MetricValues> fetchAll(SequencedSet<String> securityIds, SequencedSet<QuoteMetric> quoteMetrics) {
        List<Callable<SequencedMap<QuoteMetric,Comparable<?>>>> tasks = new ArrayList<>();
        List<String> securityIdList = new ArrayList<>();
        for (String securityId : securityIds) {
            tasks.add(() -> worker.get().fetch(securityId, quoteMetrics));
            securityIdList.add(securityId);
        }
        try {
            // blocks until every task has completed (normally or exceptionally)
            List<Future<SequencedMap<QuoteMetric,Comparable<?>>>> futures = executorService.invokeAll(tasks);
            List<MetricValues> metricValuesList = new ArrayList<>();
            for (int i = 0; i < securityIdList.size(); i++) {
                String secId = securityIdList.get(i);
                metricValuesList.add(QuoteFetcher.toMetricValues(secId, futures.get(i).get()));
            }
            return metricValuesList;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }
}
