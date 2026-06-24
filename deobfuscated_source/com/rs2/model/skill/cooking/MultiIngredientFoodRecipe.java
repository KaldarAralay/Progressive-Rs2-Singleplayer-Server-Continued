/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.cooking;

import java.util.HashMap;

public enum MultiIngredientFoodRecipe {
    a(2142, 1942, 1921, 1999, 2001, 25, 117.0, true, 0, 0),
    b(1942, 2142, 1921, 1997, 2001, 25, 117.0, true, 0, 0),
    c(2140, 1942, 1921, 1999, 2001, 25, 117.0, true, 0, 0),
    d(1942, 2140, 1921, 1997, 2001, 25, 117.0, true, 0, 0),
    e(1982, 1985, 2283, 2285, 2287, 35, 143.0, true, 0, 0),
    f(361, 5988, 1923, 7086, 7068, 67, 204.0, true, 0, 0),
    g(5988, 361, 1923, 7088, 7068, 67, 204.0, true, 0, 0);

    private int firstIngredientItemId;
    private int secondIngredientItemId;
    private int baseItemId;
    private int firstStageProductItemId;
    private int finalProductItemId;
    private int requiredLevel;
    private double experience;
    private boolean putIntoMessage;
    private int firstStageReturnedItemId;
    private int finalStageReturnedItemId;
    private static HashMap recipesByFirstIngredientItemId;
    private static HashMap recipesByFirstStageProductItemId;

    static {
        recipesByFirstIngredientItemId = new HashMap();
        recipesByFirstStageProductItemId = new HashMap();
        MultiIngredientFoodRecipe[] multiIngredientFoodRecipeArray = MultiIngredientFoodRecipe.values();
        int n = multiIngredientFoodRecipeArray.length;
        int n2 = 0;
        while (n2 < n) {
            MultiIngredientFoodRecipe multiIngredientFoodRecipe = multiIngredientFoodRecipeArray[n2];
            recipesByFirstIngredientItemId.put(multiIngredientFoodRecipe.firstIngredientItemId, multiIngredientFoodRecipe);
            recipesByFirstStageProductItemId.put(multiIngredientFoodRecipe.firstStageProductItemId, multiIngredientFoodRecipe);
            ++n2;
        }
    }

    public static MultiIngredientFoodRecipe forFirstIngredientItemId(int n) {
        return (MultiIngredientFoodRecipe)((Object)recipesByFirstIngredientItemId.get(n));
    }

    public static MultiIngredientFoodRecipe forFirstStageProductItemId(int n) {
        return (MultiIngredientFoodRecipe)((Object)recipesByFirstStageProductItemId.get(n));
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private MultiIngredientFoodRecipe(int n2, int n3, int n4, int n5, int n6, int n7, double d, boolean bl, int n8, int n9) {
        this.firstIngredientItemId = n2;
        this.secondIngredientItemId = n3;
        this.baseItemId = n4;
        this.firstStageProductItemId = n5;
        this.finalProductItemId = n6;
        this.requiredLevel = n7;
        this.experience = d;
        this.putIntoMessage = true;
        this.firstStageReturnedItemId = 0;
        this.finalStageReturnedItemId = 0;
    }

    public final int getFirstIngredientItemId() {
        return this.firstIngredientItemId;
    }

    public final int getSecondIngredientItemId() {
        return this.secondIngredientItemId;
    }

    public final int getFirstStageProductItemId() {
        return this.firstStageProductItemId;
    }

    public final int getBaseItemId() {
        return this.baseItemId;
    }

    public final int getFinalProductItemId() {
        return this.finalProductItemId;
    }

    public final int getRequiredLevel() {
        return this.requiredLevel;
    }

    public final double getExperience() {
        return this.experience;
    }

    public final boolean usesPutIntoMessage() {
        return this.putIntoMessage;
    }

    public final int getFirstStageReturnedItemId() {
        return this.firstStageReturnedItemId;
    }

    public final int getFinalStageReturnedItemId() {
        return this.finalStageReturnedItemId;
    }
}

