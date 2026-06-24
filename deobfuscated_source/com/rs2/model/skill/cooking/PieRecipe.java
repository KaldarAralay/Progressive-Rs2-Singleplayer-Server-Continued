/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.cooking;

import java.util.HashMap;

public enum PieRecipe {
    a(6032, 2953, 434, 2315, 7164, 7166, 7168, 29, 128.0, true, 3727, 3727, 0),
    b(1982, 1957, 1965, 2315, 7172, 7174, 7176, 34, 138.0, true, 0, 0, 0),
    c(339, 333, 1942, 2315, 7182, 7184, 7186, 47, 164.0, true, 0, 0, 0),
    d(329, 361, 1942, 2315, 7192, 7194, 7196, 70, 210.0, true, 0, 0, 0),
    e(2136, 2876, 3226, 2315, 7202, 7204, 7206, 85, 240.0, true, 0, 0, 0),
    f(5504, 5982, 1955, 2315, 7212, 7214, 7216, 95, 260.0, true, 0, 0, 0);

    private int firstIngredientItemId;
    private int secondIngredientItemId;
    private int thirdIngredientItemId;
    private int pieShellItemId;
    private int firstStagePieItemId;
    private int secondStagePieItemId;
    private int rawPieItemId;
    private int requiredLevel;
    private double experience;
    private boolean putIntoMessage;
    private int firstStageReturnedItemId;
    private int secondStageReturnedItemId;
    private int thirdStageReturnedItemId;
    private static HashMap recipesByFirstIngredientItemId;
    private static HashMap recipesByFirstStagePieItemId;
    private static HashMap recipesBySecondStagePieItemId;

    static {
        recipesByFirstIngredientItemId = new HashMap();
        recipesByFirstStagePieItemId = new HashMap();
        recipesBySecondStagePieItemId = new HashMap();
        PieRecipe[] pieRecipeArray = PieRecipe.values();
        int n = pieRecipeArray.length;
        int n2 = 0;
        while (n2 < n) {
            PieRecipe pieRecipe = pieRecipeArray[n2];
            recipesByFirstIngredientItemId.put(pieRecipe.firstIngredientItemId, pieRecipe);
            recipesByFirstStagePieItemId.put(pieRecipe.firstStagePieItemId, pieRecipe);
            recipesBySecondStagePieItemId.put(pieRecipe.secondStagePieItemId, pieRecipe);
            ++n2;
        }
    }

    public static PieRecipe forFirstIngredientItemId(int n) {
        return (PieRecipe)((Object)recipesByFirstIngredientItemId.get(n));
    }

    public static PieRecipe forFirstStagePieItemId(int n) {
        return (PieRecipe)((Object)recipesByFirstStagePieItemId.get(n));
    }

    public static PieRecipe forSecondStagePieItemId(int n) {
        return (PieRecipe)((Object)recipesBySecondStagePieItemId.get(n));
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private PieRecipe(int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9, double d, boolean bl, int n10, int n11, int n12) {
        this.firstIngredientItemId = n2;
        this.secondIngredientItemId = n3;
        this.thirdIngredientItemId = n4;
        this.pieShellItemId = 2315;
        this.firstStagePieItemId = n6;
        this.secondStagePieItemId = n7;
        this.rawPieItemId = n8;
        this.requiredLevel = n9;
        this.experience = d;
        this.putIntoMessage = true;
        this.firstStageReturnedItemId = n10;
        this.secondStageReturnedItemId = n11;
        this.thirdStageReturnedItemId = 0;
    }

    public final int getFirstIngredientItemId() {
        return this.firstIngredientItemId;
    }

    public final int getSecondIngredientItemId() {
        return this.secondIngredientItemId;
    }

    public final int getThirdIngredientItemId() {
        return this.thirdIngredientItemId;
    }

    public final int getFirstStagePieItemId() {
        return this.firstStagePieItemId;
    }

    public final int getSecondStagePieItemId() {
        return this.secondStagePieItemId;
    }

    public final int getPieShellItemId() {
        return this.pieShellItemId;
    }

    public final int getRawPieItemId() {
        return this.rawPieItemId;
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

    public final int getSecondStageReturnedItemId() {
        return this.secondStageReturnedItemId;
    }

    public final int getThirdStageReturnedItemId() {
        return this.thirdStageReturnedItemId;
    }
}

