package io.github.rxue.investmentutility.v2.fx.ecb;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

record ExchangeRates(DataSet dataSet, Structure structure) {
    record DataSet(Map<String,BigDecimal[]> seriesObservations) {
    }
    record Structure(List<ObservationValue> observationValues) {
        record ObservationValue(LocalDate id) {}
    }
}
