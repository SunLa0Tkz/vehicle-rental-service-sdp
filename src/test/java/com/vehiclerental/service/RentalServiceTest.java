package com.vehiclerental.service;

import com.vehiclerental.factory.BikeFactory;
import com.vehiclerental.factory.VanFactory;
import com.vehiclerental.model.VehicleType;
import com.vehiclerental.observer.Customer;
import com.vehiclerental.observer.FleetDashboard;
import com.vehiclerental.pricing.DailyPricing;
import com.vehiclerental.pricing.HourlyPricing;
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

    @BeforeEach
    void setUp() {
        inventory = new VehicleInventory();
        service = new RentalService(inventory);
        inventory.add(new VanFactory().createVehicle("V-1", "Transit"));
    }

    @Test
    void rentAppliesChosenStrategy() {
        Rental hourly = service.rent("V-1", "Timur", Duration.ofHours(2), new HourlyPricing());
        service.returnVehicle(hourly.id());
        Rental daily = service.rent("V-1", "Timur", Duration.ofHours(2), new DailyPricing());

        assertEquals(new BigDecimal("9000"), hourly.cost());
        assertEquals(new BigDecimal("27000"), daily.cost());
    }

    @Test
    void rentedVehicleCannotBeRentedAgainUntilReturned() {
        Rental rental = service.rent("V-1", "Timur", Duration.ofHours(1), new HourlyPricing());
        assertFalse(rental.vehicle().isAvailable());
        assertThrows(IllegalStateException.class,
                () -> service.rent("V-1", "Dana", Duration.ofHours(1), new HourlyPricing()));

        service.returnVehicle(rental.id());

        assertTrue(rental.vehicle().isAvailable());
    }

    @Test
    void rejectsInvalidRequests() {
        assertThrows(IllegalArgumentException.class,
                () -> service.rent("missing", "Timur", Duration.ofHours(1), new HourlyPricing()));
        assertThrows(IllegalArgumentException.class,
                () -> service.rent("V-1", "Timur", Duration.ZERO, new HourlyPricing()));
        assertThrows(IllegalArgumentException.class, () -> service.returnVehicle("R-404"));
    }

    @Test
    void returningTwiceFails() {
        Rental rental = service.rent("V-1", "Timur", Duration.ofHours(1), new HourlyPricing());
        service.returnVehicle(rental.id());

        assertThrows(IllegalArgumentException.class, () -> service.returnVehicle(rental.id()));
    }

    @Test
    void customerIsNotifiedOnlyWhenSubscribedTypeBecomesAvailable() {
        Customer vanWatcher = new Customer("Dana");
        Customer bikeWatcher = new Customer("Aidar");
        inventory.subscribe(VehicleType.VAN, vanWatcher);
        inventory.subscribe(VehicleType.BIKE, bikeWatcher);

        Rental rental = service.rent("V-1", "Timur", Duration.ofHours(1), new HourlyPricing());
        assertTrue(vanWatcher.getInbox().isEmpty());

        service.returnVehicle(rental.id());

        assertEquals(List.of("VAN V-1 (Transit) is now available"), vanWatcher.getInbox());
        assertTrue(bikeWatcher.getInbox().isEmpty());
    }

    @Test
    void unsubscribedCustomerGetsNoNotification() {
        Customer customer = new Customer("Dana");
        inventory.subscribe(VehicleType.VAN, customer);
        inventory.unsubscribe(VehicleType.VAN, customer);

        Rental rental = service.rent("V-1", "Timur", Duration.ofHours(1), new HourlyPricing());
        service.returnVehicle(rental.id());

        assertTrue(customer.getInbox().isEmpty());
    }

    @Test
    void dashboardTracksAvailableCountPerType() {
        FleetDashboard dashboard = new FleetDashboard(inventory);
        inventory.subscribeToAll(dashboard);
        inventory.add(new BikeFactory().createVehicle("B-1", "Trek"));
        inventory.add(new VanFactory().createVehicle("V-2", "Sprinter"));
        assertEquals(1, dashboard.getAvailableCount(VehicleType.BIKE));
        assertEquals(2, dashboard.getAvailableCount(VehicleType.VAN));

        Rental rental = service.rent("V-2", "Timur", Duration.ofHours(1), new HourlyPricing());
        assertEquals(1, dashboard.getAvailableCount(VehicleType.VAN));

        service.returnVehicle(rental.id());
        assertEquals(2, dashboard.getAvailableCount(VehicleType.VAN));
    }
}
