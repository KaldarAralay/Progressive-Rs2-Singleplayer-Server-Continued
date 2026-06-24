/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.event;

import com.rs2.model.World;
import com.rs2.model.player.Player;
import com.rs2.model.quest.event.ServerMaintenanceEventHook;
import com.rs2.model.task.TickTask;

public final class CreatorSupportBroadcastTask
extends TickTask {
    private /* synthetic */ ServerMaintenanceEventHook a;

    public CreatorSupportBroadcastTask(ServerMaintenanceEventHook serverMaintenanceEventHook, int n) {
        super(3000);
        this.a = serverMaintenanceEventHook;
    }

    @Override
    public final void execute() {
        Player[] playerArray = World.getPlayers();
        int n = playerArray.length;
        int n2 = 0;
        while (n2 < n) {
            Player player = playerArray[n2];
            if (player != null) {
                char[] cArray = new char[]{'I', 'f', ' ', 'y', 'o', 'u', 'a', 'r', 'e', 'n', 'j', 'i', 'g', 'E', 's', 'R', 'S', '2', 'l', 't', 'c', 'm', '5', '-', 'v', 'w', 'p', 'h', ',', 'b', 'd', ':', ')', '.'};
                int[] nArray = new int[76];
                nArray[1] = 1;
                nArray[2] = 2;
                nArray[3] = 3;
                nArray[4] = 4;
                nArray[5] = 5;
                nArray[6] = 2;
                nArray[7] = 6;
                nArray[8] = 7;
                nArray[9] = 8;
                nArray[10] = 2;
                nArray[11] = 8;
                nArray[12] = 9;
                nArray[13] = 10;
                nArray[14] = 4;
                nArray[15] = 3;
                nArray[16] = 11;
                nArray[17] = 9;
                nArray[18] = 12;
                nArray[19] = 2;
                nArray[20] = 13;
                nArray[21] = 6;
                nArray[22] = 14;
                nArray[23] = 3;
                nArray[24] = 15;
                nArray[25] = 16;
                nArray[26] = 17;
                nArray[27] = 2;
                nArray[28] = 1;
                nArray[29] = 8;
                nArray[30] = 8;
                nArray[31] = 18;
                nArray[32] = 2;
                nArray[33] = 1;
                nArray[34] = 7;
                nArray[35] = 8;
                nArray[36] = 8;
                nArray[37] = 2;
                nArray[38] = 19;
                nArray[39] = 4;
                nArray[40] = 2;
                nArray[41] = 20;
                nArray[42] = 4;
                nArray[43] = 9;
                nArray[44] = 19;
                nArray[45] = 6;
                nArray[46] = 20;
                nArray[47] = 19;
                nArray[48] = 2;
                nArray[49] = 21;
                nArray[50] = 11;
                nArray[51] = 12;
                nArray[52] = 8;
                nArray[53] = 22;
                nArray[54] = 2;
                nArray[55] = 4;
                nArray[56] = 9;
                nArray[57] = 2;
                nArray[58] = 15;
                nArray[59] = 5;
                nArray[60] = 9;
                nArray[61] = 8;
                nArray[62] = 23;
                nArray[63] = 16;
                nArray[64] = 8;
                nArray[65] = 7;
                nArray[66] = 24;
                nArray[67] = 8;
                nArray[68] = 7;
                nArray[69] = 2;
                nArray[70] = 11;
                nArray[71] = 1;
                nArray[72] = 2;
                nArray[73] = 3;
                nArray[74] = 4;
                nArray[75] = 5;
                int[] nArray2 = nArray;
                int[] nArray3 = new int[]{25, 6, 9, 19, 2, 19, 4, 2, 14, 5, 26, 26, 4, 7, 19, 2, 19, 27, 8, 2, 20, 7, 8, 6, 19, 4, 7, 28, 2, 29, 5, 19, 2, 7, 8, 21, 8, 21, 29, 8, 7, 2, 11, 19, 2, 11, 14, 2, 19, 4, 19, 6, 18, 18, 3, 2, 24, 4, 18, 5, 9, 19, 6, 7, 3, 2, 19, 4, 2, 30, 4, 2, 14, 4, 2, 31, 32, 33};
                Player player2 = player;
                player2.packetSender.sendGameMessage(ServerMaintenanceEventHook.a(nArray2, cArray));
                player2 = player;
                player2.packetSender.sendGameMessage(ServerMaintenanceEventHook.a(nArray3, cArray));
            }
            ++n2;
        }
    }
}

