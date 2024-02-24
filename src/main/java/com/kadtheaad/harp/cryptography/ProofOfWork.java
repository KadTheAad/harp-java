package com.kadtheaad.harp.cryptography;

import java.nio.charset.StandardCharsets;

/**
 * This class provides methods for generating a proof of work.
 * It is not meant to be instantiated.
 */
public class ProofOfWork {
    private ProofOfWork() {}
    private static final boolean VERBOSE = false;
    private static final String DIFFICULTY = "00000";
    public static final long version = 2;

    public static final long bits = 419520339;
    /**
     * Verifies the proof of work for the given starter, signature, timestamp, and nonce.
     *
     * @param starter   The starter bytes.
     * @param signature The signature bytes.
     * @param timestamp The timestamp.
     * @param nonce     The nonce.
     * @return True if the proof of work is valid, false otherwise.
     */
    public static boolean verify(byte[] starter, byte[] signature, long timestamp, long nonce) {
        return verify(getMessage(starter, signature, timestamp), nonce);
    }

    private static boolean verify(String message, long nonce) {
        Hasher hasher = new Hasher(false);
        byte[] hash = hasher.hash(hasher.hash(message.concat(Long.toString(nonce)).getBytes(StandardCharsets.UTF_8)));
        String hashTest = bytesToHex(reverseBytes(hash));

        if (VERBOSE)
            System.out.println(hashTest);

        return hashTest.startsWith(DIFFICULTY);
    }

    /**
     * Generates a proof of work for the given starter, signature, and timestamp.
     *
     * @param starter   The starter bytes.
     * @param signature The signature bytes.
     * @param timestamp The timestamp.
     * @return The nonce that makes the proof of work valid.
     */
    public static long get(byte[] starter, byte[] signature, long timestamp) {
        String message = getMessage(starter, signature, timestamp);
        long nonce = 0;

        while (!verify(message, nonce)) {
            nonce++;
        }
        return nonce;
    }

    /**
     * Converts the given bytes to a hexadecimal string.
     *
     * @param hash The bytes to be converted.
     * @return The hexadecimal string.
     */
    public static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Reverses the order of the given bytes.
     *
     * @param bytes The bytes to be reversed.
     * @return The reversed bytes.
     */
    public static byte[] reverseBytes(byte[] bytes) {
        byte[] buffer = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++)
            buffer[i] = bytes[bytes.length - 1 - i];
        return buffer;
    }

    /**
     * Constructs a message from the given starter, signature, and timestamp.
     *
     * @param starter   The starter bytes.
     * @param signature The signature bytes.
     * @param timestamp The timestamp.
     * @return The constructed message.
     */
    public static String getMessage(byte[] starter, byte[] signature, long timestamp) {
        return ProofOfWork.version + new String(reverseBytes(starter)) + new String(reverseBytes(signature)) + timestamp + ProofOfWork.bits;
    }
}