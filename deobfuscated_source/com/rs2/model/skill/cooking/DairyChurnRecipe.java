/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.cooking;

import java.util.HashMap;

public enum DairyChurnRecipe {
    CREAM(15342, new int[]{1927}, 2130, 21, 18.0),
    BUTTER(15343, new int[]{1927, 2130}, 6697, 38, 40.0),
    CHEESE(15344, new int[]{1927, 2130, 6697}, 1985, 48, 64.0);

    private int buttonId;
    private int[] ingredientItemIds;
    private int productItemId;
    private int requiredLevel;
    private double experience;
    private static HashMap recipesByButtonId;

    static {
        recipesByButtonId = new HashMap();
        DairyChurnRecipe[] dairyChurnRecipeArray = DairyChurnRecipe.values();
        int n = dairyChurnRecipeArray.length;
        int n2 = 0;
        while (n2 < n) {
            DairyChurnRecipe dairyChurnRecipe = dairyChurnRecipeArray[n2];
            recipesByButtonId.put(dairyChurnRecipe.buttonId, dairyChurnRecipe);
            ++n2;
        }
    }

    public static DairyChurnRecipe forButtonId(int n) {
        return (DairyChurnRecipe)((Object)recipesByButtonId.get(n));
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private DairyChurnRecipe(int n2, int[] nArray, int n3, int n4, double d) {
        this.buttonId = n2;
        this.ingredientItemIds = nArray;
        this.productItemId = n3;
        this.requiredLevel = n4;
        this.experience = d;
    }

    public final int[] getIngredientItemIds() {
        return this.ingredientItemIds;
    }

    public final int getProductItemId() {
        return this.productItemId;
    }

    public final int getRequiredLevel() {
        return this.requiredLevel;
    }

    public final double getExperience() {
        return this.experience;
    }
}

