/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.fishing;

public enum FishingWhirlpool {
    RIVER_WHIRLPOOL(403, new int[]{309}),
    NET_BAIT_WHIRLPOOL(404, new int[]{316}),
    CAGE_HARPOON_WHIRLPOOL(405, new int[]{312}),
    BIG_NET_HARPOON_WHIRLPOOL(406, new int[]{313}),
    MONKFISH_WHIRLPOOL(3849, new int[]{3848});

    private int whirlpoolNpcId;
    private int[] sourceNpcIds;

    /*
     * WARNING - void declaration
     */
    private FishingWhirlpool(int n2, int[] nArray) {
        this.whirlpoolNpcId = n2;
        this.sourceNpcIds = nArray;
    }

    public final int[] getSourceNpcIds() {
        return this.sourceNpcIds;
    }

    public final int getWhirlpoolNpcId() {
        return this.whirlpoolNpcId;
    }

    public static FishingWhirlpool forSourceNpcId(int n) {
        FishingWhirlpool[] fishingWhirlpoolArray = FishingWhirlpool.values();
        int n2 = fishingWhirlpoolArray.length;
        int n3 = 0;
        while (n3 < n2) {
            FishingWhirlpool fishingWhirlpool;
            FishingWhirlpool fishingWhirlpool2 = fishingWhirlpool = fishingWhirlpoolArray[n3];
            int[] nArray = fishingWhirlpool.sourceNpcIds;
            int n4 = fishingWhirlpool.sourceNpcIds.length;
            int n5 = 0;
            while (n5 < n4) {
                int n6 = nArray[n5];
                if (n6 == n) {
                    return fishingWhirlpool;
                }
                ++n5;
            }
            ++n3;
        }
        return null;
    }

    public static FishingWhirlpool forWhirlpoolNpcId(int n) {
        FishingWhirlpool[] fishingWhirlpoolArray = FishingWhirlpool.values();
        int n2 = fishingWhirlpoolArray.length;
        int n3 = 0;
        while (n3 < n2) {
            FishingWhirlpool fishingWhirlpool;
            FishingWhirlpool fishingWhirlpool2 = fishingWhirlpool = fishingWhirlpoolArray[n3];
            if (fishingWhirlpool.whirlpoolNpcId == n) {
                return fishingWhirlpool;
            }
            ++n3;
        }
        return null;
    }
}

