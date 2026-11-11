package com.vehiclerental.app;

import com.vehiclerental.facade.RentalFacade;
import com.vehiclerental.model.VehicleType;

public final class SampleFleet {
    private SampleFleet() {
    }

    public static void addTo(RentalFacade facade) {
        facade.addVehicle(VehicleType.CAR, "Toyota Camry");
        facade.addVehicle(VehicleType.CAR, "Hyundai Elantra");
        facade.addVehicle(VehicleType.BIKE, "Trek FX 2");
        facade.addVehicle(VehicleType.BIKE, "Giant Escape 3");
        facade.addVehicle(VehicleType.VAN, "Ford Transit");
    }
}
