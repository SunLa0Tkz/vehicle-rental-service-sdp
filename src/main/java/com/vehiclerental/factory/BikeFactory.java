package com.vehiclerental.factory;

import com.vehiclerental.model.Bike;
import com.vehiclerental.model.Vehicle;

import java.math.BigDecimal;

public class BikeFactory extends VehicleFactory {
    private static final BigDecimal HOURLY_RATE = new BigDecimal("500");
    private static final BigDecimal DAILY_RATE = new BigDecimal("3000");

    @Override
    public Vehicle createVehicle(String id, String model) {
        return new Bike(id, model, HOURLY_RATE, DAILY_RATE);
    }
}
