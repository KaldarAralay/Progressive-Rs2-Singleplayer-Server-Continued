/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.net.packet;

import com.rs2.model.player.Player;
import com.rs2.net.packet.IncomingPacket;

public interface PacketHandler {
    public void handle(Player var1, IncomingPacket var2);
}

