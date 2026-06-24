/*
 * Decompiled with CFR 0.152.
 */
package com.rs2;

import com.rs2.LanDiscoveryService;
import java.net.DatagramPacket;
import java.net.InetAddress;

public final class LanDiscoveryListener
extends Thread {
    private final /* synthetic */ DatagramPacket packet;

    public LanDiscoveryListener(String string, DatagramPacket datagramPacket) {
        super(string);
        this.packet = datagramPacket;
    }

    @Override
    public final void run() {
        try {
            while (LanDiscoveryService.running) {
                LanDiscoveryService.getSocket().receive(this.packet);
                String request = new String(this.packet.getData()).trim();
                if (!request.equals(LanDiscoveryService.requestMessage)) continue;
                byte[] responseBytes = LanDiscoveryService.responseMessage.getBytes();
                DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length, this.packet.getAddress(), this.packet.getPort());
                LanDiscoveryService.getSocket().send(responsePacket);
                byte[] reservedBytes = LanDiscoveryService.d.getBytes();
                byte[] maxBytes = LanDiscoveryService.c.getBytes();
                String hostName = InetAddress.getLocalHost().getHostName();
                if (reservedBytes.length > maxBytes.length) {
                    hostName = "invalid";
                }
                byte[] hostBytes = hostName.getBytes();
                DatagramPacket hostPacket = new DatagramPacket(hostBytes, hostBytes.length, this.packet.getAddress(), this.packet.getPort());
                LanDiscoveryService.getSocket().send(hostPacket);
            }
            return;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
    }

}

