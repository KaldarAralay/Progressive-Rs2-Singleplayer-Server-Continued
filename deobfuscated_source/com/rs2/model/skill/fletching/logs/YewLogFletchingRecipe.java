/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.fletching.logs;

import com.rs2.ServerSettings;
import java.util.HashMap;

public enum YewLogFletchingRecipe {
    a(ServerSettings.cacheVersion < 274 ? 2461 : 8874, 1515, 68, 1, 65, 68.5),
    b(8873, 1515, 68, 5, 65, 68.5),
    c(8872, 1515, 68, 10, 65, 68.5),
    d(8871, 1515, 68, 0, 65, 68.5),
    e(ServerSettings.cacheVersion < 274 ? 2462 : 8878, 1515, 66, 1, 70, 75.0),
    f(8877, 1515, 66, 5, 70, 75.0),
    g(8876, 1515, 66, 10, 70, 75.0),
    h(8875, 1515, 66, 0, 70, 75.0);

    private int buttonId;
    private int logItemId;
    private int productItemId;
    private int menuQuantity;
    private int requiredLevel;
    private double experience;
    private static HashMap recipesByButtonId;

    static {
        recipesByButtonId = new HashMap();
        YewLogFletchingRecipe[] yewLogFletchingRecipeArray = YewLogFletchingRecipe.values();
        int n = yewLogFletchingRecipeArray.length;
        int n2 = 0;
        while (n2 < n) {
            YewLogFletchingRecipe yewLogFletchingRecipe = yewLogFletchingRecipeArray[n2];
            recipesByButtonId.put(yewLogFletchingRecipe.buttonId, yewLogFletchingRecipe);
            ++n2;
        }
    }

    public static YewLogFletchingRecipe forButtonId(int n) {
        return (YewLogFletchingRecipe)((Object)recipesByButtonId.get(n));
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private YewLogFletchingRecipe(int n2, int n3, int n4, int n5, int n6, double d) {
        this.buttonId = n2;
        this.logItemId = 1515;
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

