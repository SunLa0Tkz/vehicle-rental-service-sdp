package com.vehiclerental.pricing;

import com.vehiclerental.model.Vehicle;

import java.math.BigDecimal;
import java.time.Duration;

public class DailyPricing implements PricingStrategy {
    private static final long MINUTES_PER_DAY = 24 * 60;

    @Override
    public BigDecimal calculate(Vehicle vehicle, Duration duration) {
        long startedDays = Math.ceilDiv(duration.toMinutes(), MINUTES_PER_DAY);
        return vehicle.getDailyRate().multiply(BigDecimal.valueOf(startedDays));
    }

    @Override
    public String getName() {
        return "daily";
    }
}
