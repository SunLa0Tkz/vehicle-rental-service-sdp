package com.vehiclerental.service;

import com.vehiclerental.model.Vehicle;
import com.vehiclerental.model.VehicleType;
import com.vehiclerental.observer.AvailabilityObserver;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VehicleInventory {
    private final Map<String, Vehicle> vehicles = new LinkedHashMap<>();
    private final Map<VehicleType, List<AvailabilityObserver>> observers = new EnumMap<>(VehicleType.class);

    public void subscribe(VehicleType type, AvailabilityObserver observer) {
        List<AvailabilityObserver> subscribers = observers.computeIfAbsent(type, t -> new ArrayList<>());
        if (!subscribers.contains(observer)) {
            subscribers.add(observer);
        }
    }

    public void subscribeToAll(AvailabilityObserver observer) {
        for (VehicleType type : VehicleType.values()) {
            subscribe(type, observer);
        }
    }

    public void unsubscribe(VehicleType type, AvailabilityObserver observer) {
        List<AvailabilityObserver> subscribers = observers.get(type);
        if (subscribers != null) {
            subscribers.remove(observer);
        }
    }

    public void add(Vehicle vehicle) {
        if (vehicles.putIfAbsent(vehicle.getId(), vehicle) != null) {
            throw new IllegalArgumentException("Duplicate vehicle id: " + vehicle.getId());
        }
        notifyObservers(vehicle);
    }

    public Optional<Vehicle> find(String id) {
        return Optional.ofNullable(vehicles.get(id));
    }

    public List<Vehicle> findAll() {
        return List.copyOf(vehicles.values());
    }

    public Map<VehicleType, Long> availableCounts() {
        Map<VehicleType, Long> counts = new EnumMap<>(VehicleType.class);
        for (Vehicle vehicle : vehicles.values()) {
            if (vehicle.isAvailable()) {
                counts.merge(vehicle.getType(), 1L, Long::sum);
            }
        }
        return counts;
    }

    public void setAvailability(Vehicle vehicle, boolean available) {
        vehicle.setAvailable(available);
        notifyObservers(vehicle);
    }

    private void notifyObservers(Vehicle vehicle) {
        List<AvailabilityObserver> subscribers = observers.getOrDefault(vehicle.getType(), List.of());
        for (AvailabilityObserver observer : List.copyOf(subscribers)) {
            observer.onAvailabilityChanged(vehicle);
        }
    }
}
