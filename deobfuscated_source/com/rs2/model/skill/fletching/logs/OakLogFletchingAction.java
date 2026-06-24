/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.fletching.logs;

import com.rs2.model.player.Player;
import com.rs2.model.skill.fletching.logs.LogFletchingAction;
import com.rs2.model.skill.fletching.logs.OakLogFletchingRecipe;

public final class OakLogFletchingAction
extends LogFletchingAction {
    private OakLogFletchingAction(Player player, int n, int n2, int n3, double d, int n4, int n5) {
        super(player, n, n2, n3, d, n4, n5);
    }

    public static OakLogFletchingAction create(Player player, int n, int n2) {
        OakLogFletchingRecipe oakLogFletchingRecipe = OakLogFletchingRecipe.forButtonId(n);
        if (oakLogFletchingRecipe == null || oakLogFletchingRecipe.getMenuQuantity() == 0 && n2 == 0) {
            return null;
        }
        return new OakLogFletchingAction(player, oakLogFletchingRecipe.getLogItemId(), oakLogFletchingRecipe.getProductItemId(), oakLogFletchingRecipe.getRequiredLevel(), oakLogFletchingRecipe.getExperience(), oakLogFletchingRecipe.getMenuQuantity(), n2);
    }
}

