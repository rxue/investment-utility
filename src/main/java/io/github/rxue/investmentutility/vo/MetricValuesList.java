package io.github.rxue.investmentutility.vo;

import java.util.*;

public record MetricValuesList(Map<String,MetricValues> valuesBySecurityId) {
    /**
     * @return values sorted ascending by the value of the given metric, missing (null) values are ordered last
     */
    @SuppressWarnings("unchecked")
    public List<MetricValues> sortedBy(Metric metric) {
        Comparator<MetricValues> byMetricValue = Comparator.comparing(
                metricValues -> (Comparable<Object>) metricValues.get(metric),
                Comparator.nullsLast(Comparator.naturalOrder()));
        return valuesBySecurityId.values().stream().sorted(byMetricValue).toList();
    }
    public List<MetricValues> valuesList() {
        return valuesBySecurityId.values().stream()
                .toList();
    }
    public <T> T getMetricValue(String securityId, Metric metric, Class<T> valueType) {
        Comparable<?> comparableValue = valuesBySecurityId.get(securityId)
                .get(metric);
        return valueType.cast(comparableValue);
    }
    public Map<String,Map<Metric,Comparable<?>>> fullMap() {
        Map<String,Map<Metric,Comparable<?>>> result = new HashMap<>();
        valuesBySecurityId.forEach((securityId,metricValues) ->
                result.put(securityId, metricValues.values()));
        return Collections.unmodifiableMap(result);
    }
}
