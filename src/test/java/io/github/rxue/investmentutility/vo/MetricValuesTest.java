package io.github.rxue.investmentutility.vo;

import io.github.rxue.investmentutility.marketquote.QuoteMetric;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MetricValuesTest {

    @Test
    void build_MetricValues_must_be_immutable() {
        MetricValues.Builder builder = new MetricValues.Builder("AAPL")
                .add(QuoteMetric.COMPANY_NAME, "Apple Inc.");
        MetricValues metricValues = builder.build();

        builder.add(QuoteMetric.TRAILING_PE, "30");

        assertEquals(Map.of(QuoteMetric.COMPANY_NAME, "Apple Inc."), metricValues.getValueByMetric());
    }
}
