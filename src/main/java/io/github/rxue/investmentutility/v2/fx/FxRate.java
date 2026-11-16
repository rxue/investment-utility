package io.github.rxue.investmentutility.v2.fx;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FxRate(LocalDate date, BigDecimal rate) {
}
