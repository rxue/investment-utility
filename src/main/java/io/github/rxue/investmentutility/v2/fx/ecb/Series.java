package io.github.rxue.investmentutility.v2.fx.ecb;

import java.math.BigDecimal;
import java.util.Map;

public record Series(Map<Integer,BigDecimal> observations) {
}
