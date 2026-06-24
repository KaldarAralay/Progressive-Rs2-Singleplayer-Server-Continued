/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.fletching.logs;

import com.rs2.model.player.Player;
import com.rs2.model.skill.fletching.logs.LogFletchingAction;
import com.rs2.model.skill.fletching.logs.WillowLogFletchingRecipe;

public final class WillowLogFletchingAction
extends LogFletchingAction {
    private WillowLogFletchingAction(Player player, int n, int n2, int n3, double d, int n4, int n5) {
        super(player, n, n2, n3, d, n4, n5);
    }

    public static WillowLogFletchingAction create(Player player, int n, int n2) {
        WillowLogFletchingRecipe willowLogFletchingRecipe = WillowLogFletchingRecipe.forButtonId(n);
        if (willowLogFletchingRecipe == null || willowLogFletchingRecipe.getMenuQuantity() == 0 && n2 == 0) {
            return null;
        }
        return new WillowLogFletchingAction(player, willowLogFletchingRecipe.getLogItemId(), willowLogFletchingRecipe.getProductItemId(), willowLogFletchingRecipe.getRequiredLevel(), willowLogFletchingRecipe.getExperience(), willowLogFletchingRecipe.getMenuQuantity(), n2);
    }
}

