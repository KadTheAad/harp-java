package com.kadtheaad.harp.cryptography;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Hasher {
    private MessageDigest digest;
    public Hasher(boolean doubleLength) {
        try {
            if (doubleLength)
                digest = MessageDigest.getInstance("SHA-512");
            else
                digest = MessageDigest.getInstance("SHA-256");
        } catch (Exception ignored) {}
    }

    public byte[] hash(byte[] data) {
        if (data == null) {
            return null;
        }
        return digest.digest(data);
    }

    public String hashBinary(String toHash) {
        try {
            byte[] messageHash = digest.digest(toHash.getBytes(StandardCharsets.UTF_8));
            return new BigInteger(1, messageHash).toString(2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
