/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.crafting.armor;

import com.rs2.model.player.Player;
import com.rs2.model.skill.crafting.armor.CraftedArmorAction;
import com.rs2.model.skill.crafting.armor.LeatherRecipe;

public final class LeatherCrafting
extends CraftedArmorAction {
    private LeatherCrafting(Player player, int n, int n2, int n3, int n4, int n5, int n6, double d) {
        super(player, n, n2, n3, n4, n5, n6, d);
    }

    public static LeatherCrafting createForButton(Player player, int n, int n2) {
        LeatherRecipe leatherRecipe = LeatherRecipe.forButtonId(n);
        if (leatherRecipe == null || leatherRecipe.getQuantity() == 0 && n2 == 0) {
            return null;
        }
        return new LeatherCrafting(player, leatherRecipe.getMaterialItemId(), leatherRecipe.getMaterialAmount(), leatherRecipe.getProductItemId(), leatherRecipe.getQuantity(), n2, leatherRecipe.getRequiredLevel(), leatherRecipe.getExperience());
    }
}

