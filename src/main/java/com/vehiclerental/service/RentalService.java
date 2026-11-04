package com.vehiclerental.service;

import com.vehiclerental.booking.RentalAgreement;
import com.vehiclerental.model.Vehicle;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RentalService {
    private final VehicleInventory inventory;
    private final Map<String, RentalAgreement> activeRentals = new LinkedHashMap<>();

    public RentalService(VehicleInventory inventory) {
        this.inventory = inventory;
    }

    public void rent(RentalAgreement agreement) {
        Vehicle vehicle = agreement.getVehicle();
        if (!vehicle.isAvailable()) {
            throw new IllegalStateException(vehicle + " is already rented");
        }
        if (activeRentals.putIfAbsent(agreement.getId(), agreement) != null) {
            throw new IllegalArgumentException("Duplicate rental id: " + agreement.getId());
        }
        inventory.setAvailability(vehicle, false);
    }

    public BigDecimal returnVehicle(String rentalId, Duration usedFor) {
        RentalAgreement agreement = activeRentals.get(rentalId);
        if (agreement == null) {
            throw new IllegalArgumentException("No active rental: " + rentalId);
        }
        BigDecimal total = agreement.finalPrice(usedFor);
        activeRentals.remove(rentalId);
        inventory.setAvailability(agreement.getVehicle(), true);
        return total;
    }

    public List<RentalAgreement> getActiveRentals() {
        return List.copyOf(activeRentals.values());
    }
}
