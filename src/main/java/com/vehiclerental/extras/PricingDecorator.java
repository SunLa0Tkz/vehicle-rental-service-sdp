package com.vehiclerental.extras;

import com.vehiclerental.model.Vehicle;
import com.vehiclerental.pricing.PricingStrategy;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Objects;

public abstract class PricingDecorator implements PricingStrategy {
    private static final long MINUTES_PER_DAY = 24 * 60;

    private final PricingStrategy wrapped;

    protected PricingDecorator(PricingStrategy wrapped) {
        this.wrapped = Objects.requireNonNull(wrapped);
    }

    @Override
    public BigDecimal calculate(Vehicle vehicle, Duration duration) {
        return wrapped.calculate(vehicle, duration).add(extraCost(vehicle, duration));
    }

    @Override
    public String getName() {
        return wrapped.getName() + " + " + extraName();
    }

    protected abstract BigDecimal extraCost(Vehicle vehicle, Duration duration);

    protected abstract String extraName();

    protected static long startedDays(Duration duration) {
        return Math.ceilDiv(duration.toMinutes(), MINUTES_PER_DAY);
    }
}
