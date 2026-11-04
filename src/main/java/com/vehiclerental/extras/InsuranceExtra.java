package com.vehiclerental.extras;

import com.vehiclerental.model.Vehicle;
import com.vehiclerental.pricing.PricingStrategy;

import java.math.BigDecimal;
import java.time.Duration;

public class InsuranceExtra extends PricingDecorator {
    public InsuranceExtra(PricingStrategy wrapped) {
        super(wrapped);
    }

    @Override
    protected BigDecimal extraCost(Vehicle vehicle, Duration duration) {
        BigDecimal perDay = switch (vehicle.getType()) {
            case BIKE -> new BigDecimal("500");
            case CAR -> new BigDecimal("2000");
            case VAN -> new BigDecimal("3000");
        };
        return perDay.multiply(BigDecimal.valueOf(startedDays(duration)));
    }

    @Override
    protected String extraName() {
        return "insurance";
    }
}
