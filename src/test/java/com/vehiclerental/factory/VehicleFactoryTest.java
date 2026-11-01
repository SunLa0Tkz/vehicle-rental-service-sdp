package com.vehiclerental.factory;

import com.vehiclerental.model.Bike;
import com.vehiclerental.model.Car;
import com.vehiclerental.model.Van;
import com.vehiclerental.model.Vehicle;
import com.vehiclerental.model.VehicleType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VehicleFactoryTest {
    @Test
    void createsMatchingVehicleForEachType() {
        assertInstanceOf(Car.class, VehicleFactory.forType(VehicleType.CAR).createVehicle("C-1", "Camry"));
        assertInstanceOf(Bike.class, VehicleFactory.forType(VehicleType.BIKE).createVehicle("B-1", "Trek"));
        assertInstanceOf(Van.class, VehicleFactory.forType(VehicleType.VAN).createVehicle("V-1", "Transit"));
    }

    @Test
    void newVehicleIsAvailableWithGivenIdentity() {
        Vehicle vehicle = new CarFactory().createVehicle("C-1", "Camry");

        assertEquals("C-1", vehicle.getId());
        assertEquals("Camry", vehicle.getModel());
        assertEquals(VehicleType.CAR, vehicle.getType());
        assertTrue(vehicle.isAvailable());
    }
}
