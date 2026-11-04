package com.vehiclerental.app;

import com.vehiclerental.facade.RentalFacade;
import com.vehiclerental.model.VehicleType;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleAppTest {
    @Test
    void rentAndReturnThroughMenu() {
        RentalFacade facade = new RentalFacade();
        facade.addVehicle(VehicleType.CAR, "Toyota Camry");

        String output = run(facade, "3", "Aruzhan", "c-1", "5", "2", "1", "6", "4", "r-1", "6", "0");

        assertTrue(output.contains("Created R-1: Aruzhan -> CAR C-1 (Toyota Camry), 5h, daily + insurance, 20000 KZT"));
        assertTrue(output.contains("Returned. Total to pay: 20000 KZT"));
        assertTrue(facade.getActiveRentals().isEmpty());
    }

    @Test
    void invalidInputShowsErrorAndKeepsRunning() {
        String output = run(new RentalFacade(), "2", "truck", "9", "0");

        assertTrue(output.contains("Error: Unknown vehicle type: truck"));
        assertTrue(output.contains("Unknown option: 9"));
        assertTrue(output.contains("Goodbye!"));
    }

    @Test
    void endOfInputExits() {
        String output = run(new RentalFacade(), "1");

        assertTrue(output.contains("Goodbye!"));
    }

    private String run(RentalFacade facade, String... lines) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Scanner input = new Scanner(String.join("\n", lines) + "\n");
        new ConsoleApp(facade, input, new PrintStream(buffer, true, StandardCharsets.UTF_8)).run();
        return buffer.toString(StandardCharsets.UTF_8);
    }
}
