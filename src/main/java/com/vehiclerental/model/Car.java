package com.vehiclerental.model;

import java.math.BigDecimal;

public class Car extends Vehicle {
    public Car(String id, String model, BigDecimal hourlyRate, BigDecimal dailyRate) {
        super(id, model, hourlyRate, dailyRate);
    }

    @Override
    public VehicleType getType() {
        return VehicleType.CAR;
    }
}
