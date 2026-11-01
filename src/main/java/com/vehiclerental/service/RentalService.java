package com.vehiclerental.service;

import com.vehiclerental.model.Vehicle;
import com.vehiclerental.pricing.PricingStrategy;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class RentalService {
    private final VehicleInventory inventory;
    private final Map<String, Rental> activeRentals = new HashMap<>();
    private int nextRentalNumber = 1;

    public RentalService(VehicleInventory inventory) {
        this.inventory = inventory;
    }

    public Rental rent(String vehicleId, String customerName, Duration duration, PricingStrategy pricing) {
        if (duration.isNegative() || duration.isZero()) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        Vehicle vehicle = inventory.find(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown vehicle: " + vehicleId));
        if (!vehicle.isAvailable()) {
            throw new IllegalStateException(vehicle + " is already rented");
        }

        BigDecimal cost = pricing.calculate(vehicle, duration);
        Rental rental = new Rental("R-" + nextRentalNumber++, vehicle, customerName, duration, pricing.getName(), cost);
        activeRentals.put(rental.id(), rental);
        inventory.setAvailability(vehicle, false);
        return rental;
    }

    public void returnVehicle(String rentalId) {
        Rental rental = activeRentals.remove(rentalId);
        if (rental == null) {
            throw new IllegalArgumentException("No active rental: " + rentalId);
        }
        inventory.setAvailability(rental.vehicle(), true);
    }
}
