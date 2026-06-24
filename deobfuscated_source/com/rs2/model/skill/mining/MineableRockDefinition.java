/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.mining;

import com.rs2.model.skill.mining.MiningManager;
import java.util.ArrayList;

public enum MineableRockDefinition {
    COPPER(new int[]{2090, 2091, 3042, 9708, 9709, 9710, 11936, 11937, 11938, 11960, 11961, 11962}, 436, 1, 18, 4, 1.0, 128, 400),
    TIN(new int[]{2094, 2095, 3043, 9714, 9715, 9716, 11933, 11934, 11935, 11957, 11958, 11959}, 438, 1, 18, 4, 1.0, 128, 400),
    BLURITE(new int[]{2110, 10583, 10584}, 668, 10, 18, 42, 0.125, 110, 350),
    IRON(new int[]{2092, 2093, 9717, 9718, 9719, 11954, 11955, 11956, 14856, 14857, 14858}, 440, 15, 35, 9, 0.125, 110, 350),
    COAL(new int[]{2096, 2097, 10948, 11930, 11931, 11932, 11963, 11964, 11965, 14850, 14851, 14852}, 453, 30, 50, 50, 0.125, 16, 100),
    GEM_ROCK(new int[]{2111}, -1, 40, 65, 99, 0.125, 28, 70),
    SANDSTONE(new int[]{10946}, -1, 35, 0, 8, 0.125, 16, 100),
    GRANITE(new int[]{10947}, -1, 45, 0, 8, 0.125, 7, 75),
    GOLD(new int[]{2098, 2099, 9720, 9721, 9722, 11183, 11184, 11185, 11951, 11952, 11953}, 444, 40, 65, 100, 0.125, 7, 75),
    SILVER(new int[]{2100, 2101, 11186, 11187, 11188, 11948, 11949, 11950}, 442, 20, 40, 100, 0.125, 25, 200),
    MITHRIL(new int[]{2102, 2103, 11942, 11943, 11944, 11945, 11946, 11947, 14853, 14854, 14855}, 447, 55, 80, 200, 0.125, 4, 50),
    ADAMANTITE(new int[]{2104, 2105, 11939, 11940, 11941, 14862, 14863, 14864}, 449, 70, 95, 400, 0.125, 2, 25),
    RUNITE(new int[]{2106, 2107, 14859, 14860, 14861}, 451, 85, 125, 1200, 0.125, 1, 18),
    CLAY(new int[]{2108, 2109, 9711, 9712, 9713, 10949, 11189, 11190, 11191}, 434, 1, 5, 2, 1.0, 128, 400),
    DEPLETED_ROCK(new int[]{10944, 9723, 9724, 9725, 11555, 11552, 11553, 11554, 11557, 11556, 450, 451, 452, 10587, 10585, 10586, 14832, 14833, 14834, 10945}, 0, 0, 0, 0, 1.0, 0, 0);

    private int[] objectIds;
    private int oreItemId;
    private int requiredLevel;
    private int baseExperience;
    private int respawnTicks;
    private double depletionChance;
    private int mineChanceLow;
    private int mineChanceHigh;

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private MineableRockDefinition(int[] nArray, int n2, int n3, int n4, int n5, double d, int n6, int n7) {
        this.objectIds = nArray;
        this.oreItemId = n2;
        this.requiredLevel = n3;
        this.baseExperience = n4;
        this.respawnTicks = n5;
        this.depletionChance = d;
        this.mineChanceLow = n6;
        this.mineChanceHigh = n7;
    }

    public static MineableRockDefinition forObjectId(int n) {
        MineableRockDefinition[] mineableRockDefinitionArray = MineableRockDefinition.values();
        int n2 = mineableRockDefinitionArray.length;
        int n3 = 0;
        while (n3 < n2) {
            MineableRockDefinition mineableRockDefinition = mineableRockDefinitionArray[n3];
            int[] nArray = mineableRockDefinition.objectIds;
            int n4 = mineableRockDefinition.objectIds.length;
            int n5 = 0;
            while (n5 < n4) {
                int n6 = nArray[n5];
                if (n == n6 || MiningManager.getRestoredRockObjectId(n) == n6) {
                    return mineableRockDefinition;
                }
                ++n5;
            }
            ++n3;
        }
        return null;
    }

    public static int[] collectObjectIds(MineableRockDefinition[] rockDefinitions) {
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        for (MineableRockDefinition rockDefinition : rockDefinitions) {
            int[] nArray = rockDefinition.objectIds;
            int n = nArray.length;
            int n2 = 0;
            while (n2 < n) {
                int n3 = nArray[n2];
                arrayList.add(n3);
                ++n2;
            }
        }
        int[] objectIds = new int[arrayList.size()];
        int n = 0;
        while (n < objectIds.length) {
            objectIds[n] = arrayList.get(n);
            ++n;
        }
        return objectIds;
    }

    static /* synthetic */ int getOreItemId(MineableRockDefinition mineableRockDefinition) {
        return mineableRockDefinition.oreItemId;
    }

    static /* synthetic */ int getMineChanceLow(MineableRockDefinition mineableRockDefinition) {
        return mineableRockDefinition.mineChanceLow;
    }

    static /* synthetic */ int getMineChanceHigh(MineableRockDefinition mineableRockDefinition) {
        return mineableRockDefinition.mineChanceHigh;
    }

    static /* synthetic */ double getDepletionChance(MineableRockDefinition mineableRockDefinition) {
        return mineableRockDefinition.depletionChance;
    }

    static /* synthetic */ int getBaseExperience(MineableRockDefinition mineableRockDefinition) {
        return mineableRockDefinition.baseExperience;
    }

    static /* synthetic */ int getRespawnTicks(MineableRockDefinition mineableRockDefinition) {
        return mineableRockDefinition.respawnTicks;
    }

    static /* synthetic */ int getRequiredLevel(MineableRockDefinition mineableRockDefinition) {
        return mineableRockDefinition.requiredLevel;
    }
}

