/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.crafting.armor;

import com.rs2.model.player.Player;
import com.rs2.model.skill.crafting.armor.BlackDragonhideRecipe;
import com.rs2.model.skill.crafting.armor.CraftedArmorAction;

public final class BlackDragonhideCrafting
extends CraftedArmorAction {
    private BlackDragonhideCrafting(Player player, int n, int n2, int n3, int n4, int n5, int n6, double d) {
        super(player, n, n2, n3, n4, n5, n6, d);
    }

    public static BlackDragonhideCrafting createForButton(Player player, int n, int n2) {
        BlackDragonhideRecipe blackDragonhideRecipe = BlackDragonhideRecipe.forButtonId(n);
        if (blackDragonhideRecipe == null || blackDragonhideRecipe.getQuantity() == 0 && n2 == 0) {
            return null;
        }
        return new BlackDragonhideCrafting(player, blackDragonhideRecipe.getMaterialItemId(), blackDragonhideRecipe.getMaterialAmount(), blackDragonhideRecipe.getProductItemId(), blackDragonhideRecipe.getQuantity(), n2, blackDragonhideRecipe.getRequiredLevel(), blackDragonhideRecipe.getExperience());
    }
}

