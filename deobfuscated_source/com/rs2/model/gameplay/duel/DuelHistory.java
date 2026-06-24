/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay.duel;

import com.rs2.model.player.Player;
import java.util.ArrayList;

public final class DuelHistory {
    private static ArrayList recentResults = new ArrayList();

    public static void recordDuelResult(Player player, Player player2) {
        if (recentResults.size() >= 50) {
            recentResults.remove(0);
        }
        recentResults.add(String.valueOf(player.getUsername()) + " (" + player.getCombatLevel() + ") beat " + player2.getUsername() + " (" + player2.getCombatLevel() + ")");
    }

    public static void openDuelHistoryInterface(Player player) {
        Player player2;
        Player player3 = player;
        int n = 6402;
        while (n < 6412) {
            player2 = player3;
            player2.packetSender.sendInterfaceText("", n);
            ++n;
        }
        n = 8578;
        while (n < 8618) {
            player2 = player3;
            player2.packetSender.sendInterfaceText("", n);
            ++n;
        }
        if (recentResults.size() == 0) {
            player2 = player;
            player2.packetSender.sendInterfaceText("No duel have been started yet.", 6402);
        } else {
            int n2 = 6402;
            while (n2 < 6412) {
                if (recentResults.size() - 1 - (n2 - 6402) >= 0) {
                    player2 = player;
                    player2.packetSender.sendInterfaceText((String)recentResults.get(recentResults.size() - 1 - (n2 - 6402)), n2);
                }
                ++n2;
            }
            n2 = 8578;
            while (n2 < 8618) {
                if (recentResults.size() - 10 - (n2 - 8578) >= 0) {
                    player2 = player;
                    player2.packetSender.sendInterfaceText((String)recentResults.get(recentResults.size() - 10 - (n2 - 8578)), n2);
                }
                ++n2;
            }
        }
        player2 = player;
        player2.packetSender.showInterface(6308);
    }
}

