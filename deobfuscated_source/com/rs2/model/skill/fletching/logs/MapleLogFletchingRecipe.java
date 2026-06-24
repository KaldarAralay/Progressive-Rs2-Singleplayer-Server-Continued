/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.fletching.logs;

import com.rs2.ServerSettings;
import java.util.HashMap;

public enum MapleLogFletchingRecipe {
    a(ServerSettings.cacheVersion < 274 ? 2461 : 8874, 1517, 64, 1, 50, 50.0),
    b(8873, 1517, 64, 5, 50, 50.0),
    c(8872, 1517, 64, 10, 50, 50.0),
    d(8871, 1517, 64, 0, 50, 50.0),
    e(ServerSettings.cacheVersion < 274 ? 2462 : 8878, 1517, 62, 1, 55, 58.3),
    f(8877, 1517, 62, 5, 55, 58.3),
    g(8876, 1517, 62, 10, 55, 58.3),
    h(8875, 1517, 62, 0, 55, 58.3);

    private int buttonId;
    private int logItemId;
    private int productItemId;
    private int menuQuantity;
    private int requiredLevel;
    private double experience;
    private static HashMap recipesByButtonId;

    static {
        recipesByButtonId = new HashMap();
        MapleLogFletchingRecipe[] mapleLogFletchingRecipeArray = MapleLogFletchingRecipe.values();
        int n = mapleLogFletchingRecipeArray.length;
        int n2 = 0;
        while (n2 < n) {
            MapleLogFletchingRecipe mapleLogFletchingRecipe = mapleLogFletchingRecipeArray[n2];
            recipesByButtonId.put(mapleLogFletchingRecipe.buttonId, mapleLogFletchingRecipe);
            ++n2;
        }
    }

    public static MapleLogFletchingRecipe forButtonId(int n) {
        return (MapleLogFletchingRecipe)((Object)recipesByButtonId.get(n));
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private MapleLogFletchingRecipe(int n2, int n3, int n4, int n5, int n6, double d) {
        this.buttonId = n2;
        this.logItemId = 1517;
        this.productItemId = n4;
        this.menuQuantity = n5;
        this.requiredLevel = n6;
        this.experience = d;
    }

    public final int getLogItemId() {
        return this.logItemId;
    }

    public final int getProductItemId() {
        return this.productItemId;
    }

    public final int getMenuQuantity() {
        return this.menuQuantity;
    }

    public final int getRequiredLevel() {
        return this.requiredLevel;
    }

    public final double getExperience() {
        return this.experience;
    }
}

