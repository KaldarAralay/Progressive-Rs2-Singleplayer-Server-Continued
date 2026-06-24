/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.npc.drop;

import com.rs2.ServerSettings;
import com.rs2.model.Entity;
import com.rs2.model.GameplayHelper;
import com.rs2.model.clue.TreasureTrailManager;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.npc.NpcDefinition;
import com.rs2.model.player.Player;
import com.rs2.util.GameUtil;
import java.util.ArrayList;
import java.util.Random;

public final class NpcDropManager {
    private static Random random = new Random();
    private static int[] HERB_DROP_ITEM_IDS = new int[]{199, 201, 203, 205, 207, 209, 211, 213, 215, 217, 219, 2485, 3049, 3051};
    private static int[] SEED_DROP_ITEM_IDS = new int[]{5291, 5292, 5293, 5294, 5295, 5297, 5298, 5299, 5301, 5303, 5304, 5302, 5296, 5300};
    private static int[] GEM_DROP_ITEM_IDS = new int[]{1623, 1621, 1619, 1617};
    private static int[] HERB_DROP_WEIGHTS = new int[]{4, 4, 4, 4, 4, 3, 3, 3, 2, 2, 2, 1, 1, 1};
    private static int[] SEED_DROP_WEIGHTS = new int[]{4, 4, 4, 4, 4, 3, 3, 3, 2, 2, 2, 1, 1, 1};
    private static int[] GEM_DROP_WEIGHTS = new int[]{4, 3, 2, 1};
    private static ArrayList<Integer> weightedVirtualDropChoices = new ArrayList<Integer>();

    private static int resolveVirtualDropItemId(Entity entity, int n) {
        Player player = null;
        if (entity.isPlayer()) {
            player = (Player)entity;
        }
        Random random = new Random();
        int n2 = 0;
        if (!entity.isPlayer() || player.ownsClueScroll()) {
            n2 = 1;
        }
        weightedVirtualDropChoices.clear();
        int n3 = n - 65000;
        switch (n3) {
            case 0: {
                return -1;
            }
            case 1: {
                if (n2 != 0) {
                    return -1;
                }
                return TreasureTrailManager.randomClueItemForLevel(1);
            }
            case 2: {
                if (n2 != 0) {
                    return -1;
                }
                return TreasureTrailManager.randomClueItemForLevel(2);
            }
            case 3: {
                if (n2 != 0) {
                    return -1;
                }
                return TreasureTrailManager.randomClueItemForLevel(3);
            }
            case 4: {
                if (n2 != 0) {
                    return -1;
                }
                return TreasureTrailManager.randomClueItemForLevel(3);
            }
            case 5: {
                n3 = 0;
                while (n3 < HERB_DROP_ITEM_IDS.length) {
                    n = HERB_DROP_WEIGHTS[n3];
                    n2 = 0;
                    while (n2 < n) {
                        weightedVirtualDropChoices.add(HERB_DROP_ITEM_IDS[n3]);
                        ++n2;
                    }
                    ++n3;
                }
                return weightedVirtualDropChoices.get(random.nextInt(weightedVirtualDropChoices.size()));
            }
            case 6: {
                n3 = 0;
                while (n3 < GEM_DROP_ITEM_IDS.length) {
                    n = GEM_DROP_WEIGHTS[n3];
                    n2 = 0;
                    while (n2 < n) {
                        if (!ServerSettings.freeToPlayWorld && (player == null || player.isMember()) || !ItemDefinition.forId(GEM_DROP_ITEM_IDS[n3]).isMembersOnly()) {
                            weightedVirtualDropChoices.add(GEM_DROP_ITEM_IDS[n3]);
                        }
                        ++n2;
                    }
                    ++n3;
                }
                return weightedVirtualDropChoices.get(random.nextInt(weightedVirtualDropChoices.size()));
            }
            case 7: {
                n3 = 0;
                while (n3 < SEED_DROP_ITEM_IDS.length) {
                    n = SEED_DROP_WEIGHTS[n3];
                    n2 = 0;
                    while (n2 < n) {
                        weightedVirtualDropChoices.add(SEED_DROP_ITEM_IDS[n3]);
                        ++n2;
                    }
                    ++n3;
                }
                return weightedVirtualDropChoices.get(random.nextInt(weightedVirtualDropChoices.size()));
            }
        }
        return -1;
    }

    private static boolean isVirtualDropId(int n) {
        return n >= 65000;
    }

    private static int randomInclusive(int n, int n2) {
        n = random.nextInt(n2 + 1 - n) + n;
        return n;
    }

    private static NpcDropTable getDropTableForNpcId(int n) {
        NpcDefinition npcDefinition = NpcDefinition.forId(n);
        int n2 = npcDefinition.getDropTableNpcIdOverride();
        n = n2 == -1 ? n : n2;
        return NpcDropTable.forNpcId(n);
    }

    public static ItemStack[] rollDrops(Entity entity, int n, boolean bl) {
        NpcDefinition npcDefinition = NpcDefinition.forId(n);
        ItemStack[] guaranteedDrops = null;
        NpcDropEntry[] guaranteedEntries = NpcDropManager.getDropTableForNpcId(n).getGuaranteedDrops(entity);
        if (guaranteedEntries != null && guaranteedEntries.length > 0) {
            ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>();
            int n4 = 0;
            while (n4 < guaranteedEntries.length) {
                NpcDropEntry npcDropEntry = guaranteedEntries[n4];
                int n3 = npcDropEntry.getItemId();
                int n2 = 1;
                if (npcDropEntry.getFixedAmount() > 0) {
                    n2 = npcDropEntry.getFixedAmount();
                }
                if (npcDropEntry.getMinAmount() > 0) {
                    n2 = NpcDropManager.randomInclusive(npcDropEntry.getMinAmount(), npcDropEntry.getMaxAmount());
                }
                if (npcDropEntry.getAmountOptions().length > 0 && npcDropEntry.getItemId() > 0) {
                    int n5 = random.nextInt(npcDropEntry.getAmountOptions().length);
                    n2 = npcDropEntry.getAmountOptions()[n5];
                }
                if (npcDropEntry.getMinAmounts().length > 0 && npcDropEntry.getItemId() > 0) {
                    int n6 = random.nextInt(npcDropEntry.getAmountOptions().length);
                    n2 = NpcDropManager.randomInclusive(npcDropEntry.getMinAmounts()[n6], npcDropEntry.getMaxAmounts()[n6]);
                }
                if (NpcDropManager.isVirtualDropId(n3)) {
                    if (NpcDropManager.isVirtualDropTableId(n3)) {
                        ItemStack[] itemStackArray4 = NpcDropManager.resolveVirtualDropTable(entity, n3, n2, npcDefinition.getCombatLevel());
                        if (itemStackArray4 != null) {
                            int n7 = 0;
                            while (n7 < itemStackArray4.length) {
                                arrayList.add(NpcDropManager.createItemStack(itemStackArray4[n7].getId(), itemStackArray4[n7].getAmount()));
                                ++n7;
                            }
                        }
                    } else {
                        n3 = NpcDropManager.resolveVirtualDropItemId(entity, n3);
                    }
                }
                if (n3 == 5509) {
                    n3 = GameplayHelper.selectNextAvailableEssencePouch(entity, n3);
                }
                if (ItemDefinition.isDefined(n3)) {
                    arrayList.add(NpcDropManager.createItemStack(n3, n2));
                }
                ++n4;
            }
            guaranteedDrops = arrayList.toArray(new ItemStack[arrayList.size()]);
        }
        ItemStack[] weightedDrops = NpcDropManager.rollWeightedDrops(entity, n, true, 1, npcDefinition.getCombatLevel());
        ItemStack[] independentDrops = NpcDropManager.rollIndependentDrops(entity, n, true, 1, npcDefinition.getCombatLevel());
        int guaranteedCount = guaranteedDrops == null ? 0 : guaranteedDrops.length;
        int weightedCount = weightedDrops == null ? 0 : weightedDrops.length;
        int independentCount = independentDrops == null ? 0 : independentDrops.length;
        ItemStack[] itemStackArray = new ItemStack[guaranteedCount + weightedCount + independentCount];
        int index = 0;
        index = NpcDropManager.copyDrops(guaranteedDrops, itemStackArray, index);
        index = NpcDropManager.copyDrops(weightedDrops, itemStackArray, index);
        NpcDropManager.copyDrops(independentDrops, itemStackArray, index);
        return itemStackArray;
    }

    private static int copyDrops(ItemStack[] source, ItemStack[] destination, int offset) {
        if (source == null) {
            return offset;
        }
        int n = 0;
        while (n < source.length) {
            destination[offset++] = source[n];
            ++n;
        }
        return offset;
    }

    private static ItemStack[] rollIndependentDrops(Entity entity, int n, boolean bl, int n2, int n3) {
        NpcDropEntry[] npcDropEntryArray = NpcDropManager.getDropTableForNpcId(n).getIndependentDrops(entity);
        if (npcDropEntryArray == null) {
            return null;
        }
        if (npcDropEntryArray.length == 0) {
            return null;
        }
        ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>();
        int n4 = 0;
        while (n4 < npcDropEntryArray.length) {
            double d = npcDropEntryArray[n4].getChanceNumerator();
            double d2 = npcDropEntryArray[n4].getChanceDenominator();
            double d3 = d / d2;
            if (ServerSettings.customDropRatesEnabled) {
                d3 = NpcDropManager.applyCustomDropRate(d, d2);
            }
            if (GameUtil.rollChance(d3)) {
                ItemStack[] itemStackArray = NpcDropManager.rollDropEntrySelection(entity, n, true, 1, n3, true, n4);
                int n5 = 0;
                while (itemStackArray != null && n5 < itemStackArray.length) {
                    arrayList.add(itemStackArray[n5]);
                    ++n5;
                }
            }
            ++n4;
        }
        return arrayList.toArray(new ItemStack[arrayList.size()]);
    }

    private static ItemStack[] rollWeightedDrops(Entity entity, int n, boolean bl, int n2, int n3) {
        return NpcDropManager.rollDropEntrySelection(entity, n, true, n2, n3, false, -1);
    }

    private static ItemStack[] rollDropEntrySelection(Entity entity, int n, boolean bl, int n3, int n4, boolean bl2, int n6) {
        NpcDropTable npcDropTable = NpcDropManager.getDropTableForNpcId(n);
        NpcDropEntry[] npcDropEntryArray = bl2 ? npcDropTable.getIndependentDrops(entity) : npcDropTable.getWeightedDrops(entity);
        if (npcDropEntryArray == null) {
            return null;
        }
        if (npcDropEntryArray.length == 0) {
            return null;
        }
        NpcDropEntry[] npcDropEntryArray2 = npcDropEntryArray;
        boolean bl3 = false;
        if (!bl2) {
            if (ServerSettings.rareDropChanceDivisor > 0 || ServerSettings.customDropRatesEnabled) {
                bl3 = true;
            }
            if (bl3 && ServerSettings.rareDropChanceDivisor > 0) {
                if (bl) {
                    NpcDropManager.assignMissingWeightedChances(npcDropEntryArray2);
                }
                ArrayList<NpcDropEntry> commonDrops = new ArrayList<NpcDropEntry>();
                ArrayList<NpcDropEntry> rareDrops = new ArrayList<NpcDropEntry>();
                int n8 = 0;
                while (n8 < npcDropEntryArray.length) {
                    NpcDropEntry npcDropEntry = npcDropEntryArray[n8];
                    npcDropEntry.getItemId();
                    double d = npcDropEntry.getChanceNumerator();
                    double d2 = npcDropEntry.getChanceDenominator();
                    double d3 = d / d2;
                    double d4 = 1.0 / (double)ServerSettings.rareDropChanceDivisor;
                    if (d3 <= d4) {
                        rareDrops.add(npcDropEntry);
                    } else {
                        commonDrops.add(npcDropEntry);
                    }
                    ++n8;
                }
                ArrayList<NpcDropEntry> selectedDrops = rareDrops.size() == 0 ? commonDrops : rareDrops;
                npcDropEntryArray2 = selectedDrops.toArray(new NpcDropEntry[selectedDrops.size()]);
            }
            if ((n == 6391 || n == 6392 || n == 6393) && entity.isPlayer()) {
                Player player = (Player)entity;
                boolean bl4;
                if (player.getEquipmentManager().getItemIdAtSlot(12) == 2572) {
                    player.cd = true;
                    bl4 = true;
                } else {
                    bl4 = false;
                }
                if (bl4) {
                    npcDropEntryArray2 = NpcDropManager.removeNoDropEntries(npcDropEntryArray);
                    bl3 = true;
                }
            }
        }
        int noDrop = 0;
        ItemStack[] itemStackArray = new ItemStack[1];
        int selectedIndex;
        if (bl) {
            if (bl2) {
                selectedIndex = n6;
            } else {
                NpcDropManager.assignMissingWeightedChances(npcDropEntryArray2);
                selectedIndex = NpcDropManager.selectDropIndex(npcDropEntryArray2, bl3);
            }
            if (selectedIndex == -1) {
                noDrop = 1;
                selectedIndex = 0;
            }
        } else {
            selectedIndex = random.nextInt(npcDropEntryArray2.length);
        }
        NpcDropEntry selectedEntry = npcDropEntryArray2[selectedIndex];
        int itemId;
        int n7 = 0;
        if (selectedEntry.getItemIds().length > 0) {
            n7 = random.nextInt(selectedEntry.getItemIds().length);
            itemId = selectedEntry.getItemIds()[n7];
        } else {
            itemId = selectedEntry.getItemId();
        }
        n7 = NpcDropManager.rollEntryAmountForInitialVirtualCheck(selectedEntry);
        if (noDrop != 0) {
            itemId = 65000;
        }
        if (NpcDropManager.isVirtualDropId(itemId)) {
            if (NpcDropManager.isVirtualDropTableId(itemId)) {
                ItemStack[] nestedDrops = NpcDropManager.resolveVirtualDropTable(entity, itemId, n7 * n3, n4);
                if (nestedDrops != null) {
                    itemStackArray = new ItemStack[nestedDrops.length];
                    int n10 = 0;
                    while (n10 < nestedDrops.length) {
                        itemStackArray[n10] = NpcDropManager.createItemStack(nestedDrops[n10].getId(), nestedDrops[n10].getAmount());
                        ++n10;
                    }
                }
            } else {
                itemId = NpcDropManager.resolveVirtualDropItemId(entity, itemId);
            }
        }
        if (itemId == 5509) {
            itemId = GameplayHelper.selectNextAvailableEssencePouch(entity, itemId);
        }
        int amount = NpcDropManager.rollEntryAmount(selectedEntry);
        if (selectedEntry.getItemIds().length > 1) {
            ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>();
            int n11 = 0;
            while (n11 < selectedEntry.getItemIds().length) {
                itemId = selectedEntry.getItemIds()[n11];
                amount = selectedEntry.getMinAmounts().length > 1 ? NpcDropManager.randomInclusive(selectedEntry.getMinAmounts()[n11], selectedEntry.getMaxAmounts()[n11]) : selectedEntry.getAmountOptions()[n11];
                if (NpcDropManager.isVirtualDropId(itemId)) {
                    if (NpcDropManager.isVirtualDropTableId(itemId)) {
                        ItemStack[] nestedDrops = NpcDropManager.resolveVirtualDropTable(entity, itemId, amount * n3, n4);
                        int n12 = 0;
                        while (nestedDrops != null && n12 < nestedDrops.length) {
                            arrayList.add(nestedDrops[n12]);
                            ++n12;
                        }
                    } else {
                        itemId = NpcDropManager.resolveVirtualDropItemId(entity, itemId);
                    }
                }
                if (itemId == 5509) {
                    itemId = GameplayHelper.selectNextAvailableEssencePouch(entity, itemId);
                }
                if (ItemDefinition.isDefined(itemId)) {
                    arrayList.add(NpcDropManager.createItemStack(itemId, amount * n3));
                }
                ++n11;
            }
            itemStackArray = arrayList.toArray(new ItemStack[arrayList.size()]);
        } else if (ItemDefinition.isDefined(itemId)) {
            itemStackArray[0] = NpcDropManager.createItemStack(itemId, amount * n3);
        }
        return itemStackArray;
    }

    private static int rollEntryAmountForInitialVirtualCheck(NpcDropEntry npcDropEntry) {
        int n = NpcDropManager.rollEntryAmount(npcDropEntry);
        if (npcDropEntry.getItemIds().length > 1 && npcDropEntry.getMinAmounts().length <= 1) {
            n = npcDropEntry.getAmountOptions()[0];
        }
        if (npcDropEntry.getMinAmounts().length > 1) {
            n = NpcDropManager.randomInclusive(npcDropEntry.getMinAmounts()[0], npcDropEntry.getMaxAmounts()[0]);
        }
        return n;
    }

    private static int rollEntryAmount(NpcDropEntry npcDropEntry) {
        int n = 0;
        if (npcDropEntry.getFixedAmount() > 0) {
            n = npcDropEntry.getFixedAmount();
        }
        if (npcDropEntry.getMinAmount() > 0) {
            n = NpcDropManager.randomInclusive(npcDropEntry.getMinAmount(), npcDropEntry.getMaxAmount());
        }
        if (npcDropEntry.getAmountOptions().length > 0 && npcDropEntry.getItemId() > 0) {
            int n2 = random.nextInt(npcDropEntry.getAmountOptions().length);
            n = npcDropEntry.getAmountOptions()[n2];
        }
        return n;
    }

    private static NpcDropEntry[] removeNoDropEntries(NpcDropEntry[] npcDropEntryArray) {
        int n;
        ArrayList<NpcDropEntry> arrayList = new ArrayList<NpcDropEntry>();
        int n2 = 0;
        while (n2 < npcDropEntryArray.length) {
            n = npcDropEntryArray[n2].getItemId();
            if (n != 65000) {
                arrayList.add(npcDropEntryArray[n2]);
            }
            ++n2;
        }
        NpcDropEntry[] npcDropEntryArray2 = new NpcDropEntry[arrayList.size()];
        n = 0;
        while (n < arrayList.size()) {
            arrayList.get(n).getItemId();
            npcDropEntryArray2[n] = arrayList.get(n);
            ++n;
        }
        return npcDropEntryArray2;
    }

    private static boolean isVirtualDropTableId(int n) {
        return n >= 65008;
    }

    private static ItemStack[] resolveVirtualDropTable(Entity entity, int n, int n2, int n3) {
        ItemStack[] itemStackArray = null;
        if (n == 65008) {
            itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6391, true, n2, n3);
        } else if (n == 65009) {
            itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6392, true, n2, n3);
        } else if (n == 65010) {
            itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6393, true, n2, n3);
        } else if (n == 65011) {
            itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6394, true, n2, n3);
        } else if (n == 65012) {
            itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6395, true, n2, n3);
        } else if (n == 65013) {
            itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6396, true, n2, n3);
        } else if (n == 65014) {
            itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6397, true, n2, n3);
        } else if (n == 65015) {
            itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6398, true, n2, n3);
        } else if (n == 65016) {
            itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6399, true, n2, n3);
        } else if (n == 65017) {
            itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6400, true, n2, n3);
        } else if (n == 65018) {
            n = 1 + n3 * 10;
            if ((n = GameUtil.randomInt(n)) < 485) {
                itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6401, true, n2, n3);
            } else if (n >= 485 && n < 728) {
                itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6414, true, n2, n3);
            } else if (n >= 728 && n < 850) {
                itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6415, true, n2, n3);
            } else if (n >= 850 && n < 947) {
                itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6416, true, n2, n3);
            } else if (n >= 947 && n < 995) {
                itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6417, true, n2, n3);
            } else if (n >= 995) {
                itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6418, true, n2, n3);
            }
        } else if (n == 65019) {
            itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6402, true, n2, n3);
        } else if (n == 65020) {
            itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6403, true, n2, n3);
        } else if (n == 65021) {
            itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6404, true, n2, n3);
        } else if (n == 65022) {
            itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6405, true, n2, n3);
        } else if (n == 65023) {
            itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6406, true, n2, n3);
        } else if (n == 65024) {
            itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6407, true, n2, n3);
        } else if (n == 65025) {
            itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6408, true, n2, n3);
        } else if (n == 65026) {
            itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6409, true, n2, n3);
        } else if (n == 65027) {
            itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6410, true, n2, n3);
        } else if (n == 65028) {
            itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6411, true, n2, n3);
        } else if (n == 65029) {
            itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6412, true, n2, n3);
        } else if (n == 65030) {
            itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6413, true, n2, n3);
        } else if (n == 65036) {
            itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6419, true, n2, n3);
        } else if (n == 65040) {
            itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6423, true, n2, n3);
        } else if (n == 65041) {
            itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6424, true, n2, n3);
        } else if (n == 65042) {
            itemStackArray = NpcDropManager.rollWeightedDrops(entity, 6425, true, n2, n3);
        }
        return itemStackArray;
    }

    private static void assignMissingWeightedChances(NpcDropEntry[] npcDropEntryArray) {
        int n = npcDropEntryArray.length;
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        double d = 0.0;
        int n2 = 0;
        while (n2 < n) {
            if (npcDropEntryArray[n2].getChanceType() == 1) {
                arrayList.add(n2);
            } else {
                double d2 = npcDropEntryArray[n2].getChanceNumerator();
                double d3 = npcDropEntryArray[n2].getChanceDenominator();
                double d4 = d2 / d3;
                d += d4;
            }
            ++n2;
        }
        if (arrayList.size() > 0) {
            double d5;
            double d6 = d5 = (1.0 - d) / (double)arrayList.size();
            double d7 = Math.floor(d5);
            double d8 = d6 - d7;
            long l = NpcDropManager.greatestCommonDivisor(Math.round(d8 * 1.0E9), 1000000000L);
            long l2 = Math.round(d8 * 1.0E9) / l;
            long l3 = 1000000000L / l;
            String string = String.valueOf((long)(d7 * (double)l3) + l2) + "/" + l3;
            String[] stringArray = string.split("/");
            int n3 = 0;
            while (n3 < arrayList.size()) {
                int n4 = arrayList.get(n3);
                npcDropEntryArray[n4].setChanceNumerator(Integer.parseInt(stringArray[0]));
                npcDropEntryArray[n4].setChanceDenominator(Integer.parseInt(stringArray[1]));
                ++n3;
            }
        }
    }

    private static long greatestCommonDivisor(long l, long l2) {
        while (l != 0L) {
            if (l2 == 0L) {
                return l;
            }
            if (l < l2) {
                l2 %= l;
                continue;
            }
            long l3 = l2;
            l2 = l % l2;
            l = l3;
        }
        return l2;
    }

    private static double applyCustomDropRate(double d, double d2) {
        double d6 = d / d2;
        double d7 = d6 >= 0.043478260869565216 ? d * ServerSettings.commonDropRateMultiplier : (d6 >= 0.011111111111111112 ? d * ServerSettings.uncommonDropRateMultiplier : (d6 >= 0.0012300123001230013 ? d * ServerSettings.rareDropRateMultiplier : d * ServerSettings.veryRareDropRateMultiplier));
        double d8 = d7 / d2;
        if (ServerSettings.dropRateCap > 0) {
            double d3 = (double)ServerSettings.dropRateCap;
            double d4 = 1.0 / d3;
            if (d8 < d4) {
                d8 = d4;
            }
        }
        return d8;
    }

    private static int selectDropIndex(NpcDropEntry[] npcDropEntryArray, boolean bl) {
        double d = 1.0;
        if (bl) {
            d = 0.0;
        }
        int n = npcDropEntryArray.length;
        int n2 = 0;
        while (n2 < n) {
            double d2 = npcDropEntryArray[n2].getChanceNumerator();
            double d3 = npcDropEntryArray[n2].getChanceDenominator();
            double d4 = d2 / d3;
            if (ServerSettings.customDropRatesEnabled) {
                d4 = NpcDropManager.applyCustomDropRate(d2, d3);
            }
            if (bl) {
                d += d4;
            }
            ++n2;
        }
        double d5 = Math.random();
        double d6 = d5 * d;
        double d7 = 0.0;
        int n3 = 0;
        while (n3 < n) {
            double d9 = npcDropEntryArray[n3].getChanceNumerator();
            double d10 = npcDropEntryArray[n3].getChanceDenominator();
            double d11 = d9 / d10;
            if (ServerSettings.customDropRatesEnabled) {
                d11 = NpcDropManager.applyCustomDropRate(d9, d10);
            }
            d7 += d11;
            if (d7 >= d6) {
                return n3;
            }
            ++n3;
        }
        return -1;
    }

    private static ItemStack createItemStack(int n, int n2) {
        return new ItemStack(n, n2);
    }
}
