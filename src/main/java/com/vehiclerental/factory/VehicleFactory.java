package com.vehiclerental.factory;

import com.vehiclerental.model.Vehicle;
import com.vehiclerental.model.VehicleType;

public abstract class VehicleFactory {
    public abstract Vehicle createVehicle(String id, String model);

    public static VehicleFactory forType(VehicleType type) {
        return switch (type) {
            case CAR -> new CarFactory();
            case BIKE -> new BikeFactory();
            case VAN -> new VanFactory();
        };
    }
}
