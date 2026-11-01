package com.vehiclerental.observer;

import com.vehiclerental.model.Vehicle;

public interface AvailabilityObserver {
    void onAvailabilityChanged(Vehicle vehicle);
}
