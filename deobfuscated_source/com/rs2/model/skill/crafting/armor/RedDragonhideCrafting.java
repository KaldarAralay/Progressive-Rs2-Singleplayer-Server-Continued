/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.crafting.armor;

import com.rs2.model.player.Player;
import com.rs2.model.skill.crafting.armor.CraftedArmorAction;
import com.rs2.model.skill.crafting.armor.RedDragonhideRecipe;

public final class RedDragonhideCrafting
extends CraftedArmorAction {
    private RedDragonhideCrafting(Player player, int n, int n2, int n3, int n4, int n5, int n6, double d) {
        super(player, n, n2, n3, n4, n5, n6, d);
    }

    public static RedDragonhideCrafting createForButton(Player player, int n, int n2) {
        RedDragonhideRecipe redDragonhideRecipe = RedDragonhideRecipe.forButtonId(n);
        if (redDragonhideRecipe == null || redDragonhideRecipe.getQuantity() == 0 && n2 == 0) {
            return null;
        }
        return new RedDragonhideCrafting(player, redDragonhideRecipe.getMaterialItemId(), redDragonhideRecipe.getMaterialAmount(), redDragonhideRecipe.getProductItemId(), redDragonhideRecipe.getQuantity(), n2, redDragonhideRecipe.getRequiredLevel(), redDragonhideRecipe.getExperience());
    }
}

