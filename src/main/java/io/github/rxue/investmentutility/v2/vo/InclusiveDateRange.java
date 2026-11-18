package io.github.rxue.investmentutility.v2.vo;

import java.time.LocalDate;
import java.util.Objects;

public record InclusiveDateRange(LocalDate from, LocalDate to) {
    public InclusiveDateRange {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        if (!from.isBefore(to))
            throw new IllegalArgumentException(from + " is after or the same as " + to);
    }
}
