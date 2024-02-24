package com.kadtheaad.harp.cryptography.encryptedtcp;

public interface EncryptedTCP {
    /**
     This method sends data to the server with encryption
     @param data the data to be sent
     @return true if the data was sent successfully, false otherwise
     */
    boolean send(byte[] data);
    /**
     This method receives data from the server with encryption
     @return the data received
     */
    byte[] receive();
}
