package com.kadtheaad.harp;

import com.kadtheaad.harp.core.HARP;
import com.kadtheaad.harp.core.HarpCallback;
import com.kadtheaad.harp.core.Networker;
import com.kadtheaad.harp.core.VerifyPacket;

import javax.swing.*;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class GUI {
    static boolean isRunning = false;

    static void updateModal(JComboBox<String> interfaceComboBox, JComboBox<String> broadcastAddress) {
        java.util.List<String> broadcastAddresses = new ArrayList<>();
        Networker.getBroadcastAddresses((String) interfaceComboBox.getSelectedItem()).forEach((networkInterface) -> broadcastAddresses.add(networkInterface.getHostName()));
        broadcastAddress.setModel(new DefaultComboBoxModel<>(broadcastAddresses.toArray(new String[0])));
    }

    public static void main() {
        // Create JFrame
        JFrame frame = new JFrame("HARP GUI");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create JPanel
        JPanel panel = new JPanel();
        frame.add(panel);

        // Set the layout manager to null for absolute positioning
        panel.setLayout(null);

        // Create components
        JLabel senderLabel = new JLabel("Run as a sender:");
        JCheckBox senderCheckBox = new JCheckBox();
        JLabel interfaceLabel = new JLabel("Network Interface:");
        JComboBox<String> interfaceComboBox = new JComboBox<>();
        JLabel broadcastAddressLabel = new JLabel("Broadcast Addresses:");
        JComboBox<String> broadcastAddress = new JComboBox<>();
        JLabel toLabel = new JLabel("To Address:");
        JTextField toTextField = new JTextField();
        JLabel fromLabel = new JLabel("From Address:");
        JTextField fromTextField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        JButton submitButton = new JButton("Submit");

        // Create output text area
        JTextArea outputTextArea = new JTextArea();
        JScrollPane outputScrollPane = new JScrollPane(outputTextArea);

        // Set default values for the interface combo box
        try {
            java.util.List<String> networkInterfaces = new ArrayList<>();
            Networker.getInterfaces().forEach(networkInterface -> networkInterfaces.add(networkInterface.getName()));
            interfaceComboBox.setModel(new DefaultComboBoxModel<>(networkInterfaces.toArray(new String[0])));
        } catch (SocketException ignored) {
        }

        updateModal(interfaceComboBox, broadcastAddress);

        // Set the bounds for components
        senderLabel.setBounds(10, 20, 150, 25);
        senderCheckBox.setBounds(160, 20, 25, 25);
        interfaceLabel.setBounds(10, 50, 150, 25);
        interfaceComboBox.setBounds(160, 50, 150, 25);
        broadcastAddressLabel.setBounds(10, 80, 150, 25);
        broadcastAddress.setBounds(160, 80, 150, 25);
        toLabel.setBounds(10, 110, 150, 25);
        toTextField.setBounds(160, 110, 150, 25);
        fromLabel.setBounds(10, 140, 150, 25);
        fromTextField.setBounds(160, 140, 150, 25);
        passwordLabel.setBounds(10, 170, 150, 25);
        passwordField.setBounds(160, 170, 150, 25);
        submitButton.setBounds(10, 200, 150, 25);
        outputScrollPane.setBounds(10, 230, 760, 330);


        // Add components to the panel
        panel.add(senderLabel);
        panel.add(senderCheckBox);
        panel.add(interfaceLabel);
        panel.add(interfaceComboBox);
        panel.add(broadcastAddressLabel);
        panel.add(broadcastAddress);
        panel.add(toLabel);
        panel.add(toTextField);
        panel.add(fromLabel);
        panel.add(fromTextField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(submitButton);
        panel.add(outputScrollPane);

        // Add action listener to the interface combo box
        interfaceComboBox.addActionListener(e -> updateModal(interfaceComboBox, broadcastAddress));

        // Add action listener to the button
        submitButton.addActionListener(e -> {
            if (isRunning) {
                outputTextArea.append("Already running\n");
                return;
            }
            // Retrieve values from components
            boolean isSender = senderCheckBox.isSelected();
            String selectedInterface = (String) interfaceComboBox.getSelectedItem();
            String toAddress = toTextField.getText();
            String fromAddress = fromTextField.getText();
            char[] password = passwordField.getPassword();

            if (password.length == 0) {
                outputTextArea.append("Password cannot be empty\n");
                return;
            }

            if (fromAddress.isBlank() || fromAddress.isEmpty()) {
                outputTextArea.append("From cannot be empty\n");
                return;
            }

            if (toAddress.isBlank() || toAddress.isEmpty()) {
                outputTextArea.append("To cannot be empty\n");
                return;
            }

            new HARP(isSender, toAddress.getBytes(), fromAddress.getBytes(), new String(password), Networker.getBroadcastAddresses(selectedInterface).get(0), new HarpCallback() {
                @Override
                public void phase2(InetAddress address, VerifyPacket packet) {
                    outputTextArea.append("Running phase 2 with: " + address.getHostAddress() + "\n");
                }

                @Override
                public void run(InetAddress address) {
                    outputTextArea.append("Finished: " + address.getHostAddress() + "\n");
                    isRunning = false;
                }
            }).run();
            isRunning = true;
        });

        // Set the frame to be visible
        frame.setVisible(true);
    }
}
