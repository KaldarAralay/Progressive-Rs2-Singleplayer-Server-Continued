/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.crafting.armor;

import com.rs2.ServerSettings;
import java.util.HashMap;

public enum BlackDragonhideRecipe {
    VAMB(ServerSettings.cacheVersion < 274 ? 2471 : 8889, 2509, 1, 2491, 1, 79, 86.0),
    VAMB5(8888, 2509, 1, 2491, 5, 79, 86.0),
    VAMB10(8887, 2509, 1, 2491, 10, 79, 86.0),
    VAMBX(8886, 2509, 1, 2491, 0, 79, 86.0),
    CHAPS(ServerSettings.cacheVersion < 274 ? 2472 : 8893, 2509, 2, 2497, 1, 82, 172.0),
    CHAPS5(8892, 2509, 2, 2497, 5, 82, 172.0),
    CHAPS10(8891, 2509, 2, 2497, 10, 82, 172.0),
    CHAPSX(8890, 2509, 3, 2497, 0, 82, 172.0),
    BODY(ServerSettings.cacheVersion < 274 ? 2473 : 8897, 2509, 3, 2503, 1, 84, 258.0),
    BODY5(8896, 2509, 3, 2503, 5, 84, 258.0),
    BODY10(8895, 2509, 3, 2503, 10, 84, 258.0),
    BODYX(8894, 2509, 3, 2503, 0, 84, 258.0);

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
        BlackDragonhideRecipe[] blackDragonhideRecipeArray = BlackDragonhideRecipe.values();
        int n = blackDragonhideRecipeArray.length;
        int n2 = 0;
        while (n2 < n) {
            BlackDragonhideRecipe blackDragonhideRecipe = blackDragonhideRecipeArray[n2];
            definitionsByButtonId.put(blackDragonhideRecipe.buttonId, blackDragonhideRecipe);
            ++n2;
        }
    }

    public static BlackDragonhideRecipe forButtonId(int n) {
        if (definitionsByButtonId == null) {
            return null;
        }
        return (BlackDragonhideRecipe)((Object)definitionsByButtonId.get(n));
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private BlackDragonhideRecipe(int n2, int n3, int n4, int n5, int n6, int n7, double d) {
        this.buttonId = n2;
        this.materialItemId = 2509;
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

