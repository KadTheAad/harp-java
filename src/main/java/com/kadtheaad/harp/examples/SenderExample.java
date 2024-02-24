package com.kadtheaad.harp.examples;

import com.kadtheaad.harp.core.HARP;
import com.kadtheaad.harp.core.Networker;

import java.net.InetAddress;
import java.util.List;

public class SenderExample {
    static HARP sender;

    public static void main(String[] args) {
        List<InetAddress> broadcastAddress = Networker.getBroadcastAddresses();
        if (broadcastAddress == null) {
            System.out.println("No broadcast address found");
            return;
        }

        sender = new HARP(true, "address2".getBytes(), "address".getBytes(), "1234", broadcastAddress.get(0));
        sender.run();
    }
}