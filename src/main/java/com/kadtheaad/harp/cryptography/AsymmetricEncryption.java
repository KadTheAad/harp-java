package com.kadtheaad.harp.cryptography;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.util.Arrays;

public class AsymmetricEncryption {
    private final PublicKey publicKey;
    private final PrivateKey privateKey;
    private PublicKey otherPublicKey;

    public AsymmetricEncryption() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);

        KeyPair pair = keyGen.generateKeyPair();
        privateKey = pair.getPrivate();
        publicKey = pair.getPublic();
        otherPublicKey = null;
    }

    public static byte[] encrypt(byte[] data, PublicKey key) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public byte[] decrypt(byte[] data) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    public byte[] sign(byte[] data) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    public static boolean verify(byte[] data, byte[] signature, PublicKey key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return Arrays.equals(cipher.doFinal(signature), data);
        } catch (Exception e) {
            return false;
        }
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PublicKey getOtherPublicKey() {
        return otherPublicKey;
    }

    public void setOtherPublicKey(PublicKey otherPublicKey) {
        if (this.otherPublicKey == null)
            this.otherPublicKey = otherPublicKey;
    }
}
