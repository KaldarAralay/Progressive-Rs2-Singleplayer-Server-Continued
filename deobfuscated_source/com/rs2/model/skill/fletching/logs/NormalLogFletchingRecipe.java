/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.fletching.logs;

import com.rs2.ServerSettings;
import java.util.HashMap;

public enum NormalLogFletchingRecipe {
    a(ServerSettings.cacheVersion < 274 ? 2471 : 8889, 1511, 52, 1, 1, 5.0),
    b(8888, 1511, 52, 5, 1, 5.0),
    c(8887, 1511, 52, 10, 1, 5.0),
    d(8886, 1511, 52, 0, 1, 5.0),
    e(ServerSettings.cacheVersion < 274 ? 2472 : 8893, 1511, 50, 1, 5, 5.0),
    f(8892, 1511, 50, 5, 5, 5.0),
    g(8891, 1511, 50, 10, 5, 5.0),
    h(8890, 1511, 50, 0, 5, 5.0),
    i(ServerSettings.cacheVersion < 274 ? 2473 : 8897, 1511, 48, 1, 10, 10.0),
    j(8896, 1511, 48, 5, 10, 10.0),
    k(8895, 1511, 48, 10, 10, 10.0),
    l(8894, 1511, 48, 0, 10, 10.0);

    private int buttonId;
    private int logItemId;
    private int productItemId;
    private int menuQuantity;
    private int requiredLevel;
    private double experience;
    private static HashMap recipesByButtonId;

    static {
        recipesByButtonId = new HashMap();
        NormalLogFletchingRecipe[] normalLogFletchingRecipeArray = NormalLogFletchingRecipe.values();
        int n = normalLogFletchingRecipeArray.length;
        int n2 = 0;
        while (n2 < n) {
            NormalLogFletchingRecipe normalLogFletchingRecipe = normalLogFletchingRecipeArray[n2];
            recipesByButtonId.put(normalLogFletchingRecipe.buttonId, normalLogFletchingRecipe);
            ++n2;
        }
    }

    public static NormalLogFletchingRecipe forButtonId(int n) {
        return (NormalLogFletchingRecipe)((Object)recipesByButtonId.get(n));
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private NormalLogFletchingRecipe(int n2, int n3, int n4, int n5, int n6, double d) {
        this.buttonId = n2;
        this.logItemId = 1511;
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

