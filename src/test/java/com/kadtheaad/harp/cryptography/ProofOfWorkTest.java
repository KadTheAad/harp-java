package com.kadtheaad.harp.cryptography;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProofOfWorkTest {

    @Test
    public void verifyWithValidInputAndNonceFromGet() {
        byte[] starter = "Hello".getBytes();
        byte[] signature = "World".getBytes();
        long timestamp = System.currentTimeMillis();
        long nonce = ProofOfWork.get(starter, signature, timestamp);

        boolean result = ProofOfWork.verify(starter, signature, timestamp, nonce);

        assertTrue(result);
    }

    @Test
    public void verifyWithInvalidInput() {
        byte[] starter = "Hello".getBytes();
        byte[] signature = "World".getBytes();
        long timestamp = System.currentTimeMillis();
        long nonce = -1;

        boolean result = ProofOfWork.verify(starter, signature, timestamp, nonce);

        assertFalse(result);
    }

    @Test
    public void getWithValidInput() {
        byte[] starter = "Hello".getBytes();
        byte[] signature = "World".getBytes();
        long timestamp = System.currentTimeMillis();

        long nonce = ProofOfWork.get(starter, signature, timestamp);

        assertTrue(nonce >= 0);
    }

    @Test
    public void bytesToHexWithValidInput() {
        byte[] data = "Hello World!".getBytes();

        String hex = ProofOfWork.bytesToHex(data);

        assertNotNull(hex);
    }

    @Test
    public void bytesToHexWithValidInputIsAccurate() {
        byte[] data = "Hello World!".getBytes();

        String hex = ProofOfWork.bytesToHex(data);

        assertEquals(hex, "48656c6c6f20576f726c6421");
    }

    @Test
    public void reverseBytesWithValidInput() {
        byte[] data = "Hello World!".getBytes();

        byte[] reversed = ProofOfWork.reverseBytes(data);

        assertNotNull(reversed);
    }

    @Test
    public void getMessageWithValidInput() {
        byte[] starter = "Hello".getBytes();
        byte[] signature = "World".getBytes();
        long timestamp = System.currentTimeMillis();

        String message = ProofOfWork.getMessage(starter, signature, timestamp);

        assertNotNull(message);
    }
}