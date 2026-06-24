/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.herblore;

import com.rs2.ServerSettings;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.quest.QuestDefinition;

public final class PestleAndMortarHandler {
    private static int PESTLE_AND_MORTAR_ITEM_ID = 233;
    private static final int[][] GRINDING_RECIPES = new int[][]{{237, 235}, {1973, 1975}, {5075, 6693}, {243, 241}};

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static boolean handlePestleAndMortar(Player player, ItemStack itemStack, ItemStack itemStack2, int n, int n2) {
        try {
            n2 = itemStack.getId();
            int n3 = itemStack2.getId();
            int[][] nArray = GRINDING_RECIPES;
            int n4 = 0;
            while (true) {
                if (n4 >= 4) {
                    return false;
                }
                int[] nArray2 = nArray[n4];
                int n5 = nArray2[0];
                int n6 = nArray2[1];
                if (n2 == n5 && n3 == PESTLE_AND_MORTAR_ITEM_ID || n2 == PESTLE_AND_MORTAR_ITEM_ID && n3 == n5) {
                    player.getInventoryManager().getContainer().getItemAt(n).getId();
                    if (!ServerSettings.herbloreEnabled) {
                        Player player2 = player;
                        player2.packetSender.sendGameMessage("This skill is currently disabled.");
                        return true;
                    }
                    if (player.getQuestState(29) != 1) {
                        Object object = QuestDefinition.forId(29);
                        object = ((QuestDefinition)object).getName();
                        Player player3 = player;
                        player3.packetSender.sendGameMessage("You need to complete " + (String)object + " to do this.");
                        return true;
                    }
                    if (!player.getInventoryManager().containsItem(n5)) return true;
                    player.getInventoryManager().removeItem(new ItemStack(n5, 1));
                    player.getInventoryManager().addItem(new ItemStack(n6, 1));
                    Player player4 = player;
                    player4.packetSender.sendSoundEffect(373, 1, 0);
                    player4 = player;
                    player4.packetSender.sendGameMessage("You grind the " + new ItemStack(n5).getDefinition().getName() + ".");
                    return true;
                }
                ++n4;
            }
        }
        catch (Exception exception) {}
        return false;
    }
}

