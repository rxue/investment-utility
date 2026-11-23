package io.github.rxue.investmentutility.v2.util;

import java.time.LocalDate;

public record DailyValue<V>(LocalDate date, V value) {
}
