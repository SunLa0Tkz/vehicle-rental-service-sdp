package com.vehiclerental.pricing;

import com.vehiclerental.model.Vehicle;

import java.math.BigDecimal;
import java.time.Duration;

public interface PricingStrategy {
    BigDecimal calculate(Vehicle vehicle, Duration duration);

    String getName();
}
