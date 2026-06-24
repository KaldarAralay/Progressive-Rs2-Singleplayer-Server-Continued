/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.net.packet.handler;

import com.rs2.ServerSettings;
import com.rs2.model.player.Player;
import com.rs2.net.packet.IncomingPacket;
import com.rs2.net.packet.PacketHandler;

public final class AppearancePacketHandler
implements PacketHandler {
    private static int[] DEFAULT_MALE_BODY_PARTS = new int[]{86, 87, 89, 84, 88, 90, 85};

    @Override
    public final void handle(Player player, IncomingPacket incomingPacket) {
        player.setGender(incomingPacket.getReader().readSignedByte());
        player.getAppearanceParts()[3] = incomingPacket.getReader().readSignedByte();
        player.getAppearanceParts()[6] = incomingPacket.getReader().readSignedByte();
        player.getAppearanceParts()[0] = incomingPacket.getReader().readSignedByte();
        player.getAppearanceParts()[1] = incomingPacket.getReader().readSignedByte();
        player.getAppearanceParts()[4] = incomingPacket.getReader().readSignedByte();
        player.getAppearanceParts()[2] = incomingPacket.getReader().readSignedByte();
        player.getAppearanceParts()[5] = incomingPacket.getReader().readSignedByte();
        player.getAppearanceColors()[0] = incomingPacket.getReader().readSignedByte();
        player.getAppearanceColors()[1] = incomingPacket.getReader().readSignedByte();
        player.getAppearanceColors()[2] = incomingPacket.getReader().readSignedByte();
        player.getAppearanceColors()[3] = incomingPacket.getReader().readSignedByte();
        player.getAppearanceColors()[4] = incomingPacket.getReader().readSignedByte();
        AppearancePacketHandler.validateAppearance(player);
    }

    public static void validateAppearance(Player player) {
        int n = player.getGender();
        if (n < 0 || n > 1) {
            player.resetAppearance();
            return;
        }
        int[] nArray = ServerSettings.APPEARANCE_COLOR_RANGES[player.getGender()][0];
        int[] nArray2 = ServerSettings.APPEARANCE_COLOR_RANGES[player.getGender()][1];
        int n2 = 0;
        while (n2 < player.getAppearanceColors().length) {
            if (player.getAppearanceColors()[n2] < nArray[n2] || player.getAppearanceColors()[n2] > nArray2[n2]) {
                player.getAppearanceColors()[n2] = nArray[n2];
            }
            ++n2;
        }
        nArray = ServerSettings.APPEARANCE_BODY_PART_RANGES[player.getGender()][0];
        nArray2 = ServerSettings.APPEARANCE_BODY_PART_RANGES[player.getGender()][1];
        n2 = 0;
        while (n2 < player.getAppearanceParts().length) {
            if (n == 1 && n2 == 6) {
                player.getAppearanceParts()[n2] = -1;
            } else if (!(n == 0 && player.getAppearanceParts()[n2] == DEFAULT_MALE_BODY_PARTS[n2] && player.ee == 1 || player.getAppearanceParts()[n2] >= nArray[n2] && player.getAppearanceParts()[n2] <= nArray2[n2])) {
                player.getAppearanceParts()[n2] = nArray[n2];
            }
            ++n2;
        }
    }
}

