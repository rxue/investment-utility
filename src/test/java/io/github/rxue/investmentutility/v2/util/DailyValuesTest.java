package io.github.rxue.investmentutility.v2.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class DailyValuesTest {
    private static final double DELTA = 1e-9;
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

        DailyValuesImpl<LocalDate, BigDecimal> dailyValues = new DailyValuesImpl<>(
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

        DailyValuesImpl<LocalDate, BigDecimal> dailyValues = new DailyValuesImpl<>(
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
    @Test
    void value_6_days_with_saturday_as_the_last_day() {
        // ECB EUR/USD as returned for the range Monday 5.1.2026 - Saturday 10.1.2026 (see EcbFxRateFetcherIT)
        LocalDate monday = LocalDate.of(2026,1,5);
        LocalDate[] tradingDays = {
                LocalDate.of(2025,12,29),
                LocalDate.of(2025,12,30),
                LocalDate.of(2025,12,31),
                LocalDate.of(2026,1,2),
                monday,
                monday.plusDays(1),
                monday.plusDays(2),
                monday.plusDays(3),
                monday.plusDays(4)};
        Double[] rates = {
                1.1766,
                1.1757,
                1.175,
                1.1721,
                1.1664,
                1.1707,
                1.1684,
                1.1675,
                1.1642};

        DailyValuesImpl<LocalDate,Double> fxRates = new DailyValuesImpl<>(tradingDays, rates, monday.plusDays(5), LocalDate::toEpochDay);

        assertEquals(1.1664, fxRates.value(LocalDate.of(2026,1,5)), DELTA);
        assertEquals(1.1707, fxRates.value(LocalDate.of(2026,1,6)), DELTA);
        assertEquals(1.1684, fxRates.value(LocalDate.of(2026,1,7)), DELTA);
        assertEquals(1.1675, fxRates.value(LocalDate.of(2026,1,8)), DELTA);
        double fridayFxRate = 1.1642;
        assertEquals(fridayFxRate, fxRates.value(LocalDate.of(2026,1,9)), DELTA);
        assertEquals(fridayFxRate, fxRates.value(LocalDate.of(2026,1,10)), DELTA);
    }
    @Test
    void value_2_days_last_day_is_the_first_day_after_holiday() {
        // ECB EUR/USD as returned for the range Monday 5.1.2026 - Saturday 10.1.2026 (see EcbFxRateFetcherIT)
        LocalDate firstDateAfterHoliday = LocalDate.of(2026, 4, 7);
        LocalDate[] tradingDays = {
                LocalDate.of(2026,3,27),
                LocalDate.of(2026,3,30),
                LocalDate.of(2026,3,31),
                LocalDate.of(2026,4,1),
                LocalDate.of(2026,4,2),
                firstDateAfterHoliday};
        Double[] rates = {
                1.1517,
                1.1484,
                1.1498,
                1.1605,
                1.1525,
                1.1557};

        DailyValuesImpl<LocalDate,Double> fxRates = new DailyValuesImpl<>(tradingDays, rates, firstDateAfterHoliday, LocalDate::toEpochDay);

        assertEquals(1.1525, fxRates.value(LocalDate.of(2026,4,6)), DELTA);
        assertEquals(1.1557, fxRates.value(LocalDate.of(2026,4,7)), DELTA);
    }
}
