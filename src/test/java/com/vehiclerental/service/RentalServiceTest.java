package com.vehiclerental.service;

import com.vehiclerental.booking.RentalAgreement;
import com.vehiclerental.booking.RentalAgreementBuilder;
import com.vehiclerental.factory.BikeFactory;
import com.vehiclerental.factory.VanFactory;
import com.vehiclerental.model.Vehicle;
import com.vehiclerental.model.VehicleType;
import com.vehiclerental.observer.Customer;
import com.vehiclerental.observer.FleetDashboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RentalServiceTest {
    private VehicleInventory inventory;
    private RentalService service;
    private Vehicle van;

    @BeforeEach
    void setUp() {
        inventory = new VehicleInventory();
        service = new RentalService(inventory);
        van = new VanFactory().createVehicle("V-1", "Transit");
        inventory.add(van);
    }

    @Test
    void rentMarksVehicleAsRented() {
        service.rent(agreement("R-1"));

        assertFalse(van.isAvailable());
        assertEquals(1, service.getActiveRentals().size());
    }

    @Test
    void rentedVehicleCannotBeRentedAgain() {
        service.rent(agreement("R-1"));

        assertThrows(IllegalStateException.class, () -> service.rent(agreement("R-2")));
    }

    @Test
    void returnChargesLongerOfBookedAndUsedTime() {
        service.rent(agreement("R-1"));

        assertEquals(new BigDecimal("13500"), service.returnVehicle("R-1", Duration.ofHours(3)));
        assertTrue(van.isAvailable());
        assertTrue(service.getActiveRentals().isEmpty());
    }

    @Test
    void invalidReturnKeepsRentalOpen() {
        service.rent(agreement("R-1"));

        assertThrows(IllegalArgumentException.class, () -> service.returnVehicle("R-1", Duration.ZERO));
        assertFalse(van.isAvailable());
        assertEquals(1, service.getActiveRentals().size());
    }

    @Test
    void returningUnknownOrClosedRentalFails() {
        assertThrows(IllegalArgumentException.class, () -> service.returnVehicle("R-404", Duration.ofHours(1)));

        service.rent(agreement("R-1"));
        service.returnVehicle("R-1", Duration.ofHours(2));

        assertThrows(IllegalArgumentException.class, () -> service.returnVehicle("R-1", Duration.ofHours(2)));
    }

    @Test
    void customerIsNotifiedOnlyWhenSubscribedTypeBecomesAvailable() {
        Customer vanWatcher = new Customer("Dana");
        Customer bikeWatcher = new Customer("Aidar");
        inventory.subscribe(VehicleType.VAN, vanWatcher);
        inventory.subscribe(VehicleType.BIKE, bikeWatcher);

        service.rent(agreement("R-1"));
        assertTrue(vanWatcher.getInbox().isEmpty());

        service.returnVehicle("R-1", Duration.ofHours(2));

        assertEquals(List.of("VAN V-1 (Transit) is now available"), vanWatcher.getInbox());
        assertTrue(bikeWatcher.getInbox().isEmpty());
    }

    @Test
    void unsubscribedCustomerGetsNoNotification() {
        Customer customer = new Customer("Dana");
        inventory.subscribe(VehicleType.VAN, customer);
        inventory.unsubscribe(VehicleType.VAN, customer);

        service.rent(agreement("R-1"));
        service.returnVehicle("R-1", Duration.ofHours(2));

        assertTrue(customer.getInbox().isEmpty());
    }

    @Test
    void subscribingTwiceNotifiesOnce() {
        Customer customer = new Customer("Dana");
        inventory.subscribe(VehicleType.VAN, customer);
        inventory.subscribe(VehicleType.VAN, customer);

        service.rent(agreement("R-1"));
        service.returnVehicle("R-1", Duration.ofHours(2));

        assertEquals(1, customer.getInbox().size());
    }

    @Test
    void dashboardTracksCountsAndEvents() {
        FleetDashboard dashboard = new FleetDashboard(inventory);
        inventory.subscribeToAll(dashboard);
        inventory.add(new BikeFactory().createVehicle("B-1", "Trek"));
        assertEquals(1, dashboard.getAvailableCount(VehicleType.BIKE));
        assertEquals(1, dashboard.getAvailableCount(VehicleType.VAN));

        service.rent(agreement("R-1"));

        assertEquals(0, dashboard.getAvailableCount(VehicleType.VAN));
        assertEquals(List.of("BIKE B-1 (Trek) is available", "VAN V-1 (Transit) is rented"), dashboard.getEvents());
    }

    private RentalAgreement agreement(String id) {
        return new RentalAgreementBuilder()
                .id(id)
                .customer("Timur")
                .vehicle(van)
                .duration(Duration.ofHours(2))
                .build();
    }
}
