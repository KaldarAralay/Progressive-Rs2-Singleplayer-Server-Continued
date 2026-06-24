/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.item.action;

import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.util.GameUtil;

public final class BirdNestSearchHandler {
    private static int[] commonSeedNestRewards = new int[]{5312, 5283, 5284, 5285, 5286, 5313};
    private static int[] uncommonSeedNestRewards = new int[]{5314, 5288, 5287, 5315, 5289};
    private static int[] rareSeedNestRewards = new int[]{5316, 5290};
    private static int[] veryRareSeedNestRewards = new int[]{5317};
    private static int[] commonRingNestRewards = new int[]{1635, 1637};
    private static int[] uncommonRingNestRewards = new int[]{1639};
    private static int[] rareRingNestRewards = new int[]{1641};
    private static int[] veryRareRingNestRewards = new int[]{1643};

    public static boolean searchNest(Player player, int n) {
        int[] nArray;
        int[] nArray2;
        Object object;
        int[] nArray3;
        switch (n) {
            case 5070: {
                player.getInventoryManager().removeItem(new ItemStack(n));
                player.getInventoryManager().addOrDropItem(new ItemStack(5076));
                player.getInventoryManager().addOrDropItem(new ItemStack(5075));
                return true;
            }
            case 5071: {
                player.getInventoryManager().removeItem(new ItemStack(n));
                player.getInventoryManager().addOrDropItem(new ItemStack(5078));
                player.getInventoryManager().addOrDropItem(new ItemStack(5075));
                return true;
            }
            case 5072: {
                player.getInventoryManager().removeItem(new ItemStack(n));
                player.getInventoryManager().addOrDropItem(new ItemStack(5077));
                player.getInventoryManager().addOrDropItem(new ItemStack(5075));
                return true;
            }
            case 5073: {
                nArray3 = commonSeedNestRewards;
                object = uncommonSeedNestRewards;
                nArray2 = rareSeedNestRewards;
                nArray = veryRareSeedNestRewards;
                break;
            }
            case 5074: {
                nArray3 = commonRingNestRewards;
                object = uncommonRingNestRewards;
                nArray2 = rareRingNestRewards;
                nArray = veryRareRingNestRewards;
                break;
            }
            default: {
                return false;
            }
        }
        int n2 = GameUtil.randomInclusive(100);
        int n3 = n2 <= 60 ? nArray3[GameUtil.randomInclusive(nArray3.length - 1)] : (n2 <= 80 ? ((int[])object)[GameUtil.randomInclusive(((int[])object).length - 1)] : (n2 <= 95 ? nArray2[GameUtil.randomInclusive(nArray2.length - 1)] : nArray[GameUtil.randomInclusive(0)]));
        Player player2 = player;
        object = player2;
        player2.packetSender.sendGameMessage("You search the nest...and find something in it!");
        player.getInventoryManager().removeItem(new ItemStack(n));
        player.getInventoryManager().addOrDropItem(new ItemStack(n3));
        player.getInventoryManager().addOrDropItem(new ItemStack(5075));
        return true;
    }
}

