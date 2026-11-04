package com.vehiclerental.app;

import com.vehiclerental.booking.RentalAgreement;
import com.vehiclerental.extras.Extra;
import com.vehiclerental.facade.RentalFacade;
import com.vehiclerental.model.Vehicle;
import com.vehiclerental.model.VehicleType;
import com.vehiclerental.observer.FleetDashboard;
import com.vehiclerental.pricing.PricingPlan;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;

public class ConsoleApp {
    private static final String EXIT = "0";

    private final RentalFacade facade;
    private final Scanner in;
    private final PrintStream out;

    public ConsoleApp(RentalFacade facade, Scanner in, PrintStream out) {
        this.facade = facade;
        this.in = in;
        this.out = out;
    }

    public void run() {
        out.println("=== Vehicle Rental Service ===");
        while (true) {
            printMenu();
            String choice = ask("> ");
            if (choice.equals(EXIT)) {
                out.println("Goodbye!");
                return;
            }
            try {
                handle(choice);
            } catch (IllegalArgumentException | IllegalStateException e) {
                out.println("Error: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        out.println();
        out.println("1. List vehicles");
        out.println("2. Add vehicle");
        out.println("3. Rent vehicle");
        out.println("4. Return vehicle");
        out.println("5. Notify me when available");
        out.println("6. Active rentals");
        out.println("7. Fleet dashboard");
        out.println("0. Exit");
    }

    private void handle(String choice) {
        switch (choice) {
            case "1" -> listVehicles();
            case "2" -> addVehicle();
            case "3" -> rentVehicle();
            case "4" -> returnVehicle();
            case "5" -> subscribe();
            case "6" -> listRentals();
            case "7" -> showDashboard();
            default -> out.println("Unknown option: " + choice);
        }
    }

    private void listVehicles() {
        for (Vehicle vehicle : facade.getVehicles()) {
            out.printf("%-4s %-5s %-16s %5s KZT/h %6s KZT/day  %s%n",
                    vehicle.getId(), vehicle.getType(), vehicle.getModel(),
                    vehicle.getHourlyRate(), vehicle.getDailyRate(),
                    vehicle.isAvailable() ? "available" : "rented");
        }
    }

    private void addVehicle() {
        VehicleType type = readType();
        String model = ask("Model: ");
        out.println("Added " + facade.addVehicle(type, model));
    }

    private void rentVehicle() {
        String customer = ask("Customer name: ");
        String vehicleId = ask("Vehicle id: ").toUpperCase(Locale.ROOT);
        Duration duration = Duration.ofHours(readInt("Hours: "));
        PricingPlan plan = readPlan();
        Set<Extra> extras = readExtras();
        RentalAgreement agreement = facade.rent(customer, vehicleId, duration, plan, extras);
        out.println("Created " + agreement);
    }

    private void returnVehicle() {
        String rentalId = ask("Rental id: ").toUpperCase(Locale.ROOT);
        Duration usedFor = Duration.ofHours(readInt("Hours used: "));
        BigDecimal total = facade.returnVehicle(rentalId, usedFor);
        out.println("Returned. Total to pay: " + total + " KZT");
    }

    private void subscribe() {
        String name = ask("Your name: ");
        VehicleType type = readType();
        facade.notifyWhenAvailable(name, type);
        out.println(name + " will be notified when a " + type.name().toLowerCase(Locale.ROOT) + " is available");
    }

    private void listRentals() {
        List<RentalAgreement> rentals = facade.getActiveRentals();
        if (rentals.isEmpty()) {
            out.println("No active rentals");
        }
        rentals.forEach(out::println);
    }

    private void showDashboard() {
        FleetDashboard dashboard = facade.getDashboard();
        for (VehicleType type : VehicleType.values()) {
            out.println(type + ": " + dashboard.getAvailableCount(type) + " available");
        }
        List<String> events = dashboard.getEvents();
        out.println("Last events:");
        events.subList(Math.max(0, events.size() - 5), events.size())
                .forEach(event -> out.println("  " + event));
    }

    private VehicleType readType() {
        String input = ask("Type (car, bike, van): ");
        try {
            return VehicleType.valueOf(input.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown vehicle type: " + input);
        }
    }

    private PricingPlan readPlan() {
        return switch (ask("Pricing (1 - hourly, 2 - daily): ")) {
            case "1" -> PricingPlan.HOURLY;
            case "2" -> PricingPlan.DAILY;
            default -> throw new IllegalArgumentException("Unknown pricing option");
        };
    }

    private Set<Extra> readExtras() {
        String input = ask("Extras (1 - insurance, 2 - child seat, 3 - GPS, e.g. 1,3 or empty): ");
        Set<Extra> extras = EnumSet.noneOf(Extra.class);
        if (input.isEmpty()) {
            return extras;
        }
        for (String part : input.split(",")) {
            int index = parseNumber(part) - 1;
            if (index < 0 || index >= Extra.values().length) {
                throw new IllegalArgumentException("Unknown extra: " + part.trim());
            }
            extras.add(Extra.values()[index]);
        }
        return extras;
    }

    private int readInt(String prompt) {
        return parseNumber(ask(prompt));
    }

    private static int parseNumber(String input) {
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Not a number: " + input.trim());
        }
    }

    private String ask(String prompt) {
        out.print(prompt);
        return in.hasNextLine() ? in.nextLine().trim() : EXIT;
    }
}
