/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.crafting.armor;

import com.rs2.model.player.Player;
import com.rs2.model.skill.crafting.armor.CraftedArmorAction;
import com.rs2.model.skill.crafting.armor.SnakeskinArmorRecipe;

public final class SnakeskinArmorCrafting
extends CraftedArmorAction {
    private SnakeskinArmorCrafting(Player player, int n, int n2, int n3, int n4, int n5, int n6, double d) {
        super(player, n, n2, n3, n4, n5, n6, d);
    }

    public static SnakeskinArmorCrafting createForButton(Player player, int n, int n2) {
        SnakeskinArmorRecipe snakeskinArmorRecipe = SnakeskinArmorRecipe.forButtonId(n);
        if (snakeskinArmorRecipe == null || snakeskinArmorRecipe.getQuantity() == 0 && n2 == 0) {
            return null;
        }
        return new SnakeskinArmorCrafting(player, snakeskinArmorRecipe.getMaterialItemId(), snakeskinArmorRecipe.getMaterialAmount(), snakeskinArmorRecipe.getProductItemId(), snakeskinArmorRecipe.getQuantity(), n2, snakeskinArmorRecipe.getRequiredLevel(), snakeskinArmorRecipe.getExperience());
    }
}

