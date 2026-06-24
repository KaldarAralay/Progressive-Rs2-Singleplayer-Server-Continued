/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.crafting.armor;

import java.util.HashMap;

public enum SnakeskinArmorRecipe {
    VAMB(8889, 6289, 8, 6330, 1, 8, 47, 35.0),
    VAMB5(8888, 6289, 8, 6330, 5, 8, 47, 35.0),
    VAMB10(8887, 6289, 8, 6330, 10, 8, 47, 35.0),
    VAMBX(8886, 6289, 8, 6330, 0, 8, 47, 35.0),
    CHAPS(8893, 6289, 12, 6324, 1, 12, 51, 50.0),
    CHAPS5(8892, 6289, 12, 6324, 5, 12, 51, 50.0),
    CHAPS10(8891, 6289, 12, 6324, 10, 12, 51, 50.0),
    CHAPSX(8890, 6289, 12, 6324, 0, 12, 51, 50.0),
    BODY(8897, 6289, 15, 6322, 1, 15, 53, 55.0),
    BODY5(8896, 6289, 15, 6322, 5, 15, 53, 55.0),
    BODY10(8895, 6289, 15, 6322, 10, 15, 53, 55.0),
    BODYX(8894, 6289, 15, 6322, 0, 15, 53, 55.0);

    private int buttonId;
    private int materialItemId;
    private int materialAmount;
    private int productItemId;
    private int quantity;
    private int requiredLevel;
    private double experience;
    private static HashMap definitionsByButtonId;

    static {
        definitionsByButtonId = new HashMap();
        SnakeskinArmorRecipe[] snakeskinArmorRecipeArray = SnakeskinArmorRecipe.values();
        int n = snakeskinArmorRecipeArray.length;
        int n2 = 0;
        while (n2 < n) {
            SnakeskinArmorRecipe snakeskinArmorRecipe = snakeskinArmorRecipeArray[n2];
            definitionsByButtonId.put(snakeskinArmorRecipe.buttonId, snakeskinArmorRecipe);
            ++n2;
        }
    }

    public static SnakeskinArmorRecipe forButtonId(int n) {
        if (definitionsByButtonId == null) {
            return null;
        }
        return (SnakeskinArmorRecipe)((Object)definitionsByButtonId.get(n));
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private SnakeskinArmorRecipe(int n2, int n3, int n4, int n5, int n6, int n7, int n8, double d) {
        this.buttonId = n2;
        this.materialItemId = 6289;
        this.materialAmount = n4;
        this.productItemId = n5;
        this.quantity = n6;
        this.requiredLevel = n8;
        this.experience = d;
    }

    public final int getMaterialItemId() {
        return this.materialItemId;
    }

    public final int getMaterialAmount() {
        return this.materialAmount;
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

