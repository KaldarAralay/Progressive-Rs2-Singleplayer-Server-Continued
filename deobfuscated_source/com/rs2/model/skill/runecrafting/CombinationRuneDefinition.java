/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.runecrafting;

public enum CombinationRuneDefinition {
    MIST_WITH_AIR_TALISMAN(1438, 556, 2480, 4695, 6, 8.5),
    MIST_WITH_WATER_TALISMAN(1444, 555, 2478, 4695, 6, 8.0),
    DUST_WITH_AIR_TALISMAN(1438, 556, 2481, 4696, 10, 9.0),
    DUST_WITH_EARTH_TALISMAN(1440, 557, 2478, 4696, 10, 8.3),
    MUD_WITH_WATER_TALISMAN(1444, 555, 2481, 4698, 13, 9.5),
    MUD_WITH_EARTH_TALISMAN(1440, 557, 2480, 4698, 13, 9.3),
    SMOKE_WITH_AIR_TALISMAN(1438, 556, 2482, 4697, 15, 9.5),
    SMOKE_WITH_FIRE_TALISMAN(1442, 554, 2478, 4697, 15, 8.5),
    STEAM_WITH_WATER_TALISMAN(1444, 555, 2482, 4694, 19, 10.0),
    STEAM_WITH_FIRE_TALISMAN(1442, 554, 2480, 4694, 19, 9.3),
    LAVA_WITH_EARTH_TALISMAN(1440, 557, 2482, 4699, 23, 10.5),
    LAVA_WITH_FIRE_TALISMAN(1442, 554, 2481, 4699, 23, 10.0);

    private int talismanItemId;
    private int runeItemId;
    private int altarObjectId;
    private int productRuneItemId;
    private int requiredLevel;
    private double experience;

    public static CombinationRuneDefinition forTalismanAndAltarObjectId(int n, int n2) {
        CombinationRuneDefinition[] combinationRuneDefinitionArray = CombinationRuneDefinition.values();
        int n3 = combinationRuneDefinitionArray.length;
        int n4 = 0;
        while (n4 < n3) {
            CombinationRuneDefinition combinationRuneDefinition = combinationRuneDefinitionArray[n4];
            if (n == combinationRuneDefinition.talismanItemId && n2 == combinationRuneDefinition.altarObjectId) {
                return combinationRuneDefinition;
            }
            ++n4;
        }
        return null;
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private CombinationRuneDefinition(int n2, int n3, int n4, int n5, int n6, double d) {
        this.talismanItemId = n2;
        this.runeItemId = n3;
        this.altarObjectId = n4;
        this.productRuneItemId = n5;
        this.requiredLevel = n6;
        this.experience = d;
    }

    public final int getTalismanItemId() {
        return this.talismanItemId;
    }

    public final int getRuneItemId() {
        return this.runeItemId;
    }

    public final int getProductRuneItemId() {
        return this.productRuneItemId;
    }

    public final int getRequiredLevel() {
        return this.requiredLevel;
    }

    public final double getExperience() {
        return this.experience;
    }
}

