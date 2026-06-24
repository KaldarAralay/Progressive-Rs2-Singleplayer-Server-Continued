/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.fletching.logs;

import com.rs2.model.player.Player;
import com.rs2.model.skill.fletching.logs.AcheyLogFletchingRecipe;
import com.rs2.model.skill.fletching.logs.LogFletchingAction;

public final class AcheyLogFletchingAction
extends LogFletchingAction {
    private AcheyLogFletchingAction(Player player, int n, int n2, int n3, double d, int n4, int n5) {
        super(player, n, n2, n3, d, n4, n5);
    }

    public static AcheyLogFletchingAction create(Player player, int n, int n2) {
        AcheyLogFletchingRecipe acheyLogFletchingRecipe = AcheyLogFletchingRecipe.forButtonId(n);
        if (acheyLogFletchingRecipe == null || acheyLogFletchingRecipe.getMenuQuantity() == 0 && n2 == 0) {
            return null;
        }
        return new AcheyLogFletchingAction(player, acheyLogFletchingRecipe.getLogItemId(), acheyLogFletchingRecipe.getProductItemId(), acheyLogFletchingRecipe.getRequiredLevel(), acheyLogFletchingRecipe.getExperience(), acheyLogFletchingRecipe.getMenuQuantity(), n2);
    }
}

