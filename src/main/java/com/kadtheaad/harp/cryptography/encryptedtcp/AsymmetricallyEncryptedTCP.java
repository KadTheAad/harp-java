package com.kadtheaad.harp.cryptography.encryptedtcp;

import com.kadtheaad.harp.cryptography.AsymmetricEncryption;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class AsymmetricallyEncryptedTCP implements EncryptedTCP {
    private final AsymmetricEncryption asymmetricEncryption;
    private final DataInputStream dis;
    private final DataOutputStream dos;

    public AsymmetricallyEncryptedTCP(AsymmetricEncryption asymmetricEncryption, Socket socket) {
        this.asymmetricEncryption = asymmetricEncryption;

        try {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    @Override
    public boolean send(byte[] data) {
        try {
            byte[] encrypted = AsymmetricEncryption.encrypt(data, asymmetricEncryption.getOtherPublicKey());
            dos.writeInt(encrypted.length);
            dos.write(encrypted);
            dos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public byte[] receive() {
        try {
            int length = dis.readInt();
            byte[] data = new byte[length];
            dis.readFully(data);
            return asymmetricEncryption.decrypt(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
