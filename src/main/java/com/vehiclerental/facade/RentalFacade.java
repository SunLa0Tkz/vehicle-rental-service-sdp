package com.vehiclerental.facade;

import com.vehiclerental.booking.RentalAgreement;
import com.vehiclerental.booking.RentalAgreementBuilder;
import com.vehiclerental.extras.Extra;
import com.vehiclerental.factory.VehicleFactory;
import com.vehiclerental.model.Vehicle;
import com.vehiclerental.model.VehicleType;
import com.vehiclerental.observer.Customer;
import com.vehiclerental.observer.FleetDashboard;
import com.vehiclerental.pricing.PricingPlan;
import com.vehiclerental.service.RentalService;
import com.vehiclerental.service.VehicleInventory;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RentalFacade {
    private final VehicleInventory inventory = new VehicleInventory();
    private final RentalService rentalService = new RentalService(inventory);
    private final FleetDashboard dashboard = new FleetDashboard(inventory);
    private final Map<String, Customer> customers = new HashMap<>();
    private final Map<VehicleType, Integer> vehicleCounters = new EnumMap<>(VehicleType.class);
    private int nextRentalNumber = 1;

    public RentalFacade() {
        inventory.subscribeToAll(dashboard);
    }

    public Vehicle addVehicle(VehicleType type, String model) {
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException("Model is required");
        }
        int number = vehicleCounters.merge(type, 1, Integer::sum);
        String id = type.name().charAt(0) + "-" + number;
        Vehicle vehicle = VehicleFactory.forType(type).createVehicle(id, model.trim());
        inventory.add(vehicle);
        return vehicle;
    }

    public RentalAgreement rent(String customerName, String vehicleId, Duration duration,
                                PricingPlan plan, Set<Extra> extras) {
        Vehicle vehicle = inventory.find(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown vehicle: " + vehicleId));
        RentalAgreementBuilder builder = new RentalAgreementBuilder()
                .id("R-" + nextRentalNumber)
                .customer(customerName)
                .vehicle(vehicle)
                .duration(duration)
                .pricing(plan.createStrategy());
        extras.forEach(builder::extra);

        RentalAgreement agreement = builder.build();
        rentalService.rent(agreement);
        nextRentalNumber++;
        return agreement;
    }

    public BigDecimal returnVehicle(String rentalId, Duration usedFor) {
        return rentalService.returnVehicle(rentalId, usedFor);
    }

    public Customer notifyWhenAvailable(String customerName, VehicleType type) {
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        Customer customer = customers.computeIfAbsent(customerName.trim(), Customer::new);
        inventory.subscribe(type, customer);
        return customer;
    }

    public List<Vehicle> getVehicles() {
        return inventory.findAll();
    }

    public List<RentalAgreement> getActiveRentals() {
        return rentalService.getActiveRentals();
    }

    public FleetDashboard getDashboard() {
        return dashboard;
    }
}
