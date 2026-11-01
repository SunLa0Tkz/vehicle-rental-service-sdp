package com.vehiclerental.model;

import java.math.BigDecimal;

public class Bike extends Vehicle {
    public Bike(String id, String model, BigDecimal hourlyRate, BigDecimal dailyRate) {
        super(id, model, hourlyRate, dailyRate);
    }

    @Override
    public VehicleType getType() {
        return VehicleType.BIKE;
    }
}
