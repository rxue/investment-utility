package io.github.rxue.investmentutility.v2.fx.ecb;

import io.github.rxue.investmentutility.v2.fx.FxRateSeries;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FxRateArrayTest {
    @Test
    void get_base_without_weekend_gap() {
        // ECB EUR/USD, Monday 5.1.2026 - Friday 9.1.2026 (see EcbFxRateFetcherIT)
        LocalDate monday = LocalDate.of(2026, 1, 5);
        LocalDate tuesday = LocalDate.of(2026, 1, 6);
        LocalDate wednesday = LocalDate.of(2026, 1, 7);
        LocalDate thursday = LocalDate.of(2026, 1, 8);
        LocalDate friday = LocalDate.of(2026, 1, 9);
        BigDecimal mondayRate = new BigDecimal("1.1664");
        BigDecimal tuesdayRate = new BigDecimal("1.1707");
        BigDecimal wednesdayRate = new BigDecimal("1.1684");
        BigDecimal thursdayRate = new BigDecimal("1.1675");
        BigDecimal fridayRate = new BigDecimal("1.1642");

        FxRateSeries fxRateSeries = new FxRateSeries("USD",
                new LocalDate[]{monday, tuesday, wednesday, thursday, friday},
                new BigDecimal[]{mondayRate, tuesdayRate, wednesdayRate, thursdayRate, fridayRate});

        assertEquals(mondayRate, fxRateSeries.get(monday));
        assertEquals(tuesdayRate, fxRateSeries.get(tuesday));
        assertEquals(wednesdayRate, fxRateSeries.get(wednesday));
        assertEquals(thursdayRate, fxRateSeries.get(thursday));
        assertEquals(fridayRate, fxRateSeries.get(friday));
    }

    @Test
    void get_with_saturday() {
        // ECB EUR/USD, Monday 5.1.2026 - Friday 9.1.2026 (see EcbFxRateFetcherIT)
        LocalDate monday = LocalDate.of(2026, 1, 5);
        LocalDate tuesday = LocalDate.of(2026, 1, 6);
        LocalDate wednesday = LocalDate.of(2026, 1, 7);
        LocalDate thursday = LocalDate.of(2026, 1, 8);
        LocalDate friday = LocalDate.of(2026, 1, 9);
        BigDecimal mondayRate = new BigDecimal("1.1664");
        BigDecimal tuesdayRate = new BigDecimal("1.1707");
        BigDecimal wednesdayRate = new BigDecimal("1.1684");
        BigDecimal thursdayRate = new BigDecimal("1.1675");
        BigDecimal fridayRate = new BigDecimal("1.1642");

        FxRateSeries fxRateSeries = new FxRateSeries("USD",
                new LocalDate[]{monday, tuesday, wednesday, thursday, friday},
                new BigDecimal[]{mondayRate, tuesdayRate, wednesdayRate, thursdayRate, fridayRate});

        assertEquals(mondayRate, fxRateSeries.get(monday));
        assertEquals(tuesdayRate, fxRateSeries.get(tuesday));
        assertEquals(wednesdayRate, fxRateSeries.get(wednesday));
        assertEquals(thursdayRate, fxRateSeries.get(thursday));
        assertEquals(fridayRate, fxRateSeries.get(friday));
        assertEquals(fridayRate, fxRateSeries.get(friday.plusDays(1)));
    }

}
