package io.github.rxue.investmentutility.vo;

import java.util.*;

public final class MetricValues {
    private final String securityId;
    private final SequencedMap<Metric,Comparable<?>> values;
    private MetricValues(Builder builder) {
        this.securityId = builder.securityId;
        this.values = Collections.unmodifiableSequencedMap(new LinkedHashMap<>(builder.values));
    }
    public String getSecurityId() {
        return securityId;
    }

    public SequencedMap<Metric,Comparable<?>> getValueByMetric() {
        return values;
    }

    public List<Metric> metricList() {
        return values.keySet().stream()
                .toList();
    }
    public List<Comparable<?>> values() {
        return values.values().stream().toList();
    }
    public Comparable<?> valueOf(Metric metric) {
        return values.get(metric);
    }

    public static Comparator<MetricValues> comparatorBy(Metric metric) {
        return Comparator.comparing(mv -> (Comparable<Object>) mv.valueOf(metric),
                Comparator.nullsLast(Comparator.naturalOrder()));
    }

    public static class Builder {
        private final String securityId;
        private final SequencedMap<Metric,Comparable<?>> values;
        public Builder(String securityId) {
            this.securityId = securityId;
            this.values = new LinkedHashMap<>();
        }
        public Builder add(Metric metric,Comparable<?> value) {
            values.put(metric, value);
            return this;
        }
        public Builder addAll(SequencedMap<? extends Metric,? extends Comparable<?>> values) {
            this.values.putAll(values);
            return this;
        }
        public MetricValues build() {
            return new MetricValues(this);
        }
    }
}
