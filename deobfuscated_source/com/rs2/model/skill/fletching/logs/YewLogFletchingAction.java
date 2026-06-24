/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.fletching.logs;

import com.rs2.model.player.Player;
import com.rs2.model.skill.fletching.logs.LogFletchingAction;
import com.rs2.model.skill.fletching.logs.YewLogFletchingRecipe;

public final class YewLogFletchingAction
extends LogFletchingAction {
    private YewLogFletchingAction(Player player, int n, int n2, int n3, double d, int n4, int n5) {
        super(player, n, n2, n3, d, n4, n5);
    }

    public static YewLogFletchingAction create(Player player, int n, int n2) {
        YewLogFletchingRecipe yewLogFletchingRecipe = YewLogFletchingRecipe.forButtonId(n);
        if (yewLogFletchingRecipe == null || yewLogFletchingRecipe.getMenuQuantity() == 0 && n2 == 0) {
            return null;
        }
        return new YewLogFletchingAction(player, yewLogFletchingRecipe.getLogItemId(), yewLogFletchingRecipe.getProductItemId(), yewLogFletchingRecipe.getRequiredLevel(), yewLogFletchingRecipe.getExperience(), yewLogFletchingRecipe.getMenuQuantity(), n2);
    }
}

