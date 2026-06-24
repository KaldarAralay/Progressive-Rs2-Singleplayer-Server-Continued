/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.crafting.armor;

import com.rs2.model.player.Player;
import com.rs2.model.skill.crafting.armor.CraftedArmorAction;
import com.rs2.model.skill.crafting.armor.HardLeatherRecipe;

public final class HardLeatherCrafting
extends CraftedArmorAction {
    private HardLeatherCrafting(Player player, int n, int n2, int n3, int n4, int n5, int n6, double d) {
        super(player, n, n2, n3, n4, n5, n6, d);
    }

    public static HardLeatherCrafting createForButton(Player player, int n, int n2) {
        HardLeatherRecipe hardLeatherRecipe = HardLeatherRecipe.forButtonId(n);
        if (hardLeatherRecipe == null || hardLeatherRecipe.getQuantity() == 0 && n2 == 0) {
            return null;
        }
        return new HardLeatherCrafting(player, hardLeatherRecipe.getMaterialItemId(), hardLeatherRecipe.getMaterialAmount(), hardLeatherRecipe.getProductItemId(), hardLeatherRecipe.getQuantity(), n2, hardLeatherRecipe.getRequiredLevel(), hardLeatherRecipe.getExperience());
    }
}

