package com.kadtheaad.harp.core;

import org.javatuples.Pair;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Networker {
    private Networker() {}

    public static List<InetAddress> getBroadcastAddresses() {
        try {
            List<InetAddress> broadcastAddresses = new ArrayList<>();
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();

                if (networkInterface.isUp() && !networkInterface.isLoopback()) {
                    for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                        InetAddress broadcast = interfaceAddress.getBroadcast();

                        if (broadcast != null) {
                            broadcastAddresses.add(broadcast);
                        }
                    }
                }
            }
            return broadcastAddresses;
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<InetAddress> getBroadcastAddresses(String networkInterface) {
        List<InetAddress> broadcastAddresses = new ArrayList<>();
        try {
            NetworkInterface ni = NetworkInterface.getByName(networkInterface);
            for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {
                InetAddress broadcast = interfaceAddress.getBroadcast();

                if (broadcast != null) {
                    broadcastAddresses.add(broadcast);
                }
            }
        } catch (Exception ignored) {
            return getBroadcastAddresses();
        }
        return broadcastAddresses;
    }

    public static List<NetworkInterface> getInterfaces() throws SocketException {
        List<NetworkInterface> interfaces = new ArrayList<>();
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();

            if (networkInterface.isUp() && !networkInterface.isLoopback()) {
                boolean hasBroadcast = false;
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    if (interfaceAddress.getBroadcast() != null) {
                        hasBroadcast = true;
                        break;
                    }
                }
                if (hasBroadcast)
                    interfaces.add(networkInterface);
            }
        }
        return interfaces;
    }

    // Send udp broadcast message function
    public static boolean sendBroadcast(byte[] sendData, int port, InetAddress broadcastAddress) {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);

            DatagramPacket packet = new DatagramPacket(sendData, sendData.length, broadcastAddress, port);
            socket.setSoTimeout(5000);
            socket.send(packet);

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // Receive udp broadcast message function
    public static Pair<byte[], InetAddress> receiveBroadcast(int port, int bufferSize, int timeout) throws SocketTimeoutException {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            byte[] receiveData = new byte[bufferSize];
            DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);

            socket.setSoTimeout(timeout);
            socket.receive(packet);
            socket.close();

            return new Pair<>(packet.getData(), packet.getAddress());
        } catch (SocketTimeoutException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] receiveBroadcast(int port, int bufferSize) {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            byte[] receiveData = new byte[bufferSize];
            DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);

            socket.receive(packet);

            return packet.getData();
        } catch (IOException e) {
            return null;
        }
    }
}
