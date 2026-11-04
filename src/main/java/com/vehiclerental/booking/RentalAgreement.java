package com.vehiclerental.booking;

import com.vehiclerental.model.Vehicle;
import com.vehiclerental.pricing.PricingStrategy;

import java.math.BigDecimal;
import java.time.Duration;

public class RentalAgreement {
    private final String id;
    private final String customerName;
    private final Vehicle vehicle;
    private final Duration duration;
    private final PricingStrategy pricing;

    RentalAgreement(String id, String customerName, Vehicle vehicle, Duration duration, PricingStrategy pricing) {
        this.id = id;
        this.customerName = customerName;
        this.vehicle = vehicle;
        this.duration = duration;
        this.pricing = pricing;
    }

    public String getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Duration getDuration() {
        return duration;
    }

    public PricingStrategy getPricing() {
        return pricing;
    }

    public BigDecimal getPrice() {
        return pricing.calculate(vehicle, duration);
    }

    public BigDecimal finalPrice(Duration usedFor) {
        if (usedFor.isNegative() || usedFor.isZero()) {
            throw new IllegalArgumentException("Used time must be positive");
        }
        Duration charged = usedFor.compareTo(duration) > 0 ? usedFor : duration;
        return pricing.calculate(vehicle, charged);
    }

    @Override
    public String toString() {
        return id + ": " + customerName + " -> " + vehicle + ", " + duration.toHours() + "h, "
                + pricing.getName() + ", " + getPrice() + " KZT";
    }
}
