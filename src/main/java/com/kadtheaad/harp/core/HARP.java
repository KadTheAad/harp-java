package com.kadtheaad.harp.core;

import com.kadtheaad.harp.cryptography.AsymmetricEncryption;
import com.kadtheaad.harp.cryptography.encryptedtcp.EncryptedTCP;
import com.kadtheaad.harp.cryptography.encryptedtcp.SymmetricallyEncryptedTCP;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.*;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class HARP implements Runnable {
    private static final int BUFFER_SIZE = 8192;
    private static final int PORT = 4545;
    private static final int PORT2 = 8192;

    private final String password;
    private final byte[] to;
    private final byte[] me;
    private final boolean isSender;
    private final List<Thread> threads;
    private final AtomicBoolean done;
    private final AsymmetricEncryption key;
    private final Map<InetAddress, List<Long>> addresses = new ConcurrentHashMap<>();
    private final InetAddress broadcastAddress;
    @Nullable
    private final HarpCallback callback;

    public HARP(boolean isSender, byte[] to, byte[] me, String password, InetAddress broadcastAddress, @NotNull HarpCallback callback) {
        this.isSender = isSender;
        this.to = to;
        this.me = me;
        this.password = password;
        this.broadcastAddress = broadcastAddress;
        this.callback = callback;

        done = new AtomicBoolean(false);
        threads = new ArrayList<>();
        try {
            key = new AsymmetricEncryption();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public HARP(boolean isSender, byte[] to, byte[] me, String password, @NotNull HarpCallback callback) {
        this.isSender = isSender;
        this.to = to;
        this.me = me;
        this.password = password;
        try {
            this.broadcastAddress = InetAddress.getByName("255.255.255.255");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        this.callback = callback;

        done = new AtomicBoolean(false);
        threads = new ArrayList<>();
        try {
            key = new AsymmetricEncryption();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public HARP(boolean isSender, byte[] to, byte[] me, String password) {
        this.isSender = isSender;
        this.to = to;
        this.me = me;
        this.password = password;
        try {
            this.broadcastAddress = InetAddress.getByName("255.255.255.255");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        this.callback = null;

        done = new AtomicBoolean(false);
        threads = new ArrayList<>();
        try {
            key = new AsymmetricEncryption();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public HARP(boolean isSender, byte[] to, byte[] me, String password, InetAddress broadcastAddress) {
        this.isSender = isSender;
        this.to = to;
        this.me = me;
        this.password = password;
        this.broadcastAddress = broadcastAddress;
        this.callback = null;

        done = new AtomicBoolean(false);
        threads = new ArrayList<>();
        try {
            key = new AsymmetricEncryption();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        if (isSender) {
            startThread(this::sendBroadcastPackets);
            startThread(this::listenForRepliesToBroadcast);
        } else {
            startThread(this::listenForBroadcastsAndReply);
        }
    }

    private void sendBroadcastPackets() {
        try {
            int i = 0;
            var ref = new Object() {
                byte[] packetAsBytes = null;
            };
            while (!done.get()) {
                ref.packetAsBytes = createPacket(i, ref.packetAsBytes);
                startThread(() -> Networker.sendBroadcast(concatenateByteArrays(ref.packetAsBytes, new byte[]{1}), PORT, broadcastAddress));
                i = i == 5 ? 0 : i + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] createPacket(int i, byte[] packet) throws Exception {
        if (packet == null || i == 5)
            return new VerifyPacket(to, me, password, key).get();
        else {
            Thread.sleep(1000);
            return packet;
        }
    }
    private void listenForRepliesToBroadcast() {
        while (!done.get()) {
            try (ServerSocket serverSocket = new ServerSocket(PORT2)) {
                serverSocket.setSoTimeout(5000);
                Socket accepted = serverSocket.accept();

                EncryptedTCP encryptedTCP = new SymmetricallyEncryptedTCP(password, accepted);

                startThread(() -> handleReplyToBroadcast(encryptedTCP, accepted));
            } catch (SocketTimeoutException ignored) {
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private void handleReplyToBroadcast(EncryptedTCP encryptedTCP, Socket accepted) {
        try {
            // Receive verify packet
            byte[] data = encryptedTCP.receive();
            if (data == null) return;
            VerifyPacket verifyPacket = new VerifyPacket(data);
            if (!verifyPacket.verify(VerifyPacket.getStarter(me, to, password))) return;

            // Send public key
            if (!encryptedTCP.send(key.getPublicKey().getEncoded())) return;

            // Receive public key
            byte[] publicKey = encryptedTCP.receive();
            if (publicKey == null) return;
            key.setOtherPublicKey(KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKey)));

            if (!AsymmetricEncryption.verify(verifyPacket.getStarter(), verifyPacket.getSignature(), key.getOtherPublicKey()))
                return;

            finished(accepted.getInetAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listenForBroadcastsAndReply() {
        try {
            byte[] starter = VerifyPacket.getStarter(me, to, password);
            while (!done.get()) {
                Pair<byte[], InetAddress> data;
                try {
                    data = Networker.receiveBroadcast(PORT, BUFFER_SIZE, 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }

                if (data == null) continue;

                startThread(new Runnable(){
                    byte[] data;
                    InetAddress address;
                    byte[] starter;
                    public Runnable set(byte[] data, InetAddress address, byte[] starter){
                        this.data = data;
                        this.address = address;
                        this.starter = starter;
                        return this;
                    }

                    @Override
                    public void run() {
                        replyToBroadcast(data, address, starter);
                    }
                }.set(data.getValue0(), data.getValue1(), starter));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void replyToBroadcast(byte[] data, InetAddress address, byte[] starter) {
        try {
            if (addresses.containsKey(address)) {
                if (addresses.get(address).size() > 5) return;
                if (addresses.get(address).get(addresses.size()) < System.currentTimeMillis() - 7000) {
                    addresses.get(address).add(System.currentTimeMillis());
                } else {
                    return;
                }
            }
            else {
                if (addresses.containsKey(address))
                    addresses.get(address).add(System.currentTimeMillis());
                else {
                    List<Long> list = new ArrayList<>();
                    list.add(System.currentTimeMillis());
                    addresses.put(address, list);
                }
            }
            if (data == null) return;

            while (data[data.length - 1] == 0) {
                byte[] temp = new byte[data.length - 1];
                System.arraycopy(data, 0, temp, 0, temp.length);
                data = temp;
            }

            byte[] temp = new byte[data.length - 1];
            System.arraycopy(data, 0, temp, 0, temp.length);
            data = temp;

            VerifyPacket packet = new VerifyPacket(data);

            if (!packet.verify(starter)) return;

            if (callback == null)
                System.out.println("Received valid message from " + address.getHostAddress());
            else
                callback.phase2(address, packet.clone());
            phase2(address, packet);
        } catch (Exception ignored) {}
    }

    private void phase2(InetAddress address, VerifyPacket packet) {
        if (!isSender) {
            startThread(() -> {
                try (Socket socket = new Socket()) {
                    // Send verify packet back
                    byte[] packetAsBytes = new VerifyPacket(to, me, password, key).get();
                    socket.connect(new InetSocketAddress(address, PORT2), 5000);
                    EncryptedTCP encryptedTCP = new SymmetricallyEncryptedTCP(password, socket);
                    if (!encryptedTCP.send(packetAsBytes)) return;

                    // Receive public key
                    byte[] publicKey = encryptedTCP.receive();
                    if (publicKey == null) return;
                    key.setOtherPublicKey(KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKey)));

                    // Send public key back
                    if (!AsymmetricEncryption.verify(packet.getStarter(), packet.getSignature(), key.getOtherPublicKey())) return;
                    if (!encryptedTCP.send(key.getPublicKey().getEncoded())) return;
                    finished(address);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public boolean isSender() {
        return isSender;
    }

    // concatenate arrays function
    public static byte[] concatenateByteArrays(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    private void startThread(Runnable runnable) {
//        threads.add(Thread.startVirtualThread(runnable));
        Thread thread = new Thread(runnable);
        threads.add(thread);
        thread.start();
    }

    private void finished(InetAddress address) {
        if (done.get()) return;
        done.set(true);
        if (callback == null)
            System.out.println("Finished: " + address.getHostAddress());
        else
            new Thread(() -> callback.run(address)).start();

        new Thread(() -> {
            for (Thread thread : threads) {
                thread.interrupt();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
                if (thread.isAlive()) {
                    System.out.println("Thread " + thread.getName() + " is still alive...");
                    thread.stop();
                }
            }
        }).start();
    }
}
