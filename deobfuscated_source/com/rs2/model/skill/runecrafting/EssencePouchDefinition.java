/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.runecrafting;

import java.util.HashMap;
import java.util.Map;

public enum EssencePouchDefinition {
    SMALL_POUCH(0, 5509, -1, 3, 1, -1, 0),
    MEDIUM_POUCH(1, 5510, 5511, 6, 25, 270, 3),
    LARGE_POUCH(2, 5512, 5513, 9, 50, 261, 2),
    GIANT_POUCH(3, 5514, 5515, 12, 75, 120, 3);

    private int pouchIndex;
    private int itemId;
    private int degradedItemId;
    private int capacity;
    private int requiredLevel;
    private int degradeAfterUses;
    private int degradedCapacityPenalty;
    private static Map lookupById;

    static {
        lookupById = new HashMap();
        EssencePouchDefinition[] essencePouchDefinitionArray = EssencePouchDefinition.values();
        int n = essencePouchDefinitionArray.length;
        int n2 = 0;
        while (n2 < n) {
            EssencePouchDefinition essencePouchDefinition = essencePouchDefinitionArray[n2];
            lookupById.put(essencePouchDefinition.pouchIndex, essencePouchDefinition);
            lookupById.put(essencePouchDefinition.itemId, essencePouchDefinition);
            lookupById.put(essencePouchDefinition.degradedItemId, essencePouchDefinition);
            ++n2;
        }
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private EssencePouchDefinition(int n2, int n3, int n4, int n5, int n6, int n7, int n8) {
        this.pouchIndex = n2;
        this.itemId = n3;
        this.degradedItemId = n4;
        this.capacity = n5;
        this.requiredLevel = n6;
        this.degradeAfterUses = n7;
        this.degradedCapacityPenalty = n8;
    }

    public static EssencePouchDefinition forItemOrIndex(int n) {
        return (EssencePouchDefinition)((Object)lookupById.get(n));
    }

    public final int getPouchIndex() {
        return this.pouchIndex;
    }

    public final int getItemId() {
        return this.itemId;
    }

    public final int getDegradedItemId() {
        return this.degradedItemId;
    }

    public final int getCapacity() {
        return this.capacity;
    }

    public final int getRequiredLevel() {
        return this.requiredLevel;
    }

    public final int getDegradeAfterUses() {
        return this.degradeAfterUses;
    }

    public final int getDegradedCapacityPenalty() {
        return this.degradedCapacityPenalty;
    }
}

