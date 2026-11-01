package com.vehiclerental.observer;

import com.vehiclerental.model.Vehicle;
import com.vehiclerental.model.VehicleType;
import com.vehiclerental.service.VehicleInventory;

public class FleetDashboard implements AvailabilityObserver {
    private final VehicleInventory inventory;

    public FleetDashboard(VehicleInventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public void onAvailabilityChanged(Vehicle vehicle) {
        System.out.println("  [dashboard] available: " + inventory.availableCounts());
    }

    public long getAvailableCount(VehicleType type) {
        return inventory.availableCounts().getOrDefault(type, 0L);
    }
}
