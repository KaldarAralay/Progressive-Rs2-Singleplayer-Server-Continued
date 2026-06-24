/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.crafting.armor;

import com.rs2.model.player.Player;
import com.rs2.model.skill.crafting.armor.BlueDragonhideRecipe;
import com.rs2.model.skill.crafting.armor.CraftedArmorAction;

public final class BlueDragonhideCrafting
extends CraftedArmorAction {
    private BlueDragonhideCrafting(Player player, int n, int n2, int n3, int n4, int n5, int n6, double d) {
        super(player, n, n2, n3, n4, n5, n6, d);
    }

    public static BlueDragonhideCrafting createForButton(Player player, int n, int n2) {
        BlueDragonhideRecipe blueDragonhideRecipe = BlueDragonhideRecipe.forButtonId(n);
        if (blueDragonhideRecipe == null || blueDragonhideRecipe.getQuantity() == 0 && n2 == 0) {
            return null;
        }
        return new BlueDragonhideCrafting(player, blueDragonhideRecipe.getMaterialItemId(), blueDragonhideRecipe.getMaterialAmount(), blueDragonhideRecipe.getProductItemId(), blueDragonhideRecipe.getQuantity(), n2, blueDragonhideRecipe.getRequiredLevel(), blueDragonhideRecipe.getExperience());
    }
}

