/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.crafting;

import com.rs2.model.item.ItemDefinition;
import java.util.HashMap;

public enum SpinningRecipe {
    WOOL(ItemDefinition.isDefined(6051) ? 8889 : 8874, 1737, 1759, 1, 1, 2.5),
    WOOL5(ItemDefinition.isDefined(6051) ? 8888 : 8873, 1737, 1759, 5, 1, 2.5),
    WOOL10(ItemDefinition.isDefined(6051) ? 8887 : 8872, 1737, 1759, 10, 1, 2.5),
    WOOLX(ItemDefinition.isDefined(6051) ? 8886 : 8871, 1737, 1759, 0, 1, 2.5),
    FLAX(ItemDefinition.isDefined(6051) ? 8893 : 8878, 1779, 1777, 1, 10, 15.0),
    FLAX5(ItemDefinition.isDefined(6051) ? 8892 : 8877, 1779, 1777, 5, 10, 15.0),
    FLAX10(ItemDefinition.isDefined(6051) ? 8891 : 8876, 1779, 1777, 10, 10, 15.0),
    FLAXX(ItemDefinition.isDefined(6051) ? 8890 : 8875, 1779, 1777, 0, 10, 15.0),
    ROOT(8897, 6051, 6038, 1, 19, 30.0),
    ROOT5(8896, 6051, 6038, 5, 19, 30.0),
    ROOT10(8895, 6051, 6038, 10, 19, 30.0),
    ROOTX(8894, 6051, 6038, 0, 19, 30.0);

    private int buttonId;
    private int ingredientItemId;
    private int productItemId;
    private int quantity;
    private int requiredLevel;
    private double experience;
    private static HashMap definitionsByButtonId;

    static {
        definitionsByButtonId = new HashMap();
        SpinningRecipe[] spinningRecipeArray = SpinningRecipe.values();
        int n = spinningRecipeArray.length;
        int n2 = 0;
        while (n2 < n) {
            SpinningRecipe spinningRecipe = spinningRecipeArray[n2];
            definitionsByButtonId.put(spinningRecipe.buttonId, spinningRecipe);
            ++n2;
        }
    }

    public static SpinningRecipe forButtonId(int n) {
        return (SpinningRecipe)((Object)definitionsByButtonId.get(n));
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private SpinningRecipe(int n2, int n3, int n4, int n5, int n6, double d) {
        this.buttonId = n2;
        this.ingredientItemId = n3;
        this.productItemId = n4;
        this.quantity = n5;
        this.requiredLevel = n6;
        this.experience = d;
    }

    public final int getIngredientItemId() {
        return this.ingredientItemId;
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

