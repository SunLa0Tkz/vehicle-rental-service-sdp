package com.vehiclerental.model;

import java.math.BigDecimal;
import java.util.Objects;

public abstract class Vehicle {
    private final String id;
    private final String model;
    private final BigDecimal hourlyRate;
    private final BigDecimal dailyRate;
    private boolean available = true;

    protected Vehicle(String id, String model, BigDecimal hourlyRate, BigDecimal dailyRate) {
        this.id = Objects.requireNonNull(id);
        this.model = Objects.requireNonNull(model);
        this.hourlyRate = Objects.requireNonNull(hourlyRate);
        this.dailyRate = Objects.requireNonNull(dailyRate);
    }

    public abstract VehicleType getType();

    public String getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return getType() + " " + id + " (" + model + ")";
    }
}
