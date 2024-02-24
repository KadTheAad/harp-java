package com.kadtheaad.harp.core;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.*;

class HARPTest {
    @Test
    void harpShouldRunInLessThan10SecondsAndShouldBothTheSenderAndReceiverShouldResolveTheSameAddress() {
        final String[] senderAddress = new String[1];
        final String[] receiverAddress = new String[1];
        boolean[] senderDone = {false};
        boolean[] receiverDone = {false};
        final HARP[] sender = {new HARP(true, "address".getBytes(), "address".getBytes(), "1234", new HarpCallback() {
            @Override
            public void phase2(InetAddress address, VerifyPacket packet) {

            }

            @Override
            public void run(InetAddress address) {
                senderDone[0] = true;
                senderAddress[0] = address.getHostAddress();
            }
        })};
        sender[0].run();

        final HARP[] receiver = {new HARP(false, "address".getBytes(), "address".getBytes(), "1234", new HarpCallback() {
            @Override
            public void phase2(InetAddress address, VerifyPacket packet) {

            }

            @Override
            public void run(InetAddress address) {
                receiverDone[0] = true;
                receiverAddress[0] = address.getHostAddress();
            }
        })};
        receiver[0].run();

        int i = 0;
        while (!senderDone[0] || !receiverDone[0]) {
            if (i > 10) {
                fail();
            } else {
                i++;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {}
        }
        if (senderAddress[0] == null || receiverAddress[0] == null) {
            fail();
        }
        assertEquals(senderAddress[0], receiverAddress[0]);
    }
}