package com.vehiclerental.service;

import com.vehiclerental.model.Vehicle;

import java.math.BigDecimal;
import java.time.Duration;

public record Rental(
        String id,
        Vehicle vehicle,
        String customerName,
        Duration duration,
        String pricingName,
        BigDecimal cost) {

    @Override
    public String toString() {
        return id + ": " + customerName + " -> " + vehicle + ", " + duration.toHours() + "h, "
                + pricingName + " pricing, cost " + cost + " KZT";
    }
}
