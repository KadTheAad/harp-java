package com.kadtheaad.harp.cryptography;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

/**
 * This class provides methods for symmetric encryption and decryption using the AES algorithm.
 * It is not meant to be instantiated.
 */
public class SymmetricEncryption {
    private SymmetricEncryption() {}
    /**
     * Encrypts the given data using the AES algorithm and the provided password.
     *
     * @param data     The data to be encrypted.
     * @param password The password to be used for encryption.
     * @return The encrypted data.
     * @throws NoSuchPaddingException    If the padding mechanism is not available.
     * @throws NoSuchAlgorithmException  If the AES algorithm is not available.
     * @throws IllegalBlockSizeException If the size of the data does not match the block size of the AES algorithm.
     * @throws BadPaddingException       If the padding mechanism fails.
     * @throws InvalidKeyException       If the generated key is invalid.
     * @throws InvalidKeySpecException   If the key specification is invalid.
     */
    public static byte[] encrypt(byte[] data, String password) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, generateKeyFromPassword(password));
        return cipher.doFinal(data);
    }

    /**
     * Decrypts the given data using the AES algorithm and the provided password.
     *
     * @param encryptedData The data to be decrypted.
     * @param password      The password to be used for decryption.
     * @return The decrypted data.
     * @throws NoSuchPaddingException    If the padding mechanism is not available.
     * @throws NoSuchAlgorithmException  If the AES algorithm is not available.
     * @throws IllegalBlockSizeException If the size of the data does not match the block size of the AES algorithm.
     * @throws BadPaddingException       If the padding mechanism fails.
     * @throws InvalidKeyException       If the generated key is invalid.
     * @throws InvalidKeySpecException   If the key specification is invalid.
     */
    public static byte[] decrypt(byte[] encryptedData, String password) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, generateKeyFromPassword(password));
        return cipher.doFinal(encryptedData);
    }
    /**
     * Generates a SecretKey from the provided password using the PBKDF2WithHmacSHA1 algorithm.
     *
     * @param password The password to be used for key generation.
     * @return The generated SecretKey.
     * @throws NoSuchAlgorithmException If the PBKDF2WithHmacSHA1 algorithm is not available.
     * @throws InvalidKeySpecException  If the key specification is invalid.
     */
    private static SecretKey generateKeyFromPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), "salt".getBytes(), 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }
}
