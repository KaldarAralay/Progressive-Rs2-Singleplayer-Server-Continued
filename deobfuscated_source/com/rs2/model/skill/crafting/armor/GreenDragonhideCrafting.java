/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.crafting.armor;

import com.rs2.model.player.Player;
import com.rs2.model.skill.crafting.armor.CraftedArmorAction;
import com.rs2.model.skill.crafting.armor.GreenDragonhideRecipe;

public final class GreenDragonhideCrafting
extends CraftedArmorAction {
    private GreenDragonhideCrafting(Player player, int n, int n2, int n3, int n4, int n5, int n6, double d) {
        super(player, n, n2, n3, n4, n5, n6, d);
    }

    public static GreenDragonhideCrafting createForButton(Player player, int n, int n2) {
        GreenDragonhideRecipe greenDragonhideRecipe = GreenDragonhideRecipe.forButtonId(n);
        if (greenDragonhideRecipe == null || greenDragonhideRecipe.getQuantity() == 0 && n2 == 0) {
            return null;
        }
        return new GreenDragonhideCrafting(player, greenDragonhideRecipe.getMaterialItemId(), greenDragonhideRecipe.getMaterialAmount(), greenDragonhideRecipe.getProductItemId(), greenDragonhideRecipe.getQuantity(), n2, greenDragonhideRecipe.getRequiredLevel(), greenDragonhideRecipe.getExperience());
    }
}

