package com.vehiclerental.model;

import java.math.BigDecimal;

public class Van extends Vehicle {
    public Van(String id, String model, BigDecimal hourlyRate, BigDecimal dailyRate) {
        super(id, model, hourlyRate, dailyRate);
    }

    @Override
    public VehicleType getType() {
        return VehicleType.VAN;
    }
}
