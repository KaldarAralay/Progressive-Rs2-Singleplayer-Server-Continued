/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.travel.canoe;

public enum CanoeTreeDefinition {
    LUMBRIDGE_TREE(new int[]{12163}, 12, 0),
    CHAMPIONS_GUILD_TREE(new int[]{12164}, 12, 8),
    BARBARIAN_VILLAGE_TREE(new int[]{12165}, 12, 16),
    EDGEVILLE_TREE(new int[]{12166}, 12, 24);

    private int[] objectIds;
    private int requiredLevel;
    private int configShift;

    public static CanoeTreeDefinition forObjectId(int n) {
        CanoeTreeDefinition[] canoeTreeDefinitionArray = CanoeTreeDefinition.values();
        int n2 = canoeTreeDefinitionArray.length;
        int n3 = 0;
        while (n3 < n2) {
            CanoeTreeDefinition canoeTreeDefinition;
            CanoeTreeDefinition canoeTreeDefinition2 = canoeTreeDefinition = canoeTreeDefinitionArray[n3];
            int[] nArray = canoeTreeDefinition.objectIds;
            int n4 = canoeTreeDefinition.objectIds.length;
            int n5 = 0;
            while (n5 < n4) {
                int n6 = nArray[n5];
                if (n6 == n) {
                    return canoeTreeDefinition;
                }
                ++n5;
            }
            ++n3;
        }
        return null;
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private CanoeTreeDefinition(int[] nArray, int n2, int n3) {
        this.objectIds = nArray;
        this.requiredLevel = 12;
        this.configShift = n3;
    }

    public final int getRequiredLevel() {
        return this.requiredLevel;
    }

    public final int getConfigShift() {
        return this.configShift;
    }
}

