package com.vehiclerental.extras;

import com.vehiclerental.model.Vehicle;
import com.vehiclerental.pricing.PricingStrategy;

import java.math.BigDecimal;
import java.time.Duration;

public class GpsExtra extends PricingDecorator {
    private static final BigDecimal PER_DAY = new BigDecimal("1000");

    public GpsExtra(PricingStrategy wrapped) {
        super(wrapped);
    }

    @Override
    protected BigDecimal extraCost(Vehicle vehicle, Duration duration) {
        return PER_DAY.multiply(BigDecimal.valueOf(startedDays(duration)));
    }

    @Override
    protected String extraName() {
        return "GPS";
    }
}
