package com.vehiclerental;

import com.vehiclerental.app.ConsoleApp;
import com.vehiclerental.app.SampleFleet;
import com.vehiclerental.facade.RentalFacade;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        RentalFacade facade = new RentalFacade();
        SampleFleet.addTo(facade);

        new ConsoleApp(facade, new Scanner(System.in), System.out).run();
    }
}
