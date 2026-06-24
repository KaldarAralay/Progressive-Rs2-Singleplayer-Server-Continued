/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.crafting.armor;

import com.rs2.ServerSettings;
import java.util.HashMap;

public enum RedDragonhideRecipe {
    VAMB(ServerSettings.cacheVersion < 274 ? 2471 : 8889, 2507, 1, 2489, 1, 73, 86.0),
    VAMB5(8888, 2507, 1, 2489, 5, 73, 86.0),
    VAMB10(8887, 2507, 1, 2489, 10, 73, 86.0),
    VAMBX(8886, 2507, 1, 2489, 0, 73, 86.0),
    CHAPS(ServerSettings.cacheVersion < 274 ? 2472 : 8893, 2507, 2, 2495, 1, 75, 172.0),
    CHAPS5(8892, 2507, 2, 2495, 5, 75, 172.0),
    CHAPS10(8891, 2507, 2, 2495, 10, 75, 172.0),
    CHAPSX(8890, 2507, 2, 2495, 0, 75, 172.0),
    BODY(ServerSettings.cacheVersion < 274 ? 2473 : 8897, 2507, 3, 2501, 1, 77, 258.0),
    BODY5(8896, 2507, 3, 2501, 5, 77, 258.0),
    BODY10(8895, 2507, 3, 2501, 10, 77, 258.0),
    BODYX(8894, 2507, 3, 2501, 0, 77, 258.0);

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
        RedDragonhideRecipe[] redDragonhideRecipeArray = RedDragonhideRecipe.values();
        int n = redDragonhideRecipeArray.length;
        int n2 = 0;
        while (n2 < n) {
            RedDragonhideRecipe redDragonhideRecipe = redDragonhideRecipeArray[n2];
            definitionsByButtonId.put(redDragonhideRecipe.buttonId, redDragonhideRecipe);
            ++n2;
        }
    }

    public static RedDragonhideRecipe forButtonId(int n) {
        if (definitionsByButtonId == null) {
            return null;
        }
        return (RedDragonhideRecipe)((Object)definitionsByButtonId.get(n));
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private RedDragonhideRecipe(int n2, int n3, int n4, int n5, int n6, int n7, double d) {
        this.buttonId = n2;
        this.materialItemId = 2507;
        this.materialAmount = n4;
        this.productItemId = n5;
        this.quantity = n6;
        this.requiredLevel = n7;
        this.experience = d;
    }

    public final int getButtonId() {
        return this.buttonId;
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

