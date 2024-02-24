package com.kadtheaad.harp.cryptography.encryptedtcp;

import com.kadtheaad.harp.cryptography.SymmetricEncryption;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SymmetricallyEncryptedTCP implements EncryptedTCP {
    private final String password;
    private final DataInputStream dis;
    private final DataOutputStream dos;

    public SymmetricallyEncryptedTCP(String password, Socket socket) {
        this.password = password;

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
            byte[] encrypted = SymmetricEncryption.encrypt(data, password);
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
            return SymmetricEncryption.decrypt(data, password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
