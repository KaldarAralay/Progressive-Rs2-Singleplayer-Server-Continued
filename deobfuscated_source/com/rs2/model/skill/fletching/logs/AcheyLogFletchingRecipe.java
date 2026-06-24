/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.fletching.logs;

import java.util.HashMap;

public enum AcheyLogFletchingRecipe {
    a(2799, 2862, 4825, 1, 30, 45.0),
    b(2798, 2862, 4825, 5, 30, 45.0),
    c(1747, 2862, 4825, 28, 30, 45.0),
    d(1748, 2862, 4825, 0, 30, 45.0);

    private int buttonId;
    private int logItemId;
    private int productItemId;
    private int menuQuantity;
    private int requiredLevel;
    private double experience;
    private static HashMap recipesByButtonId;

    static {
        recipesByButtonId = new HashMap();
        AcheyLogFletchingRecipe[] acheyLogFletchingRecipeArray = AcheyLogFletchingRecipe.values();
        int n = acheyLogFletchingRecipeArray.length;
        int n2 = 0;
        while (n2 < n) {
            AcheyLogFletchingRecipe acheyLogFletchingRecipe = acheyLogFletchingRecipeArray[n2];
            recipesByButtonId.put(acheyLogFletchingRecipe.buttonId, acheyLogFletchingRecipe);
            ++n2;
        }
    }

    public static AcheyLogFletchingRecipe forButtonId(int n) {
        return (AcheyLogFletchingRecipe)((Object)recipesByButtonId.get(n));
    }

    private AcheyLogFletchingRecipe(int buttonId, int logItemId, int productItemId, int menuQuantity, int requiredLevel, double experience) {
        this.buttonId = buttonId;
        this.logItemId = logItemId;
        this.productItemId = productItemId;
        this.menuQuantity = menuQuantity;
        this.requiredLevel = requiredLevel;
        this.experience = experience;
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

