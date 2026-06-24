/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.net.packet;

import com.rs2.net.packet.PacketReader;

public final class IncomingPacket {
    private int opcode;
    private int length;
    private PacketReader reader;

    public IncomingPacket(int n, int n2, PacketReader packetReader) {
        this.opcode = n;
        this.length = n2;
        this.reader = packetReader;
    }

    public final int getOpcode() {
        return this.opcode;
    }

    public final int getLength() {
        return this.length;
    }

    public final PacketReader getReader() {
        return this.reader;
    }
}

