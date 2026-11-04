package com.vehiclerental.extras;

import com.vehiclerental.model.Vehicle;
import com.vehiclerental.pricing.PricingStrategy;

import java.math.BigDecimal;
import java.time.Duration;

public class ChildSeatExtra extends PricingDecorator {
    private static final BigDecimal FEE = new BigDecimal("1500");

    public ChildSeatExtra(PricingStrategy wrapped) {
        super(wrapped);
    }

    @Override
    protected BigDecimal extraCost(Vehicle vehicle, Duration duration) {
        return FEE;
    }

    @Override
    protected String extraName() {
        return "child seat";
    }
}
