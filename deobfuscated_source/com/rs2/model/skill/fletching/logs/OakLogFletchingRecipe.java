/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.fletching.logs;

import com.rs2.ServerSettings;
import java.util.HashMap;

public enum OakLogFletchingRecipe {
    a(ServerSettings.cacheVersion < 274 ? 2461 : 8874, 1521, 54, 1, 20, 16.5),
    b(8873, 1521, 54, 5, 20, 16.5),
    c(8872, 1521, 54, 10, 20, 16.5),
    d(8871, 1521, 54, 0, 20, 16.5),
    e(ServerSettings.cacheVersion < 274 ? 2462 : 8878, 1521, 56, 1, 25, 25.0),
    f(8877, 1521, 56, 5, 25, 25.0),
    g(8876, 1521, 56, 10, 25, 25.0),
    h(8875, 1521, 56, 0, 25, 25.0);

    private int buttonId;
    private int logItemId;
    private int productItemId;
    private int menuQuantity;
    private int requiredLevel;
    private double experience;
    private static HashMap recipesByButtonId;

    static {
        recipesByButtonId = new HashMap();
        OakLogFletchingRecipe[] oakLogFletchingRecipeArray = OakLogFletchingRecipe.values();
        int n = oakLogFletchingRecipeArray.length;
        int n2 = 0;
        while (n2 < n) {
            OakLogFletchingRecipe oakLogFletchingRecipe = oakLogFletchingRecipeArray[n2];
            recipesByButtonId.put(oakLogFletchingRecipe.buttonId, oakLogFletchingRecipe);
            ++n2;
        }
    }

    public static OakLogFletchingRecipe forButtonId(int n) {
        return (OakLogFletchingRecipe)((Object)recipesByButtonId.get(n));
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private OakLogFletchingRecipe(int n2, int n3, int n4, int n5, int n6, double d) {
        this.buttonId = n2;
        this.logItemId = 1521;
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

