package io.github.rxue.investmentutility.vo.price;

import io.github.rxue.investmentutility.v2.marketquote.vo.price.ClosePriceSeries;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClosePriceSeriesTest {
    @Test
    void priceOf_no_matched_timestamp() {
        BigDecimal priceOn20260102 = new BigDecimal("25.18000030517578");
        BigDecimal priceOn20260105 = new BigDecimal("25.170000076293945");
        ClosePriceSeries series = new ClosePriceSeries("USD", new long[]{1767364200, 1767623400},
                new BigDecimal[]{priceOn20260102, priceOn20260105});
        assertEquals(priceOn20260102, series.priceOf(LocalDate.of(2026,1,2)));
        assertEquals(priceOn20260102, series.priceOf(LocalDate.of(2026,1,3)));
        assertEquals(priceOn20260102, series.priceOf(LocalDate.of(2026,1,4)));
        assertEquals(priceOn20260105, series.priceOf(LocalDate.of(2026,1,5)));
    }
}
