package com.vehiclerental;

import com.vehiclerental.factory.VehicleFactory;
import com.vehiclerental.model.VehicleType;
import com.vehiclerental.observer.Customer;
import com.vehiclerental.observer.FleetDashboard;
import com.vehiclerental.pricing.DailyPricing;
import com.vehiclerental.pricing.HourlyPricing;
import com.vehiclerental.service.Rental;
import com.vehiclerental.service.RentalService;
import com.vehiclerental.service.VehicleInventory;

import java.time.Duration;

public class Main {
    public static void main(String[] args) {
        VehicleInventory inventory = new VehicleInventory();
        RentalService rentals = new RentalService(inventory);

        inventory.subscribeToAll(new FleetDashboard(inventory));

        System.out.println("== Factory: stocking the fleet ==");
        inventory.add(VehicleFactory.forType(VehicleType.CAR).createVehicle("C-1", "Toyota Camry"));
        inventory.add(VehicleFactory.forType(VehicleType.BIKE).createVehicle("B-1", "Trek FX 2"));
        inventory.add(VehicleFactory.forType(VehicleType.VAN).createVehicle("V-1", "Ford Transit"));

        System.out.println("\n== Strategy: same vehicle, different pricing ==");
        Rental hourly = rentals.rent("C-1", "Aruzhan", Duration.ofHours(5), new HourlyPricing());
        System.out.println(hourly);
        rentals.returnVehicle(hourly.id());
        Rental daily = rentals.rent("C-1", "Aruzhan", Duration.ofHours(5), new DailyPricing());
        System.out.println(daily);
        rentals.returnVehicle(daily.id());

        System.out.println("\n== Observer: waiting for a rented van ==");
        Rental van = rentals.rent("V-1", "Timur", Duration.ofDays(2), new DailyPricing());
        System.out.println(van);
        inventory.subscribe(VehicleType.VAN, new Customer("Dana"));
        try {
            rentals.rent("V-1", "Dana", Duration.ofHours(3), new HourlyPricing());
        } catch (IllegalStateException e) {
            System.out.println("Dana cannot rent: " + e.getMessage());
        }
        rentals.returnVehicle(van.id());
    }
}
