package com.vehiclerental.observer;

import com.vehiclerental.model.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Customer implements AvailabilityObserver {
    private final String name;
    private final List<String> inbox = new ArrayList<>();

    public Customer(String name) {
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public void onAvailabilityChanged(Vehicle vehicle) {
        if (vehicle.isAvailable()) {
            String message = vehicle + " is now available";
            inbox.add(message);
            System.out.println("  [notify " + name + "] " + message);
        }
    }

    public String getName() {
        return name;
    }

    public List<String> getInbox() {
        return List.copyOf(inbox);
    }
}
