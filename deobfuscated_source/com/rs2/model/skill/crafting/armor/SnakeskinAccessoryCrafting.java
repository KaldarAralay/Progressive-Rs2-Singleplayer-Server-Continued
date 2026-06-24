/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.crafting.armor;

import com.rs2.model.player.Player;
import com.rs2.model.skill.crafting.armor.CraftedArmorAction;
import com.rs2.model.skill.crafting.armor.SnakeskinAccessoryRecipe;

public final class SnakeskinAccessoryCrafting
extends CraftedArmorAction {
    private SnakeskinAccessoryCrafting(Player player, int n, int n2, int n3, int n4, int n5, int n6, double d) {
        super(player, n, n2, n3, n4, n5, n6, d);
    }

    public static SnakeskinAccessoryCrafting createForButton(Player player, int n, int n2) {
        SnakeskinAccessoryRecipe snakeskinAccessoryRecipe = SnakeskinAccessoryRecipe.forButtonId(n);
        if (snakeskinAccessoryRecipe == null || snakeskinAccessoryRecipe.getQuantity() == 0 && n2 == 0) {
            return null;
        }
        return new SnakeskinAccessoryCrafting(player, snakeskinAccessoryRecipe.getMaterialItemId(), snakeskinAccessoryRecipe.getMaterialAmount(), snakeskinAccessoryRecipe.getProductItemId(), snakeskinAccessoryRecipe.getQuantity(), n2, snakeskinAccessoryRecipe.getRequiredLevel(), snakeskinAccessoryRecipe.getExperience());
    }
}

