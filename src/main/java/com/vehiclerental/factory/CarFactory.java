package com.vehiclerental.factory;

import com.vehiclerental.model.Car;
import com.vehiclerental.model.Vehicle;

import java.math.BigDecimal;

public class CarFactory extends VehicleFactory {
    private static final BigDecimal HOURLY_RATE = new BigDecimal("3000");
    private static final BigDecimal DAILY_RATE = new BigDecimal("18000");

    @Override
    public Vehicle createVehicle(String id, String model) {
        return new Car(id, model, HOURLY_RATE, DAILY_RATE);
    }
}
