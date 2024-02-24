package com.kadtheaad.harp.core;

import java.net.InetAddress;

public interface HarpCallback {
    /**
     Runs when the first phase of the verification process is done and phase 2 is about to start
     @param address the address of the device that is going to be verified in the next phase
     @param packet the initial packet that was sent to this device from the address
     */
    void phase2(InetAddress address, VerifyPacket packet);
    /**
     Runs when the second phase of the verification process has completed and an address is verified. Runs in a separate thread.
     @param address the verified address
     */
    void run(InetAddress address);
}
