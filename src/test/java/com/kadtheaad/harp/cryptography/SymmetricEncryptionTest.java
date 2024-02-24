package com.kadtheaad.harp.cryptography;

import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.*;

class SymmetricEncryptionTest {
    @Test
    public void encryptDecryptWithValidInput() throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
        String password = "password";
        byte[] data = "Hello World!".getBytes();

        byte[] encryptedData = SymmetricEncryption.encrypt(data, password);
        byte[] decryptedData = SymmetricEncryption.decrypt(encryptedData, password);

        assertArrayEquals(data, decryptedData);
    }


    @Test
    public void decryptWithWrongPassword() throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
        String password = "password";
        String wrongPassword = "wrongPassword";
        byte[] data = "Hello World!".getBytes();

        byte[] encryptedData = SymmetricEncryption.encrypt(data, password);

        assertThrows(BadPaddingException.class, () -> SymmetricEncryption.decrypt(encryptedData, wrongPassword));
    }

    @Test
    public void encryptWithNullPasswordShouldThrowException() {
        String password = null;
        byte[] data = "Hello World!".getBytes();

        assertThrows(NullPointerException.class, () -> SymmetricEncryption.encrypt(data, password));
    }

    @Test
    public void decryptWithNullEncryptedDataShouldThrowException() {
        String password = "password";
        byte[] encryptedData = null;

        assertThrows(IllegalArgumentException.class, () -> SymmetricEncryption.decrypt(encryptedData, password));
    }
}