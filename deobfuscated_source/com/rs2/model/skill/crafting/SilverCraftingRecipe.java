/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.crafting;

import com.rs2.model.item.ItemDefinition;
import java.util.HashMap;

public enum SilverCraftingRecipe {
    UNSTRUNG(!ItemDefinition.isDefined(5525) ? 8874 : 8889, 2355, 1714, 1, 16, 50.0),
    UNSTRUNG5(!ItemDefinition.isDefined(5525) ? 8873 : 8888, 2355, 1714, 5, 16, 50.0),
    UNSTRUNG10(!ItemDefinition.isDefined(5525) ? 8872 : 8887, 2355, 1714, 10, 16, 50.0),
    UNSTRUNGX(!ItemDefinition.isDefined(5525) ? 8871 : 8886, 2355, 1714, 0, 16, 50.0),
    SICKLE(!ItemDefinition.isDefined(5525) ? 8878 : 8893, 2355, 2961, 1, 18, 50.0),
    SICKLE5(!ItemDefinition.isDefined(5525) ? 8877 : 8892, 2355, 2961, 5, 18, 50.0),
    SICKLE10(!ItemDefinition.isDefined(5525) ? 8876 : 8891, 2355, 2961, 10, 18, 50.0),
    SICKLEX(!ItemDefinition.isDefined(5525) ? 8875 : 8890, 2355, 2961, 0, 18, 50.0),
    TIARA(8897, 2355, 5525, 1, 23, 52.5),
    TIARA5(8896, 2355, 5525, 5, 23, 52.5),
    TIARA10(8895, 2355, 5525, 10, 23, 52.5),
    TIARAX(8894, 2355, 5525, 0, 23, 52.5);

    private int buttonId;
    private int barItemId;
    private int productItemId;
    private int quantity;
    private int requiredLevel;
    private double experience;
    private static HashMap definitionsByButtonId;

    static {
        definitionsByButtonId = new HashMap();
        SilverCraftingRecipe[] silverCraftingRecipeArray = SilverCraftingRecipe.values();
        int n = silverCraftingRecipeArray.length;
        int n2 = 0;
        while (n2 < n) {
            SilverCraftingRecipe silverCraftingRecipe = silverCraftingRecipeArray[n2];
            definitionsByButtonId.put(silverCraftingRecipe.buttonId, silverCraftingRecipe);
            ++n2;
        }
    }

    public static SilverCraftingRecipe forButtonId(int n) {
        return (SilverCraftingRecipe)((Object)definitionsByButtonId.get(n));
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private SilverCraftingRecipe(int n2, int n3, int n4, int n5, int n6, double d) {
        this.buttonId = n2;
        this.barItemId = 2355;
        this.productItemId = n4;
        this.quantity = n5;
        this.requiredLevel = n6;
        this.experience = d;
    }

    public final int getBarItemId() {
        return this.barItemId;
    }

    public final int getProductItemId() {
        return this.productItemId;
    }

    public final int getQuantity() {
        return this.quantity;
    }

    public final int getRequiredLevel() {
        return this.requiredLevel;
    }

    public final double getExperience() {
        return this.experience;
    }
}

