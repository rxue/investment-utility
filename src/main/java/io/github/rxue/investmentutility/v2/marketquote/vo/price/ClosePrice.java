package io.github.rxue.investmentutility.v2.marketquote.vo.price;

import java.time.LocalDate;

/**
 *
 * @param date UTC date, i.e. no offset
 * @param value
 */
public record ClosePrice(LocalDate date, Value value) {
    public long valueInCent() {
        return value.valueInCent();
    }
}
