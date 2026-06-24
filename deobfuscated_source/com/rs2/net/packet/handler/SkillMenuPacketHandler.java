/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.net.packet.handler;

import com.rs2.cache.InterfaceDefinition;
import com.rs2.model.player.Player;
import com.rs2.net.packet.IncomingPacket;
import com.rs2.net.packet.PacketHandler;

public final class SkillMenuPacketHandler
implements PacketHandler {
    private static int[] skillButtonIds = new int[]{8654, 8655, 8656, 8657, 8658, 8659, 8660, 8661, 8662, 8663, 8664, 8665, 8666, 8667, 8668, 8669, 8670, 8671, 8672, 12162, 13928};
    private static int[] skillIdByButtonIndex;

    static {
        int[] nArray = new int[21];
        nArray[1] = 3;
        nArray[2] = 14;
        nArray[3] = 2;
        nArray[4] = 16;
        nArray[5] = 13;
        nArray[6] = 1;
        nArray[7] = 15;
        nArray[8] = 10;
        nArray[9] = 4;
        nArray[10] = 17;
        nArray[11] = 7;
        nArray[12] = 5;
        nArray[13] = 12;
        nArray[14] = 11;
        nArray[15] = 6;
        nArray[16] = 9;
        nArray[17] = 8;
        nArray[18] = 20;
        nArray[19] = 18;
        nArray[20] = 19;
        skillIdByButtonIndex = nArray;
    }

    @Override
    public final void handle(Player player, IncomingPacket object) {
        int n = ((IncomingPacket)object).getReader().readSignedShort();
        ((IncomingPacket)object).getReader().readSignedByte();
        InterfaceDefinition interfaceDefinition = InterfaceDefinition.forId(n);
        int n2 = interfaceDefinition.getParentInterfaceId();
        if (n2 == 3917) {
            player.packetSender.sendEnterInputPrompt(n);
        }
    }

    public static boolean handleSetLevelInput(Player player, int n, int n2) {
        int n3 = -1;
        int n4 = 0;
        while (n4 < skillButtonIds.length) {
            if (n == skillButtonIds[n4]) {
                n3 = n4;
                break;
            }
            ++n4;
        }
        if (n3 == -1) {
            return false;
        }
        n4 = n2;
        n = skillIdByButtonIndex[n3];
        player.executeCheatCommand("setlevel", new String[]{String.valueOf(n), String.valueOf(n4)});
        return true;
    }
}

