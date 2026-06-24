/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.travel.canoe;

public enum CanoeTypeDefinition {
    LOG(18204, 12, 30, 1, new int[]{-1, -1}, 1, false),
    DUGOUT(18205, 27, 60, 2, new int[]{18212, 18185}, 2, false),
    STABLE_DUGOUT(18206, 42, 90, 3, new int[]{18215, 18182}, 3, false),
    WAKA(18207, 57, 150, 4, new int[]{18209, 18193}, 4, true);

    int buttonId;
    int requiredLevel;
    int experience;
    int configValue;
    int[] levelInterfaceIds;
    int travelRange;
    boolean canEnterWilderness;

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private CanoeTypeDefinition(int n2, int n3, int n4, int n5, int[] nArray, int n6, boolean bl) {
        this.buttonId = n2;
        this.configValue = n5;
        this.requiredLevel = n3;
        this.experience = n4;
        this.levelInterfaceIds = nArray;
        this.travelRange = n6;
        this.canEnterWilderness = bl;
    }
}

