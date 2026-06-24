/*
 * Decompiled with CFR 0.152.
 */
package com.rs2;

import com.rs2.LanDiscoveryListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public final class LanDiscoveryService {
    public static String requestMessage = "rs2006sp req";
    public static String responseMessage = "rs2006sp resp";
    public static String c = "this is a reserved text for server name";
    public static String d = "tset";
    public static boolean e = false;
    private static DatagramSocket socket;
    static boolean running;

    static {
        running = true;
    }

    public static void startListener() {
        try {
            Object object = requestMessage.getBytes();
            object = new DatagramPacket((byte[])object, ((byte[])object).length);
            new LanDiscoveryListener("Connection Listener", (DatagramPacket)object).start();
            return;
        }
        catch (Exception exception) {
            Exception exception2 = exception;
            exception.printStackTrace();
            return;
        }
    }

    static /* synthetic */ DatagramSocket getSocket() {
        if (socket == null) {
            try {
                socket = new DatagramSocket(8002, InetAddress.getByName("0.0.0.0"));
                socket.setBroadcast(true);
            }
            catch (Exception exception) {
                exception.printStackTrace();
                throw new IllegalStateException(exception);
            }
        }
        return socket;
    }
}

