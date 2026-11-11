package com.vehiclerental;

import com.vehiclerental.app.RentalPanel;
import com.vehiclerental.app.SampleFleet;
import com.vehiclerental.facade.RentalFacade;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class GuiMain {
    public static void main(String[] args) {
        RentalFacade facade = new RentalFacade();
        SampleFleet.addTo(facade);

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
                System.out.println("Using default look and feel");
            }
            JFrame frame = new JFrame("Vehicle Rental Service");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new RentalPanel(facade));
            frame.setSize(1180, 780);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
