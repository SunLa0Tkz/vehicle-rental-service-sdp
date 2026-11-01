package com.vehiclerental.factory;

import com.vehiclerental.model.Van;
import com.vehiclerental.model.Vehicle;

import java.math.BigDecimal;

public class VanFactory extends VehicleFactory {
    private static final BigDecimal HOURLY_RATE = new BigDecimal("4500");
    private static final BigDecimal DAILY_RATE = new BigDecimal("27000");

    @Override
    public Vehicle createVehicle(String id, String model) {
        return new Van(id, model, HOURLY_RATE, DAILY_RATE);
    }
}
