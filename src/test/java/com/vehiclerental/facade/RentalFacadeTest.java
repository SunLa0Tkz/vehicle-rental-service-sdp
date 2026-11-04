package com.vehiclerental.facade;

import com.vehiclerental.booking.RentalAgreement;
import com.vehiclerental.extras.Extra;
import com.vehiclerental.model.Car;
import com.vehiclerental.model.Vehicle;
import com.vehiclerental.model.VehicleType;
import com.vehiclerental.observer.Customer;
import com.vehiclerental.pricing.PricingPlan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RentalFacadeTest {
    private RentalFacade facade;

    @BeforeEach
    void setUp() {
        facade = new RentalFacade();
        facade.addVehicle(VehicleType.CAR, "Toyota Camry");
        facade.addVehicle(VehicleType.VAN, "Ford Transit");
    }

    @Test
    void addVehicleNumbersIdsPerType() {
        Vehicle secondCar = facade.addVehicle(VehicleType.CAR, "Hyundai Elantra");
        Vehicle bike = facade.addVehicle(VehicleType.BIKE, "Trek FX 2");

        assertEquals("C-2", secondCar.getId());
        assertInstanceOf(Car.class, secondCar);
        assertEquals("B-1", bike.getId());
        assertEquals(4, facade.getVehicles().size());
    }

    @Test
    void rentBuildsAgreementWithPlanAndExtras() {
        RentalAgreement agreement = facade.rent("Aruzhan", "C-1", Duration.ofDays(1), PricingPlan.DAILY,
                EnumSet.of(Extra.CHILD_SEAT));

        assertEquals("R-1", agreement.getId());
        assertEquals("daily + child seat", agreement.getPricing().getName());
        assertEquals(new BigDecimal("19500"), agreement.getPrice());
        assertEquals(List.of(agreement), facade.getActiveRentals());
    }

    @Test
    void failedRentDoesNotTakeRentalNumber() {
        assertThrows(IllegalArgumentException.class,
                () -> facade.rent("Aruzhan", "X-9", Duration.ofHours(1), PricingPlan.HOURLY, Set.of()));

        RentalAgreement agreement = facade.rent("Aruzhan", "C-1", Duration.ofHours(1), PricingPlan.HOURLY, Set.of());

        assertEquals("R-1", agreement.getId());
    }

    @Test
    void returnVehicleGivesFinalPrice() {
        RentalAgreement agreement = facade.rent("Timur", "V-1", Duration.ofHours(2), PricingPlan.HOURLY, Set.of());

        assertEquals(new BigDecimal("13500"), facade.returnVehicle(agreement.getId(), Duration.ofHours(3)));
        assertTrue(facade.getActiveRentals().isEmpty());
    }

    @Test
    void waitingCustomerIsNotifiedOnReturn() {
        RentalAgreement agreement = facade.rent("Timur", "V-1", Duration.ofHours(2), PricingPlan.HOURLY, Set.of());
        Customer dana = facade.notifyWhenAvailable("Dana", VehicleType.VAN);

        facade.returnVehicle(agreement.getId(), Duration.ofHours(2));

        assertEquals(List.of("VAN V-1 (Ford Transit) is now available"), dana.getInbox());
    }

    @Test
    void sameNameReusesCustomer() {
        Customer first = facade.notifyWhenAvailable("Dana", VehicleType.VAN);
        Customer second = facade.notifyWhenAvailable("Dana", VehicleType.CAR);

        assertSame(first, second);
    }

    @Test
    void rejectsBlankInput() {
        assertThrows(IllegalArgumentException.class, () -> facade.addVehicle(VehicleType.CAR, " "));
        assertThrows(IllegalArgumentException.class, () -> facade.notifyWhenAvailable("", VehicleType.CAR));
    }
}
