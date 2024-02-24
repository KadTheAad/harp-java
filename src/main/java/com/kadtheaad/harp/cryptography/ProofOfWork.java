package com.kadtheaad.harp.cryptography;

import java.nio.charset.StandardCharsets;

public class ProofOfWork {
    private static final boolean VERBOSE = false;
    private static final String DIFFICULTY = "00000";
    public static final long version = 2;

    public static final long bits = 419520339;
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

    public static long get(byte[] starter, byte[] signature, long timestamp) {
        String message = getMessage(starter, signature, timestamp);
        long nonce = 0;

        while (!verify(message, nonce)) {
            nonce++;
        }
        return nonce;
    }

    public static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static byte[] reverseBytes(byte[] bytes) {
        byte[] buffer = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++)
            buffer[i] = bytes[bytes.length - 1 - i];
        return buffer;
    }

    public static String getMessage(byte[] starter, byte[] signature, long timestamp) {
        return ProofOfWork.version + new String(reverseBytes(starter)) + new String(reverseBytes(signature)) + timestamp + ProofOfWork.bits;
    }
}