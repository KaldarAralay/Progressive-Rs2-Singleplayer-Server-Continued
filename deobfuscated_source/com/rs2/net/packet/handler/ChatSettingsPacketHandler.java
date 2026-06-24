/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.net.packet.handler;

import com.rs2.model.player.Player;
import com.rs2.net.packet.IncomingPacket;
import com.rs2.net.packet.PacketHandler;

public final class ChatSettingsPacketHandler
implements PacketHandler {
    @Override
    public final void handle(Player player, IncomingPacket incomingPacket) {
        byte by = (byte)incomingPacket.getReader().readUnsignedByte(false);
        byte by2 = (byte)incomingPacket.getReader().readUnsignedByte(false);
        byte by3 = (byte)incomingPacket.getReader().readUnsignedByte(false);
        player.setPrivateChatMode(by2);
        player.setPublicChatMode(by);
        player.setTradeMode(by3);
        player.getSocialManager().refreshFriendStatuses(false);
    }
}

