/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.fletching.logs;

import com.rs2.model.player.Player;
import com.rs2.model.skill.fletching.logs.LogFletchingAction;
import com.rs2.model.skill.fletching.logs.MagicLogFletchingRecipe;

public final class MagicLogFletchingAction
extends LogFletchingAction {
    private MagicLogFletchingAction(Player player, int n, int n2, int n3, double d, int n4, int n5) {
        super(player, n, n2, n3, d, n4, n5);
    }

    public static MagicLogFletchingAction create(Player player, int n, int n2) {
        MagicLogFletchingRecipe magicLogFletchingRecipe = MagicLogFletchingRecipe.forButtonId(n);
        if (magicLogFletchingRecipe == null || magicLogFletchingRecipe.getMenuQuantity() == 0 && n2 == 0) {
            return null;
        }
        return new MagicLogFletchingAction(player, magicLogFletchingRecipe.getLogItemId(), magicLogFletchingRecipe.getProductItemId(), magicLogFletchingRecipe.getRequiredLevel(), magicLogFletchingRecipe.getExperience(), magicLogFletchingRecipe.getMenuQuantity(), n2);
    }
}

