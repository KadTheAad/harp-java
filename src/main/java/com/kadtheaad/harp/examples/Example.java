package com.kadtheaad.harp.examples;

import com.kadtheaad.harp.core.HARP;
import com.kadtheaad.harp.core.Networker;

import java.net.InetAddress;
import java.util.List;

public class Example {
    public static void main(String[] args) {
        List<InetAddress> broadcastAddress = Networker.getBroadcastAddresses();
        if (broadcastAddress == null) {
            System.out.println("No broadcast address found");
            return;
        }

        HARP sender = new HARP(true, "address".getBytes(), "address".getBytes(), "1234", broadcastAddress.get(0));
        sender.run();

        HARP receiver = new HARP(false, "address".getBytes(), "address".getBytes(), "1234", broadcastAddress.get(0));
        receiver.run();
    }
}
