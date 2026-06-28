/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay.duel;

import com.rs2.model.player.Player;
import java.util.ArrayList;

public final class DuelHistory {
    private static final int MAX_DISPLAY_RESULTS = 50;
    private static final int MAX_MATCHUP_RESULTS = 100;
    private static final int HUMAN_REPEAT_LOSS_LIMIT = 2;
    private static final int BOT_REPEAT_LOSS_LIMIT = 4;
    private static final int BOT_MATCHUP_RECENT_WINDOW = 24;
    private static final int BOT_REPEAT_LOSS_SPREAD = 3;
    private static ArrayList recentResults = new ArrayList();
    private static ArrayList recentWinnerKeys = new ArrayList();
    private static ArrayList recentLoserKeys = new ArrayList();

    public static void recordDuelResult(Player player, Player player2) {
        if (recentResults.size() >= MAX_DISPLAY_RESULTS) {
            recentResults.remove(0);
        }
        recentResults.add(String.valueOf(player.getUsername()) + " (" + player.getCombatLevel() + ") beat " + player2.getUsername() + " (" + player2.getCombatLevel() + ")");
        if (recentWinnerKeys.size() >= MAX_MATCHUP_RESULTS) {
            recentWinnerKeys.remove(0);
            recentLoserKeys.remove(0);
        }
        recentWinnerKeys.add(DuelHistory.getPlayerKey(player));
        recentLoserKeys.add(DuelHistory.getPlayerKey(player2));
    }

    public static int countRecentLossesTo(Player loser, Player winner) {
        return DuelHistory.countRecentLossesTo(loser, winner, MAX_MATCHUP_RESULTS);
    }

    public static int countRecentLossesTo(Player loser, Player winner, int maxRecentResults) {
        String loserKey = DuelHistory.getPlayerKey(loser);
        String winnerKey = DuelHistory.getPlayerKey(winner);
        int count = 0;
        int checked = 0;
        int index = recentWinnerKeys.size() - 1;
        while (index >= 0 && checked < maxRecentResults) {
            if (winnerKey.equals(recentWinnerKeys.get(index)) && loserKey.equals(recentLoserKeys.get(index))) {
                ++count;
            }
            --index;
            ++checked;
        }
        return count;
    }

    public static boolean shouldAvoidRepeatFarm(Player bot, Player opponent) {
        if (bot == null || opponent == null) {
            return false;
        }
        if (DuelHistory.isDuelArenaBot(bot) && DuelHistory.isDuelArenaBot(opponent)) {
            int recentLosses = DuelHistory.countRecentLossesTo(bot, opponent, BOT_MATCHUP_RECENT_WINDOW);
            int recentWins = DuelHistory.countRecentLossesTo(opponent, bot, BOT_MATCHUP_RECENT_WINDOW);
            return recentLosses >= BOT_REPEAT_LOSS_LIMIT && recentLosses >= recentWins + BOT_REPEAT_LOSS_SPREAD;
        }
        return DuelHistory.countRecentLossesTo(bot, opponent) >= HUMAN_REPEAT_LOSS_LIMIT;
    }

    private static boolean isDuelArenaBot(Player player) {
        return player != null && player.botEnabled && player.botMode == 7;
    }

    private static String getPlayerKey(Player player) {
        if (player == null) {
            return "";
        }
        if (player.getUsername() == null) {
            return String.valueOf(player.getIndex());
        }
        return player.getUsername().toLowerCase();
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
