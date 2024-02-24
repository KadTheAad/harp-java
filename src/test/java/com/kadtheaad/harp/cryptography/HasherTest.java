package com.kadtheaad.harp.cryptography;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class HasherTest {

    @Test
    public void hashWithValidInput() {
        Hasher hasher = new Hasher(false);
        byte[] data = "Hello World!".getBytes();

        byte[] hashedData = hasher.hash(data);

        assertNotNull(hashedData);
    }

    @Test
    public void hashBinaryWithValidInput() {
        Hasher hasher = new Hasher(false);
        String data = "Hello World!";

        String hashedData = hasher.hashBinary(data);

        assertNotNull(hashedData);
    }

    @Test
    public void hashWithNullInputShouldReturnNull() {
        Hasher hasher = new Hasher(false);
        byte[] data = null;

        byte[] hashedData = hasher.hash(data);

        assertNull(hashedData);
    }

    @Test
    public void hashBinaryWithNullInputShouldReturnNull() {
        Hasher hasher = new Hasher(false);
        String data = null;

        String hashedData = hasher.hashBinary(data);

        assertNull(hashedData);
    }

    @Test
    public void hashBinaryWithEmptyInputShouldReturnValidHash() {
        Hasher hasher = new Hasher(false);
        String data = "";

        String hashedData = hasher.hashBinary(data);

        assertNotNull(hashedData);
    }

    @Test
    public void hashWithEmptyInputShouldReturnValidHash() {
        Hasher hasher = new Hasher(false);
        byte[] data = "".getBytes();

        byte[] hashedData = hasher.hash(data);

        assertNotNull(hashedData);
    }

    @Test
    public void hashBinaryWithNonAsciiInputShouldReturnValidHash() {
        Hasher hasher = new Hasher(false);
        String data = "こんにちは世界";

        String hashedData = hasher.hashBinary(data);

        assertNotNull(hashedData);
    }

    @Test
    public void hashWithNonAsciiInputShouldReturnValidHash() {
        Hasher hasher = new Hasher(false);
        byte[] data = "こんにちは世界".getBytes(StandardCharsets.UTF_8);

        byte[] hashedData = hasher.hash(data);

        assertNotNull(hashedData);
    }
}