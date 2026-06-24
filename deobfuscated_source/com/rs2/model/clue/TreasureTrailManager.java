/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.clue;

import com.rs2.bot.route.BotWorldRouteChoice;
import com.rs2.cache.CacheArchiveEntry;
import com.rs2.cache.CacheDefinitionIndex;
import com.rs2.cache.CacheFile;
import com.rs2.model.Entity;
import com.rs2.model.GameplayHelper;
import com.rs2.model.World;
import com.rs2.model.clue.CoordinateClueHandler;
import com.rs2.model.dialogue.DialogueManager;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.npc.Npc;
import com.rs2.model.npc.drop.NpcDropManager;
import com.rs2.model.player.GrandExchangeManager;
import com.rs2.model.player.Player;
import com.rs2.util.GameUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class TreasureTrailManager {
    public static final int[] DEFAULT_SLIDER_PUZZLE_PIECES = new int[]{3952, 3954, 3956, 3958, 3960, 3962, 3964, 3966, 3968, 3970, 3972, 3974, 3976, 3978, 3980, 3982, 3984, 3986, 3988, 3990, 3992, 3994, 3996, 3998, -1};
    public static final int[] SLIDER_PUZZLE_ONE_PIECES = new int[]{2749, 2750, 2751, 2752, 2753, 2754, 2755, 2756, 2757, 2758, 2759, 2760, 2761, 2762, 2763, 2764, 2765, 2766, 2767, 2768, 2769, 2770, 2771, 2772, -1};
    public static final int[] SLIDER_PUZZLE_TWO_PIECES = new int[]{3619, 3620, 3621, 3622, 3623, 3624, 3625, 3626, 3627, 3628, 3629, 3630, 3631, 3632, 3633, 3634, 3635, 3636, 3637, 3638, 3639, 3640, 3641, 3642, -1};
    public static final int[] SLIDER_PUZZLE_THREE_PIECES = new int[]{3643, 3644, 3645, 3646, 3647, 3648, 3649, 3650, 3651, 3652, 3653, 3654, 3655, 3656, 3657, 3658, 3659, 3660, 3661, 3662, 3663, 3664, 3665, 3666, -1};
    private static int[] tierOneCommonRewardItems = new int[]{554, 555, 556, 557, 558, 559, 560, 561, 562, 563, 564, 565, 566, 374, 380, 362, 1379, 1381, 1383, 1385, 1387, 1065, 1099, 1135, 1097, 1169, 841, 843, 845, 847, 849};
    private static int[] tierTwoCommonRewardItems = new int[]{1367, 1217, 1179, 1151, 1107, 1077, 1269, 1089, 1125, 1165, 1195, 1283, 1297, 1313, 1327, 1341, 1367, 1426, 334, 330, 851, 853, 855, 857, 859, 4821, 1765};
    private static int[] tierThreeCommonRewardItems = new int[]{1430, 1371, 1345, 1331, 1317, 1301, 1287, 1271, 1211, 1199, 1073, 1161, 1183, 1091, 1111, 1123, 1145, 1199, 1681, 4823};
    private static int[] tierFourCommonRewardItems = new int[]{1432, 1373, 1347, 1333, 1319, 1303, 1289, 1275, 1213, 1079, 1093, 1113, 1127, 1147, 1163, 1185, 1201, 4824, 386, 2491, 2497, 2503};
    private static int[] tierOneUniqueRewardItems = new int[]{2583, 2585, 2587, 2589, 2591, 2593, 2595, 2597, 3472, 3473, 2579, 2633, 2635, 2637, 2631, 7362, 7364, 7366, 7368, 7386, 7388, 7390, 7392, 7394, 7396, 7329, 7330, 7331, 7332, 7338, 7344, 7350, 7356, 3827, 3831, 3835, 3827, 3831, 3835, 3827, 3831, 3835};
    private static int[] tierTwoUniqueRewardItems;
    private static int[] tierThreeUniqueRewardItems;
    private static int[] tierFourUniqueRewardItems;

    static {
        int[] nArray = new int[]{2583, 2585, 2587, 2589, 2591, 2593, 2595, 2597, 3472, 3473, 2579, 2633, 2635, 2637, 2631, 7362, 7364, 7366, 7368, 7386, 7388, 7390, 7392, 7394, 7396, 7329, 7330, 7331, 7332, 7338, 7344, 7350, 7356, 3827, 3831, 3835, 3827, 3831, 3835, 3827, 3831, 3835, 10458, 10460, 10462, 10464, 10466, 10468};
        tierTwoUniqueRewardItems = new int[]{7329, 7330, 7331, 7319, 7321, 7323, 7325, 7327, 7370, 7372, 7378, 7380, 2645, 2647, 2649, 2579, 2577, 2599, 2601, 2603, 2605, 2607, 2609, 2611, 2613, 7334, 7340, 7346, 7352, 7358, 3828, 3832, 3836, 3829, 3833, 3837, 3829, 3833, 3837, 3829, 3833, 3837};
        int[] nArray2 = new int[]{7329, 7330, 7331, 7319, 7321, 7323, 7325, 7327, 7370, 7372, 7378, 7380, 2645, 2647, 2649, 2579, 2577, 2599, 2601, 2603, 2605, 2607, 2609, 2611, 2613, 7334, 7340, 7346, 7352, 7358, 3828, 3832, 3836, 3829, 3833, 3837, 3829, 3833, 3837, 3829, 3833, 3837, 10446, 10448, 10450, 10452, 10454, 10456};
        tierThreeUniqueRewardItems = new int[]{3480, 2653, 2655, 2657, 2659, 2661, 2663, 2665, 2667, 2669, 2671, 2673, 2675, 2581, 2651, 7398, 7399, 7400, 7329, 7330, 7331, 7374, 7376, 7382, 7384, 2615, 2617, 2619, 2621, 2623, 2625, 2627, 2629, 7336, 7342, 7348, 7354, 7360, 3830, 3834, 3838, 3830, 3834, 3838, 3830, 3834, 3838, 2639, 2640, 2643, 65000};
        int[] nArray3 = new int[]{3480, 2653, 2655, 2657, 2659, 2661, 2663, 2665, 2667, 2669, 2671, 2673, 2675, 2581, 2651, 7398, 7399, 7400, 7329, 7330, 7331, 7374, 7376, 7382, 7384, 2615, 2617, 2619, 2621, 2623, 2625, 2627, 2629, 7336, 7342, 7348, 7354, 7360, 3830, 3834, 3838, 3830, 3834, 3838, 3830, 3834, 3838, 2639, 2640, 2643, 65000, 10440, 10442, 10444, 10470, 10472, 10474, 10368, 10370, 10372, 10374, 10376, 10378, 10380, 10382, 10384, 10386, 10388, 10390};
        tierFourUniqueRewardItems = new int[]{3481, 3483, 3485, 3486, 3488, 2437, 2441, 2443, 2445, 2453, 3017};
        int[] nArray4 = new int[]{3481, 3483, 3485, 3486, 3488, 2437, 2441, 2443, 2445, 2453, 3017, 10330, 10332, 10334, 10336, 10338, 10340, 10342, 10344, 10346, 10348, 10350, 10352};
    }

    public static void filterRewardItemPools() {
        int[] nArray;
        int[] nArray2;
        int[] nArray3;
        int[] nArray4;
        int[] nArray5;
        int[] nArray6;
        int[] nArray7;
        int[] nArray8;
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        int[] nArray9 = nArray8 = (int[])tierOneUniqueRewardItems.clone();
        int n = nArray8.length;
        int n2 = 0;
        while (n2 < n) {
            int n3 = nArray9[n2];
            if (ItemDefinition.isDefined(n3)) {
                arrayList.add(n3);
            }
            ++n2;
        }
        tierOneUniqueRewardItems = new int[arrayList.size()];
        int n4 = 0;
        while (n4 < arrayList.size()) {
            TreasureTrailManager.tierOneUniqueRewardItems[n4] = (Integer)arrayList.get(n4);
            ++n4;
        }
        arrayList.clear();
        nArray9 = nArray7 = (int[])tierTwoUniqueRewardItems.clone();
        n = nArray7.length;
        n2 = 0;
        while (n2 < n) {
            int n5 = nArray9[n2];
            if (ItemDefinition.isDefined(n5)) {
                arrayList.add(n5);
            }
            ++n2;
        }
        tierTwoUniqueRewardItems = new int[arrayList.size()];
        int n6 = 0;
        while (n6 < arrayList.size()) {
            TreasureTrailManager.tierTwoUniqueRewardItems[n6] = (Integer)arrayList.get(n6);
            ++n6;
        }
        arrayList.clear();
        nArray9 = nArray6 = (int[])tierThreeUniqueRewardItems.clone();
        n = nArray6.length;
        n2 = 0;
        while (n2 < n) {
            int n7 = nArray9[n2];
            if (ItemDefinition.isDefined(n7)) {
                arrayList.add(n7);
            }
            ++n2;
        }
        tierThreeUniqueRewardItems = new int[arrayList.size()];
        int n8 = 0;
        while (n8 < arrayList.size()) {
            TreasureTrailManager.tierThreeUniqueRewardItems[n8] = (Integer)arrayList.get(n8);
            ++n8;
        }
        arrayList.clear();
        nArray9 = nArray5 = (int[])tierFourUniqueRewardItems.clone();
        n = nArray5.length;
        n2 = 0;
        while (n2 < n) {
            int n9 = nArray9[n2];
            if (ItemDefinition.isDefined(n9)) {
                arrayList.add(n9);
            }
            ++n2;
        }
        tierFourUniqueRewardItems = new int[arrayList.size()];
        int n10 = 0;
        while (n10 < arrayList.size()) {
            TreasureTrailManager.tierFourUniqueRewardItems[n10] = (Integer)arrayList.get(n10);
            ++n10;
        }
        arrayList.clear();
        nArray9 = nArray4 = (int[])tierOneCommonRewardItems.clone();
        n = nArray4.length;
        n2 = 0;
        while (n2 < n) {
            int n11 = nArray9[n2];
            if (ItemDefinition.isDefined(n11)) {
                arrayList.add(n11);
            }
            ++n2;
        }
        tierOneCommonRewardItems = new int[arrayList.size()];
        int n12 = 0;
        while (n12 < arrayList.size()) {
            TreasureTrailManager.tierOneCommonRewardItems[n12] = (Integer)arrayList.get(n12);
            ++n12;
        }
        arrayList.clear();
        nArray9 = nArray3 = (int[])tierTwoCommonRewardItems.clone();
        n = nArray3.length;
        n2 = 0;
        while (n2 < n) {
            int n13 = nArray9[n2];
            if (ItemDefinition.isDefined(n13)) {
                arrayList.add(n13);
            }
            ++n2;
        }
        tierTwoCommonRewardItems = new int[arrayList.size()];
        int n14 = 0;
        while (n14 < arrayList.size()) {
            TreasureTrailManager.tierTwoCommonRewardItems[n14] = (Integer)arrayList.get(n14);
            ++n14;
        }
        arrayList.clear();
        nArray9 = nArray2 = (int[])tierThreeCommonRewardItems.clone();
        n = nArray2.length;
        n2 = 0;
        while (n2 < n) {
            int n15 = nArray9[n2];
            if (ItemDefinition.isDefined(n15)) {
                arrayList.add(n15);
            }
            ++n2;
        }
        tierThreeCommonRewardItems = new int[arrayList.size()];
        int n16 = 0;
        while (n16 < arrayList.size()) {
            TreasureTrailManager.tierThreeCommonRewardItems[n16] = (Integer)arrayList.get(n16);
            ++n16;
        }
        arrayList.clear();
        nArray9 = nArray = (int[])tierFourCommonRewardItems.clone();
        n = nArray.length;
        n2 = 0;
        while (n2 < n) {
            int n17 = nArray9[n2];
            if (ItemDefinition.isDefined(n17)) {
                arrayList.add(n17);
            }
            ++n2;
        }
        tierFourCommonRewardItems = new int[arrayList.size()];
        int n18 = 0;
        while (n18 < arrayList.size()) {
            TreasureTrailManager.tierFourCommonRewardItems[n18] = (Integer)arrayList.get(n18);
            ++n18;
        }
        arrayList.clear();
    }

    public static void clearClueInterfaceText(Player player) {
        int n = 6968;
        while (n <= 6975) {
            Player player2 = player;
            player2.packetSender.sendInterfaceText("", n);
            ++n;
        }
    }

    public static void advanceOrCompleteTrail(Player player, int n, String string, boolean bl, String string2) {
        Npc npc = World.getNpcs()[player.getInteractionTargetIndex()];
        player.getDialogueManager().setDialogueNpcId(npc != null ? npc.getNpcId() : 0);
        player.sliderPuzzlePieces = new ItemStack[25];
        switch (n) {
            case 1: {
                if (player.treasureTrailStepCount > 0 && GameUtil.randomInclusive(6) == 0 || player.treasureTrailStepCount >= 3) {
                    if (bl) {
                        player.getDialogueManager().setDialogueId(10009);
                        player.getDialogueManager().setNextDialogueStep(1);
                        player.getDialogueManager().showNpcOneLineDialogue(string2, 588);
                        return;
                    }
                    TreasureTrailManager.completeTreasureTrail(player, n);
                    return;
                }
                player.getDialogueManager().showItemIdMessage(string, 2701);
                player.getDialogueManager().finishDialogue();
                TreasureTrailManager.awardNextClueScroll(player, n);
                return;
            }
            case 2: {
                if (player.treasureTrailStepCount >= 2 && GameUtil.randomInclusive(6) == 0 || player.treasureTrailStepCount >= 4) {
                    if (bl) {
                        player.getDialogueManager().setDialogueId(10009);
                        player.getDialogueManager().setNextDialogueStep(1);
                        player.getDialogueManager().showNpcOneLineDialogue(string2, 588);
                        return;
                    }
                    TreasureTrailManager.completeTreasureTrail(player, n);
                    return;
                }
                player.getDialogueManager().showItemIdMessage(string, 2701);
                player.getDialogueManager().finishDialogue();
                TreasureTrailManager.awardNextClueScroll(player, n);
                return;
            }
            case 3: {
                if (player.treasureTrailStepCount >= 3 && GameUtil.randomInclusive(6) == 0 || player.treasureTrailStepCount >= 5) {
                    if (bl) {
                        player.getDialogueManager().setDialogueId(10009);
                        player.getDialogueManager().setNextDialogueStep(1);
                        player.getDialogueManager().showNpcOneLineDialogue(string2, 588);
                        return;
                    }
                    TreasureTrailManager.completeTreasureTrail(player, n);
                    return;
                }
                player.getDialogueManager().showItemIdMessage(string, 2701);
                player.getDialogueManager().finishDialogue();
                TreasureTrailManager.awardNextClueScroll(player, n);
            }
        }
    }

    private static void awardNextClueScroll(Player player, int n) {
        ++player.treasureTrailStepCount;
        int n2 = 1;
        if (player.clueRequiredItems != null) {
            if (!player.getInventoryManager().containsItemStack(player.clueRequiredItems[0])) {
                n2 = 0;
            }
            if (n2 != 0) {
                n2 = 0;
                while (n2 < player.clueRequiredItems.length) {
                    player.getInventoryManager().removeItem(player.clueRequiredItems[n2]);
                    ++n2;
                }
                player.clueRequiredItems = null;
            } else {
                return;
            }
        }
        player.getInventoryManager().addOrDropItem(new ItemStack(TreasureTrailManager.randomClueItemForLevel(n), 1));
        String string = player.treasureTrailStepCount > 1 ? "steps" : "step";
        Player player2 = player;
        player2.packetSender.sendGameMessage("You have completed " + player.treasureTrailStepCount + " " + string + " so far.");
    }

    public static void completeTreasureTrail(Player player, int n) {
        ++player.treasureTrailStepCount;
        int n2 = 1;
        if (player.clueRequiredItems != null) {
            if (!player.getInventoryManager().containsItemStack(player.clueRequiredItems[0])) {
                n2 = 0;
            }
            if (n2 != 0) {
                n2 = 0;
                while (n2 < player.clueRequiredItems.length) {
                    player.getInventoryManager().removeItem(player.clueRequiredItems[n2]);
                    ++n2;
                }
                player.clueRequiredItems = null;
            } else {
                return;
            }
        }
        ItemStack[] itemStackArray = null;
        switch (n) {
            case 1: {
                itemStackArray = TreasureTrailManager.rollRewardItems(player, 1);
                ++player.easyCluesCompleted;
                break;
            }
            case 2: {
                itemStackArray = TreasureTrailManager.rollRewardItems(player, 2);
                ++player.mediumCluesCompleted;
                break;
            }
            case 3: {
                itemStackArray = TreasureTrailManager.rollRewardItems(player, 3);
                ++player.hardCluesCompleted;
            }
        }
        int[] rewardItemIds = new int[itemStackArray.length];
        int[] nArray = new int[itemStackArray.length];
        int n3 = 0;
        int n4 = 0;
        while (n4 < itemStackArray.length) {
            rewardItemIds[n4] = itemStackArray[n4].getId();
            nArray[n4] = itemStackArray[n4].getAmount();
            player.getInventoryManager().addOrDropItem(new ItemStack(rewardItemIds[n4], nArray[n4]));
            int n5 = GrandExchangeManager.getGuidePrice(rewardItemIds[n4]);
            n3 += n5 * nArray[n4];
            ++n4;
        }
        Player player2 = player;
        player2.packetSender.sendGameMessage("Well done, you've completed the Treasure Trail! (" + player.treasureTrailStepCount + " " + (player.treasureTrailStepCount < 2 ? "step" : "steps") + ")");
        Player player3 = player;
        player3.packetSender.sendGameMessage("Your treasure is worth around " + GameUtil.formatNumber(n3) + " coins!");
        player.treasureTrailStepCount = 0;
        player.sliderPuzzlePieces = new ItemStack[25];
        Player player4 = player;
        player4.packetSender.sendItemContainer(6963, itemStackArray);
        Player player5 = player;
        player5.packetSender.showInterface(6960);
        Player player6 = player;
        player6.packetSender.sendMusicJingle(237, 256);
    }

    private static ItemStack[] rollRewardItems(Player player, int n) {
        int n2;
        ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>();
        int n3 = 0;
        if (n == 1) {
            n3 = 2;
        } else if (n == 2) {
            n3 = 3;
        } else if (n == 3) {
            n3 = 4;
        }
        n3 += GameUtil.randomInt(3);
        int n4 = 0;
        while (n4 < n3) {
            n2 = n;
            ItemStack[] itemStackArray3 = null;
            if (n2 == 1) {
                itemStackArray3 = NpcDropManager.rollDrops((Entity)player, 6420, false);
            } else if (n2 == 2) {
                itemStackArray3 = NpcDropManager.rollDrops((Entity)player, 6421, false);
            } else if (n2 == 3) {
                itemStackArray3 = NpcDropManager.rollDrops((Entity)player, 6422, false);
            }
            if (itemStackArray3 != null) {
                n2 = 0;
                while (n2 < itemStackArray3.length) {
                    arrayList.add(itemStackArray3[n2]);
                    ++n2;
                }
            }
            ++n4;
        }
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
        int n5 = 0;
        while (n5 < arrayList.size()) {
            n2 = ((ItemStack)arrayList.get(n5)).getId();
            int amount = ((ItemStack)arrayList.get(n5)).getAmount();
            if (!hashMap.containsKey(n2)) {
                hashMap.put(n2, amount);
            } else {
                int n6 = (Integer)hashMap.get(n2);
                hashMap.put(n2, n6 + amount);
            }
            ++n5;
        }
        ArrayList<ItemStack> arrayList2 = new ArrayList<ItemStack>();
        for (Map.Entry<Integer, Integer> entry : hashMap.entrySet()) {
            int n7 = (Integer)entry.getKey();
            int n8 = (Integer)entry.getValue();
            arrayList2.add(new ItemStack(n7, n8));
        }
        ArrayList<ItemStack> arrayList3 = new ArrayList<ItemStack>();
        int n9 = 0;
        while (n9 < arrayList2.size()) {
            int n10 = ((ItemStack)arrayList2.get(n9)).getId();
            int n11 = ((ItemStack)arrayList2.get(n9)).getAmount();
            ItemDefinition itemDefinition = ((ItemStack)arrayList2.get(n9)).getDefinition();
            if (itemDefinition.isStackable() || n11 == 1) {
                arrayList3.add((ItemStack)arrayList2.get(n9));
            } else {
                int n12 = 0;
                while (n12 < n11) {
                    arrayList3.add(new ItemStack(n10, 1));
                    ++n12;
                }
            }
            ++n9;
        }
        ItemStack[] itemStackArray = new ItemStack[arrayList3.size()];
        n9 = 0;
        while (n9 < arrayList3.size()) {
            itemStackArray[n9] = (ItemStack)arrayList3.get(n9);
            ++n9;
        }
        return itemStackArray;
    }

    public static int randomClueItemForLevel(int n) {
        ArrayList<Integer> candidateItems = new ArrayList<Integer>();
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        switch (n) {
            case 1: {
                candidateItems.add(CacheArchiveEntry.randomMapClueItemForLevel(1));
                candidateItems.add(CacheFile.randomSearchClueItemForLevel(1));
                candidateItems.add(CacheDefinitionIndex.randomNpcClueItemForLevel(1));
                candidateItems.add(CacheArchiveEntry.randomMapClueItemForLevel(1));
                candidateItems.add(CacheFile.randomSearchClueItemForLevel(1));
                candidateItems.add(CacheDefinitionIndex.randomNpcClueItemForLevel(1));
                candidateItems.add(BotWorldRouteChoice.randomCrypticDigClueItemForLevel(1));
                break;
            }
            case 2: {
                candidateItems.add(GameplayHelper.randomAnagramClueItemForLevel(2));
                candidateItems.add(CacheArchiveEntry.randomMapClueItemForLevel(2));
                candidateItems.add(CacheFile.randomSearchClueItemForLevel(2));
                candidateItems.add(CacheDefinitionIndex.randomNpcClueItemForLevel(2));
                candidateItems.add(CoordinateClueHandler.randomClueItemForLevel(2));
                break;
            }
            case 3: {
                candidateItems.add(GameplayHelper.randomAnagramClueItemForLevel(3));
                candidateItems.add(CacheArchiveEntry.randomMapClueItemForLevel(3));
                candidateItems.add(CacheFile.randomSearchClueItemForLevel(3));
                candidateItems.add(CacheDefinitionIndex.randomNpcClueItemForLevel(3));
                candidateItems.add(BotWorldRouteChoice.randomCrypticDigClueItemForLevel(3));
                candidateItems.add(CoordinateClueHandler.randomClueItemForLevel(3));
                break;
            }
            default: {
                return -1;
            }
        }
        for (Integer clueItemId : candidateItems) {
            n = clueItemId;
            if (!ItemDefinition.isDefined(n)) continue;
            arrayList.add(n);
        }
        if (arrayList.isEmpty()) {
            return -1;
        }
        return (Integer)arrayList.get(GameUtil.randomInclusive(arrayList.size() - 1));
    }

    public static boolean handleRewardContainerItem(Player player, int n) {
        switch (n) {
            case 2717: {
                player.getInventoryManager().removeItem(new ItemStack(n, 1));
                TreasureTrailManager.completeTreasureTrail(player, 1);
                return true;
            }
            case 2714: {
                player.getInventoryManager().removeItem(new ItemStack(n, 1));
                TreasureTrailManager.completeTreasureTrail(player, 2);
                return true;
            }
            case 2715: {
                player.getInventoryManager().removeItem(new ItemStack(n, 1));
                TreasureTrailManager.completeTreasureTrail(player, 3);
                return true;
            }
            case 2724: {
                player.getInventoryManager().removeItem(new ItemStack(n, 1));
                TreasureTrailManager.advanceOrCompleteTrail(player, 1, "You've found another clue!", false, "Here is your reward!");
                return true;
            }
            case 2726: {
                player.getInventoryManager().removeItem(new ItemStack(n, 1));
                TreasureTrailManager.advanceOrCompleteTrail(player, 2, "You've found another clue!", false, "Here is your reward!");
                return true;
            }
            case 2728: {
                player.getInventoryManager().removeItem(new ItemStack(n, 1));
                TreasureTrailManager.advanceOrCompleteTrail(player, 3, "You've found another clue!", false, "Here is your reward!");
                return true;
            }
        }
        return false;
    }

    public static void spawnClueWizard(Player player) {
        String string;
        int n = player.isInWilderness() ? 1007 : 1264;
        String string2 = string = player.isInWilderness() ? "Die, human!" : "For Saradomin!";
        if (!GameplayHelper.hasActiveTemporaryNpc(player, n)) {
            Npc npc = new Npc(n);
            GameplayHelper.spawnOwnedNpcAdjacentToPlayer(player, npc, true, true);
            npc.getUpdateState().setForcedText(string);
        }
    }

    public static void recordClueWizardKill(Player player, Npc npc) {
        if (npc.getNpcId() == 1007 || npc.getNpcId() == 1264) {
            player.killedClueAttacker = true;
        }
    }

}

