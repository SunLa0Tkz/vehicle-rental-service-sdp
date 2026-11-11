package com.vehiclerental.app;

import com.vehiclerental.booking.RentalAgreement;
import com.vehiclerental.extras.Extra;
import com.vehiclerental.facade.RentalFacade;
import com.vehiclerental.model.Vehicle;
import com.vehiclerental.model.VehicleType;
import com.vehiclerental.observer.Customer;
import com.vehiclerental.observer.FleetDashboard;
import com.vehiclerental.pricing.PricingPlan;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

public class RentalPanel extends JPanel {
    private final RentalFacade facade;
    private final Map<Customer, Integer> shownMessages = new LinkedHashMap<>();

    private final DefaultTableModel vehicles = new DefaultTableModel(
            new String[]{"ID", "Type", "Model", "Per hour, KZT", "Per day, KZT", "Status"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultListModel<String> rentals = new DefaultListModel<>();
    private final JTextArea log = new JTextArea();
    private final JLabel summary = new JLabel();

    private final JTextField rentCustomer = new JTextField();
    private final JComboBox<String> rentVehicle = new JComboBox<>();
    private final JSpinner rentHours = new JSpinner(new SpinnerNumberModel(5, 1, 720, 1));
    private final JComboBox<PricingPlan> rentPlan = new JComboBox<>(PricingPlan.values());
    private final JCheckBox insurance = new JCheckBox("Insurance");
    private final JCheckBox childSeat = new JCheckBox("Child seat");
    private final JCheckBox gps = new JCheckBox("GPS");

    private final JComboBox<String> returnRental = new JComboBox<>();
    private final JSpinner returnHours = new JSpinner(new SpinnerNumberModel(5, 1, 720, 1));

    private final JTextField notifyName = new JTextField();
    private final JComboBox<VehicleType> notifyType = new JComboBox<>(VehicleType.values());

    private final JComboBox<VehicleType> addType = new JComboBox<>(VehicleType.values());
    private final JTextField addModel = new JTextField();

    public RentalPanel(RentalFacade facade) {
        super(new BorderLayout(10, 10));
        this.facade = facade;
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(fleetPanel(), BorderLayout.CENTER);
        add(controlsPanel(), BorderLayout.EAST);
        add(bottomPanel(), BorderLayout.SOUTH);
        refresh();
    }

    private JComponent fleetPanel() {
        JTable table = new JTable(vehicles);
        table.setRowHeight(24);
        summary.setFont(summary.getFont().deriveFont(Font.BOLD));

        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Fleet"));
        panel.add(summary, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JComponent controlsPanel() {
        JPanel extras = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        extras.add(insurance);
        extras.add(childSeat);
        extras.add(gps);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(380, 0));
        panel.add(section("Rent a vehicle", button("Rent", this::rent),
                "Customer", rentCustomer, "Vehicle", rentVehicle, "Hours", rentHours,
                "Pricing", rentPlan, "Extras", extras));
        panel.add(section("Return a vehicle", button("Return", this::returnVehicle),
                "Rental", returnRental, "Hours used", returnHours));
        panel.add(section("Notify me when available", button("Subscribe", this::subscribe),
                "Name", notifyName, "Type", notifyType));
        panel.add(section("Add a vehicle", button("Add", this::addVehicle),
                "Type", addType, "Model", addModel));
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JComponent bottomPanel() {
        log.setEditable(false);
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        log.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane rentalsScroll = new JScrollPane(new JList<>(rentals));
        rentalsScroll.setBorder(BorderFactory.createTitledBorder("Active rentals"));
        JScrollPane logScroll = new JScrollPane(log);
        logScroll.setBorder(BorderFactory.createTitledBorder("Log"));

        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setPreferredSize(new Dimension(0, 170));
        panel.add(rentalsScroll);
        panel.add(logScroll);
        return panel;
    }

    private JPanel section(String title, JButton action, Object... rows) {
        JPanel fields = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 0, 3, 8);
        c.anchor = GridBagConstraints.WEST;
        for (int i = 0; i < rows.length; i += 2) {
            c.gridy = i / 2;
            c.gridx = 0;
            c.weightx = 0;
            c.fill = GridBagConstraints.NONE;
            fields.add(new JLabel((String) rows[i]), c);
            c.gridx = 1;
            c.weightx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            fields.add((JComponent) rows[i + 1], c);
        }
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttons.add(action);

        JPanel section = new JPanel(new BorderLayout(0, 6));
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(title), BorderFactory.createEmptyBorder(4, 6, 6, 6)));
        section.add(fields, BorderLayout.CENTER);
        section.add(buttons, BorderLayout.SOUTH);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, section.getPreferredSize().height));
        return section;
    }

    private JButton button(String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(e -> run(action));
        return button;
    }

    private void run(Runnable action) {
        try {
            action.run();
            refresh();
        } catch (IllegalArgumentException | IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rent() {
        String vehicleId = (String) rentVehicle.getSelectedItem();
        if (vehicleId == null) {
            throw new IllegalArgumentException("No available vehicle selected");
        }
        Set<Extra> extras = EnumSet.noneOf(Extra.class);
        if (insurance.isSelected()) {
            extras.add(Extra.INSURANCE);
        }
        if (childSeat.isSelected()) {
            extras.add(Extra.CHILD_SEAT);
        }
        if (gps.isSelected()) {
            extras.add(Extra.GPS);
        }
        RentalAgreement agreement = facade.rent(rentCustomer.getText(), vehicleId,
                Duration.ofHours((Integer) rentHours.getValue()), (PricingPlan) rentPlan.getSelectedItem(), extras);
        write("Created " + agreement);
    }

    private void returnVehicle() {
        String rentalId = (String) returnRental.getSelectedItem();
        if (rentalId == null) {
            throw new IllegalArgumentException("No active rental selected");
        }
        BigDecimal total = facade.returnVehicle(rentalId, Duration.ofHours((Integer) returnHours.getValue()));
        write("Returned " + rentalId + ", total to pay: " + total + " KZT");
    }

    private void subscribe() {
        VehicleType type = (VehicleType) notifyType.getSelectedItem();
        Customer customer = facade.notifyWhenAvailable(notifyName.getText(), type);
        shownMessages.putIfAbsent(customer, customer.getInbox().size());
        write(customer.getName() + " will be notified when a " + type + " is available");
    }

    private void addVehicle() {
        Vehicle vehicle = facade.addVehicle((VehicleType) addType.getSelectedItem(), addModel.getText());
        addModel.setText("");
        write("Added " + vehicle);
    }

    private void refresh() {
        vehicles.setRowCount(0);
        rentVehicle.removeAllItems();
        for (Vehicle vehicle : facade.getVehicles()) {
            vehicles.addRow(new Object[]{vehicle.getId(), vehicle.getType(), vehicle.getModel(),
                    vehicle.getHourlyRate(), vehicle.getDailyRate(), vehicle.isAvailable() ? "available" : "rented"});
            if (vehicle.isAvailable()) {
                rentVehicle.addItem(vehicle.getId());
            }
        }

        rentals.clear();
        returnRental.removeAllItems();
        for (RentalAgreement agreement : facade.getActiveRentals()) {
            rentals.addElement(agreement.toString());
            returnRental.addItem(agreement.getId());
        }

        FleetDashboard dashboard = facade.getDashboard();
        StringJoiner counts = new StringJoiner("   ", "Available now:   ", "");
        for (VehicleType type : VehicleType.values()) {
            counts.add(type + " " + dashboard.getAvailableCount(type));
        }
        summary.setText(counts.toString());

        for (Map.Entry<Customer, Integer> entry : shownMessages.entrySet()) {
            List<String> inbox = entry.getKey().getInbox();
            for (int i = entry.getValue(); i < inbox.size(); i++) {
                write("[notify " + entry.getKey().getName() + "] " + inbox.get(i));
            }
            entry.setValue(inbox.size());
        }
    }

    private void write(String line) {
        log.append(line + "\n");
        log.setCaretPosition(log.getDocument().getLength());
    }
}
