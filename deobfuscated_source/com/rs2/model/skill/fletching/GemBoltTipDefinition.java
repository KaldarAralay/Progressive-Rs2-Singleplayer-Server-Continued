/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.fletching;

import java.util.HashMap;

public enum GemBoltTipDefinition {
    a(411, 46, 6, 41, 3.0),
    b(413, 46, 24, 41, 9.0),
    c(1609, 45, 12, 11, 1.5);

    private int gemItemId;
    private int boltTipItemId;
    private int boltTipAmount;
    private int requiredLevel;
    private double experience;

    static {
        new HashMap();
    }

    public static GemBoltTipDefinition forGemItemId(int n) {
        GemBoltTipDefinition[] gemBoltTipDefinitionArray = GemBoltTipDefinition.values();
        int n2 = gemBoltTipDefinitionArray.length;
        int n3 = 0;
        while (n3 < n2) {
            GemBoltTipDefinition gemBoltTipDefinition = gemBoltTipDefinitionArray[n3];
            if (gemBoltTipDefinition.gemItemId == n) {
                return gemBoltTipDefinition;
            }
            ++n3;
        }
        return null;
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private GemBoltTipDefinition(int n2, int n3, int n4, int n5, double d) {
        this.gemItemId = n2;
        this.boltTipItemId = n3;
        this.boltTipAmount = n4;
        this.requiredLevel = n5;
        this.experience = d;
    }

    public final int getGemItemId() {
        return this.gemItemId;
    }

    public final int getBoltTipItemId() {
        return this.boltTipItemId;
    }

    public final int getBoltTipAmount() {
        return this.boltTipAmount;
    }

    public final int getRequiredLevel() {
        return this.requiredLevel;
    }

    public final double getExperience() {
        return this.experience;
    }
}

