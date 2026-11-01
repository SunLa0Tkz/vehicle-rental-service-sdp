package com.vehiclerental.pricing;

import com.vehiclerental.model.Vehicle;

import java.math.BigDecimal;
import java.time.Duration;

public class HourlyPricing implements PricingStrategy {
    private static final long MINUTES_PER_HOUR = 60;

    @Override
    public BigDecimal calculate(Vehicle vehicle, Duration duration) {
        long startedHours = Math.ceilDiv(duration.toMinutes(), MINUTES_PER_HOUR);
        return vehicle.getHourlyRate().multiply(BigDecimal.valueOf(startedHours));
    }

    @Override
    public String getName() {
        return "hourly";
    }
}
