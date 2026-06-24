/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.crafting.armor;

import com.rs2.model.player.Player;
import com.rs2.model.skill.crafting.armor.SplitbarkCraftingAction;
import com.rs2.model.skill.crafting.armor.SplitbarkRecipe;

public final class SplitbarkCrafting
extends SplitbarkCraftingAction {
    private SplitbarkCrafting(Player player, int n, int n2, int n3, int n4, int n5, int n6) {
        super(player, n, n2, n3, n4, n5, n6);
    }

    public static SplitbarkCrafting createForButton(Player player, int n, int n2) {
        SplitbarkRecipe splitbarkRecipe = SplitbarkRecipe.forButtonId(n);
        if (splitbarkRecipe == null || splitbarkRecipe.getQuantity() == 0 && n2 == 0) {
            return null;
        }
        return new SplitbarkCrafting(player, splitbarkRecipe.getProductItemId(), splitbarkRecipe.getQuantity(), n2, splitbarkRecipe.getMaterialAmount(), splitbarkRecipe.getMaterialAmount(), splitbarkRecipe.getCoinAmount());
    }
}

