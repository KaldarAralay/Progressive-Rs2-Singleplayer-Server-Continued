/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.crafting;

import com.rs2.cache.InterfaceDefinition;
import com.rs2.model.item.ItemDefinition;
import java.util.HashMap;

public enum PotteryRecipe {
    POT(InterfaceDefinition.interfaceCount <= 8888 ? 2471 : (!ItemDefinition.isDefined(4438) ? 8889 : 8949), 1761, 1787, 1931, 1, 1, 6.3, 6.3),
    POT5(!ItemDefinition.isDefined(4438) ? 8888 : 8948, 1761, 1787, 1931, 5, 1, 6.3, 6.3),
    POT10(!ItemDefinition.isDefined(4438) ? 8887 : 8947, 1761, 1787, 1931, 10, 1, 6.3, 6.3),
    POTX(!ItemDefinition.isDefined(4438) ? 8886 : 8946, 1761, 1787, 1931, 0, 1, 6.3, 6.3),
    DISH(InterfaceDefinition.interfaceCount <= 8888 ? 2472 : (!ItemDefinition.isDefined(4438) ? 8893 : 953), 1761, 1789, 2313, 1, 7, 15.0, 10.0),
    DISH5(!ItemDefinition.isDefined(4438) ? 8892 : 8952, 1761, 1789, 2313, 5, 7, 15.0, 10.0),
    DISH10(!ItemDefinition.isDefined(4438) ? 8891 : 8951, 1761, 1789, 2313, 10, 7, 15.0, 10.0),
    DISHX(!ItemDefinition.isDefined(4438) ? 8890 : 8950, 1761, 1789, 2313, 0, 7, 15.0, 10.0),
    BOWL(InterfaceDefinition.interfaceCount <= 8888 ? 2473 : (!ItemDefinition.isDefined(4438) ? 8897 : 8957), 1761, 1791, 1923, 1, 8, 18.0, 15.0),
    BOWL5(!ItemDefinition.isDefined(4438) ? 8896 : 8956, 1761, 1791, 1923, 5, 8, 18.0, 15.0),
    BOWL10(!ItemDefinition.isDefined(4438) ? 8895 : 8955, 1761, 1791, 1923, 10, 8, 18.0, 15.0),
    BOWLX(!ItemDefinition.isDefined(4438) ? 8894 : 8954, 1761, 1791, 1923, 0, 8, 18.0, 15.0),
    PLANT(8961, 1761, 5352, 5350, 1, 19, 20.0, 17.5),
    PLANT5(8960, 1761, 5352, 5350, 5, 19, 20.0, 17.5),
    PLANT10(8959, 1761, 5352, 5350, 10, 19, 20.0, 17.5),
    PLANTX(8958, 1761, 5352, 5350, 0, 19, 20.0, 17.5),
    LID(8965, 1761, 4438, 4440, 1, 25, 20.0, 20.0),
    LID5(8964, 1761, 4438, 4440, 5, 25, 20.0, 20.0),
    LID10(8963, 1761, 4438, 4440, 10, 25, 20.0, 20.0),
    LIDX(8962, 1761, 4438, 4440, 0, 25, 20.0, 20.0);

    private int buttonId;
    private int softClayItemId;
    private int unfiredItemId;
    private int firedItemId;
    private int quantity;
    private int requiredLevel;
    private double wheelExperience;
    private double firingExperience;
    public static HashMap definitionsByButtonId;

    static {
        definitionsByButtonId = new HashMap();
        PotteryRecipe[] potteryRecipeArray = PotteryRecipe.values();
        int n = potteryRecipeArray.length;
        int n2 = 0;
        while (n2 < n) {
            PotteryRecipe potteryRecipe = potteryRecipeArray[n2];
            definitionsByButtonId.put(potteryRecipe.buttonId, potteryRecipe);
            ++n2;
        }
    }

    public static PotteryRecipe forButtonId(int n) {
        return (PotteryRecipe)((Object)definitionsByButtonId.get(n));
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private PotteryRecipe(int n2, int n3, int n4, int n5, int n6, int n7, double d, double d2) {
        this.buttonId = n2;
        this.softClayItemId = 1761;
        this.unfiredItemId = n4;
        this.firedItemId = n5;
        this.quantity = n6;
        this.requiredLevel = n7;
        this.wheelExperience = d;
        this.firingExperience = d2;
    }

    public final int getButtonId() {
        return this.buttonId;
    }

    public final int getSoftClayItemId() {
        return this.softClayItemId;
    }

    public final int getUnfiredItemId() {
        return this.unfiredItemId;
    }

    public final int getFiredItemId() {
        return this.firedItemId;
    }

    public final int getQuantity() {
        return this.quantity;
    }

    public final int getRequiredLevel() {
        return this.requiredLevel;
    }

    public final double getWheelExperience() {
        return this.wheelExperience;
    }

    public final double getFiringExperience() {
        return this.firingExperience;
    }
}

