package com.vehiclerental;

import com.vehiclerental.app.ConsoleApp;
import com.vehiclerental.facade.RentalFacade;
import com.vehiclerental.model.VehicleType;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        RentalFacade facade = new RentalFacade();
        facade.addVehicle(VehicleType.CAR, "Toyota Camry");
        facade.addVehicle(VehicleType.CAR, "Hyundai Elantra");
        facade.addVehicle(VehicleType.BIKE, "Trek FX 2");
        facade.addVehicle(VehicleType.BIKE, "Giant Escape 3");
        facade.addVehicle(VehicleType.VAN, "Ford Transit");

        new ConsoleApp(facade, new Scanner(System.in), System.out).run();
    }
}
