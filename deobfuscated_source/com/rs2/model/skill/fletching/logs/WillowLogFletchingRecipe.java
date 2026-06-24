/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.fletching.logs;

import com.rs2.ServerSettings;
import java.util.HashMap;

public enum WillowLogFletchingRecipe {
    a(ServerSettings.cacheVersion < 274 ? 2461 : 8874, 1519, 60, 1, 35, 33.3),
    b(8873, 1519, 60, 5, 35, 33.3),
    c(8872, 1519, 60, 10, 35, 33.3),
    d(8871, 1519, 60, 0, 35, 33.3),
    e(ServerSettings.cacheVersion < 274 ? 2462 : 8878, 1519, 58, 1, 40, 41.5),
    f(8877, 1519, 58, 5, 40, 41.5),
    g(8876, 1519, 58, 10, 40, 41.5),
    h(8875, 1519, 58, 0, 40, 41.5);

    private int buttonId;
    private int logItemId;
    private int productItemId;
    private int menuQuantity;
    private int requiredLevel;
    private double experience;
    private static HashMap recipesByButtonId;

    static {
        recipesByButtonId = new HashMap();
        WillowLogFletchingRecipe[] willowLogFletchingRecipeArray = WillowLogFletchingRecipe.values();
        int n = willowLogFletchingRecipeArray.length;
        int n2 = 0;
        while (n2 < n) {
            WillowLogFletchingRecipe willowLogFletchingRecipe = willowLogFletchingRecipeArray[n2];
            recipesByButtonId.put(willowLogFletchingRecipe.buttonId, willowLogFletchingRecipe);
            ++n2;
        }
    }

    public static WillowLogFletchingRecipe forButtonId(int n) {
        return (WillowLogFletchingRecipe)((Object)recipesByButtonId.get(n));
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private WillowLogFletchingRecipe(int n2, int n3, int n4, int n5, int n6, double d) {
        this.buttonId = n2;
        this.logItemId = 1519;
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

