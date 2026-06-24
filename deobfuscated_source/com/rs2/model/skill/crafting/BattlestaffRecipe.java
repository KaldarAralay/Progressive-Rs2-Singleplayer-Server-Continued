/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.crafting;

import java.util.HashMap;

public enum BattlestaffRecipe {
    AIR(573, 1397, 66, 137.5),
    WATER(571, 1395, 54, 100.0),
    EARTH(575, 1399, 58, 112.5),
    FIRE(569, 1393, 62, 125.0);

    private int orbItemId;
    private int battlestaffItemId;
    private byte requiredLevel;
    private double experience;
    private static HashMap definitionsByOrbItemId;

    static {
        definitionsByOrbItemId = new HashMap();
        BattlestaffRecipe[] battlestaffRecipeArray = BattlestaffRecipe.values();
        int n = battlestaffRecipeArray.length;
        int n2 = 0;
        while (n2 < n) {
            BattlestaffRecipe battlestaffRecipe;
            BattlestaffRecipe battlestaffRecipe2 = battlestaffRecipe = battlestaffRecipeArray[n2];
            definitionsByOrbItemId.put(battlestaffRecipe2.orbItemId, battlestaffRecipe);
            ++n2;
        }
    }

    public static BattlestaffRecipe forOrbItemId(int n) {
        return (BattlestaffRecipe)((Object)definitionsByOrbItemId.get(n));
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private BattlestaffRecipe(int n2, int n3, int n4, double d) {
        this.orbItemId = n2;
        this.battlestaffItemId = n3;
        this.requiredLevel = (byte)n4;
        this.experience = d;
    }

    public final int getOrbItemId() {
        return this.orbItemId;
    }

    public final int getBattlestaffItemId() {
        return this.battlestaffItemId;
    }

    public final int getRequiredLevel() {
        return this.requiredLevel;
    }

    public final double getExperience() {
        return this.experience;
    }
}

