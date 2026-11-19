package io.github.rxue.investmentutility.v2.vo;

import io.github.rxue.investmentutility.v2.util.DailyValues;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class DailyValuesTest {
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

        DailyValues dailyValues = new DailyValues((int) monday.toEpochDay(),
                new BigDecimal[]{mondayRate, tuesdayRate, wednesdayRate, thursdayRate, fridayRate});

        assertEquals(mondayRate, dailyValues.get(monday));
        assertEquals(tuesdayRate, dailyValues.get(tuesday));
        assertEquals(wednesdayRate, dailyValues.get(wednesday));
        assertEquals(thursdayRate, dailyValues.get(thursday));
        assertEquals(fridayRate, dailyValues.get(friday));
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

        DailyValues dailyValues = new DailyValues((int) monday.toEpochDay(),
                new BigDecimal[]{mondayRate, tuesdayRate, wednesdayRate, thursdayRate, fridayRate});

        assertEquals(mondayRate, dailyValues.get(monday));
        assertEquals(tuesdayRate, dailyValues.get(tuesday));
        assertEquals(wednesdayRate, dailyValues.get(wednesday));
        assertEquals(thursdayRate, dailyValues.get(thursday));
        assertEquals(fridayRate, dailyValues.get(friday));
        assertEquals(fridayRate, dailyValues.get(friday.plusDays(1)));
    }
}
