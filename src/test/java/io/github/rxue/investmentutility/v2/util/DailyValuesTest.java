package io.github.rxue.investmentutility.v2.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class DailyValuesTest {
    @Test
    void value_base_without_weekend_gap() {
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

        DailyValues<LocalDate, BigDecimal> dailyValues = new DailyValues<>(
                new LocalDate[]{monday, tuesday, wednesday, thursday, friday},
                new BigDecimal[]{mondayRate, tuesdayRate, wednesdayRate, thursdayRate, fridayRate},
                friday,
                LocalDate::toEpochDay);

        assertEquals(mondayRate, dailyValues.value(monday));
        assertEquals(tuesdayRate, dailyValues.value(tuesday));
        assertEquals(wednesdayRate, dailyValues.value(wednesday));
        assertEquals(thursdayRate, dailyValues.value(thursday));
        assertEquals(fridayRate, dailyValues.value(friday));
        assertThrows(IllegalArgumentException.class, () -> dailyValues.value(friday.plusDays(1)));
    }

    @Test
    void value_with_saturday() {
        // ECB EUR/USD, Monday 5.1.2026 - Friday 9.1.2026 (see EcbFxRateFetcherIT)
        LocalDate monday = LocalDate.of(2026, 1, 5);
        LocalDate tuesday = LocalDate.of(2026, 1, 6);
        LocalDate wednesday = LocalDate.of(2026, 1, 7);
        LocalDate thursday = LocalDate.of(2026, 1, 8);
        LocalDate friday = LocalDate.of(2026, 1, 9);
        LocalDate saturday = friday.plusDays(1);
        BigDecimal mondayRate = new BigDecimal("1.1664");
        BigDecimal tuesdayRate = new BigDecimal("1.1707");
        BigDecimal wednesdayRate = new BigDecimal("1.1684");
        BigDecimal thursdayRate = new BigDecimal("1.1675");
        BigDecimal fridayRate = new BigDecimal("1.1642");

        DailyValues<LocalDate, BigDecimal> dailyValues = new DailyValues<>(
                new LocalDate[]{monday, tuesday, wednesday, thursday, friday},
                new BigDecimal[]{mondayRate, tuesdayRate, wednesdayRate, thursdayRate, fridayRate},
                saturday,
                LocalDate::toEpochDay);

        assertEquals(mondayRate, dailyValues.value(monday));
        assertEquals(tuesdayRate, dailyValues.value(tuesday));
        assertEquals(wednesdayRate, dailyValues.value(wednesday));
        assertEquals(thursdayRate, dailyValues.value(thursday));
        assertEquals(fridayRate, dailyValues.value(friday));
        assertEquals(fridayRate, dailyValues.value(saturday));
    }
}
