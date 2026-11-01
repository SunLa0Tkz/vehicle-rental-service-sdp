package com.vehiclerental.pricing;

import com.vehiclerental.factory.CarFactory;
import com.vehiclerental.model.Vehicle;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PricingStrategyTest {
    private final Vehicle car = new CarFactory().createVehicle("C-1", "Camry");

    @Test
    void hourlyChargesPerHour() {
        assertEquals(new BigDecimal("15000"), new HourlyPricing().calculate(car, Duration.ofHours(5)));
    }

    @Test
    void hourlyRoundsUpStartedHour() {
        assertEquals(new BigDecimal("6000"), new HourlyPricing().calculate(car, Duration.ofMinutes(61)));
    }

    @Test
    void dailyChargesFullDayForShortRental() {
        assertEquals(new BigDecimal("18000"), new DailyPricing().calculate(car, Duration.ofHours(5)));
    }

    @Test
    void dailyDoesNotRoundUpExactDays() {
        assertEquals(new BigDecimal("36000"), new DailyPricing().calculate(car, Duration.ofDays(2)));
    }

    @Test
    void dailyRoundsUpStartedDay() {
        assertEquals(new BigDecimal("54000"), new DailyPricing().calculate(car, Duration.ofHours(49)));
    }
}
