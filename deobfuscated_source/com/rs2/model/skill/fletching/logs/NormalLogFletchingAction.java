/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.fletching.logs;

import com.rs2.model.player.Player;
import com.rs2.model.skill.fletching.logs.LogFletchingAction;
import com.rs2.model.skill.fletching.logs.NormalLogFletchingRecipe;

public final class NormalLogFletchingAction
extends LogFletchingAction {
    private NormalLogFletchingAction(Player player, int n, int n2, int n3, double d, int n4, int n5) {
        super(player, n, n2, n3, d, n4, n5);
    }

    public static NormalLogFletchingAction create(Player player, int n, int n2) {
        NormalLogFletchingRecipe normalLogFletchingRecipe = NormalLogFletchingRecipe.forButtonId(n);
        if (normalLogFletchingRecipe == null || normalLogFletchingRecipe.getMenuQuantity() == 0 && n2 == 0) {
            return null;
        }
        return new NormalLogFletchingAction(player, normalLogFletchingRecipe.getLogItemId(), normalLogFletchingRecipe.getProductItemId(), normalLogFletchingRecipe.getRequiredLevel(), normalLogFletchingRecipe.getExperience(), normalLogFletchingRecipe.getMenuQuantity(), n2);
    }
}

