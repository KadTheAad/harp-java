package com.kadtheaad.harp.examples;

import com.kadtheaad.harp.core.HARP;
import com.kadtheaad.harp.core.Networker;

import java.net.InetAddress;
import java.util.List;

public class ReceiverExample {
    static HARP receiver;
    public static void main(String[] args) {
        List<InetAddress> broadcastAddress = Networker.getBroadcastAddresses();
        if (broadcastAddress == null) {
            System.out.println("No broadcast address found");
            return;
        }
        receiver = new HARP(false, "address".getBytes(), "address2".getBytes(), "1234", broadcastAddress.get(0));
        receiver.run();
    }
}
