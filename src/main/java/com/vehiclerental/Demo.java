package com.vehiclerental;

import com.vehiclerental.booking.RentalAgreement;
import com.vehiclerental.extras.Extra;
import com.vehiclerental.facade.RentalFacade;
import com.vehiclerental.model.Vehicle;
import com.vehiclerental.model.VehicleType;
import com.vehiclerental.pricing.PricingPlan;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class Demo {
    public static void main(String[] args) {
        RentalFacade rental = new RentalFacade();

        System.out.println("== Facade + Factory Method: adding vehicles ==");
        List<Vehicle> fleet = List.of(
                rental.addVehicle(VehicleType.CAR, "Toyota Camry"),
                rental.addVehicle(VehicleType.BIKE, "Trek FX 2"),
                rental.addVehicle(VehicleType.VAN, "Ford Transit"));
        for (Vehicle vehicle : fleet) {
            System.out.println(vehicle + ": " + vehicle.getHourlyRate() + " KZT/h, "
                    + vehicle.getDailyRate() + " KZT/day");
        }

        System.out.println();
        System.out.println("== Strategy: the same 5 hours, two pricing plans ==");
        RentalAgreement hourly = rental.rent("Aruzhan", "C-1", Duration.ofHours(5), PricingPlan.HOURLY, Set.of());
        System.out.println(hourly);
        rental.returnVehicle(hourly.getId(), Duration.ofHours(5));
        RentalAgreement daily = rental.rent("Aruzhan", "C-1", Duration.ofHours(5), PricingPlan.DAILY, Set.of());
        System.out.println(daily);
        rental.returnVehicle(daily.getId(), Duration.ofHours(5));

        System.out.println();
        System.out.println("== Builder + Decorator: agreement with extras ==");
        RentalAgreement van = rental.rent("Timur", "V-1", Duration.ofDays(2), PricingPlan.DAILY,
                EnumSet.of(Extra.INSURANCE, Extra.GPS));
        System.out.println(van);

        System.out.println();
        System.out.println("== Observer: Dana waits for the van ==");
        rental.notifyWhenAvailable("Dana", VehicleType.VAN);
        try {
            rental.rent("Dana", "V-1", Duration.ofHours(3), PricingPlan.HOURLY, Set.of());
        } catch (IllegalStateException e) {
            System.out.println("Dana cannot rent: " + e.getMessage());
        }
        System.out.println("Timur returns the van after 53 hours");
        BigDecimal total = rental.returnVehicle(van.getId(), Duration.ofHours(53));
        System.out.println("Timur pays " + total + " KZT");

        System.out.println();
        System.out.println("== Fleet dashboard (observer log) ==");
        rental.getDashboard().getEvents().forEach(event -> System.out.println("  " + event));
    }
}
