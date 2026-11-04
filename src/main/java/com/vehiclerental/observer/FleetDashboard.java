package com.vehiclerental.observer;

import com.vehiclerental.model.Vehicle;
import com.vehiclerental.model.VehicleType;
import com.vehiclerental.service.VehicleInventory;

import java.util.ArrayList;
import java.util.List;

public class FleetDashboard implements AvailabilityObserver {
    private final VehicleInventory inventory;
    private final List<String> events = new ArrayList<>();

    public FleetDashboard(VehicleInventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public void onAvailabilityChanged(Vehicle vehicle) {
        events.add(vehicle + (vehicle.isAvailable() ? " is available" : " is rented"));
    }

    public long getAvailableCount(VehicleType type) {
        return inventory.availableCounts().getOrDefault(type, 0L);
    }

    public List<String> getEvents() {
        return List.copyOf(events);
    }
}
