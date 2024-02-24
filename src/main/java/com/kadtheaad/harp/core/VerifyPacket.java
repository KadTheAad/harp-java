package com.kadtheaad.harp.core;

import com.kadtheaad.harp.cryptography.AsymmetricEncryption;
import com.kadtheaad.harp.cryptography.Hasher;
import com.kadtheaad.harp.cryptography.ProofOfWork;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class VerifyPacket implements Cloneable {
    private final byte[] to;
    private final byte[] from;
    private final String password;
    private final long proofOfWork;
    private final byte[] signature;
    private final long timestamp;
    private final byte[] starter;

    public VerifyPacket(VerifyPacket packet) {
        this.to = packet.to;
        this.from = packet.from;
        this.password = packet.password;
        this.proofOfWork = packet.proofOfWork;
        this.signature = packet.signature;
        this.timestamp = packet.timestamp;
        this.starter = packet.starter;
    }


    public VerifyPacket(byte[] to, byte[] from, String password, AsymmetricEncryption key) {
        this.to = to;
        this.from = from;
        this.password = password;

        this.starter = getStarter();

        try {
            this.signature = key.sign(starter);
        } catch (NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException |
                 InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        this.timestamp = System.currentTimeMillis();

        this.proofOfWork = ProofOfWork.get(starter, signature, timestamp);
    }

    public VerifyPacket(byte[] received) throws IOException {
        this.to = null;
        this.from = null;
        this.password = null;

        ByteArrayInputStream bais = new ByteArrayInputStream(received);
        try (DataInputStream dis = new DataInputStream(bais)) {
            int starterLength = dis.readInt();
            this.starter = new byte[starterLength];
            dis.readFully(this.starter);

            int signatureLength = dis.readInt();
            this.signature = new byte[signatureLength];
            dis.readFully(this.signature);

            this.proofOfWork = dis.readLong();
            this.timestamp = dis.readLong();
        }
    }

    public boolean validateProofOfWork() {
        return ProofOfWork.verify(starter, signature, timestamp, proofOfWork);
    }

    public boolean verify(byte[] starter) {
        if (!Arrays.equals(this.starter, starter)) return false;
        if (!validateProofOfWork()) return false;
        return System.currentTimeMillis() - timestamp <= 16000 && System.currentTimeMillis() - timestamp >= 100;
    }

    public byte[] get() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DataOutputStream dos = new DataOutputStream(baos)) {
            dos.writeInt(starter.length);
            dos.write(starter);
            dos.writeInt(signature.length);
            dos.write(signature);
            dos.writeLong(proofOfWork);
            dos.writeLong(timestamp);
        }
        return baos.toByteArray();
    }

    public byte[] getStarter() {
        if (starter != null)
            return starter;
        else {
            return VerifyPacket.getStarter(to, from, password);
        }
    }

    public static byte[] getStarter(byte[] to, byte[] from, String password) {
        Hasher hasher = new Hasher(false);

        byte[] toHased = hasher.hash(to);
        byte[] fromHased = hasher.hash(from);
        byte[] toFromHased = hasher.hash(HARP.concatenateByteArrays(toHased, fromHased));
        byte[] passwordHased = hasher.hash(password.getBytes());

        int buffer = passwordHased.length;

        byte[] starter = new byte[buffer];

        int i = 0;
        for (byte b : toHased)
            starter[i] = (byte) (b ^ fromHased[i++]);

        i = 0;
        for (byte b : starter)
            starter[i] = (byte) (b ^ passwordHased[i++]);

        i = 0;
        for (byte b : starter)
            starter[i] = (byte) (b ^ toFromHased[i++]);

        return hasher.hash(starter);
    }

    public byte[] getTo() {
        return to;
    }

    public byte[] getFrom() {
        return from;
    }

    public long getProofOfWork() {
        return proofOfWork;
    }

    public byte[] getSignature() {
        return signature;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public VerifyPacket clone() {
        return new VerifyPacket(this);
    }
}
