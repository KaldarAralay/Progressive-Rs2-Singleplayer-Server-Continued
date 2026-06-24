/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.fletching.logs;

import com.rs2.model.player.Player;
import com.rs2.model.skill.fletching.logs.LogFletchingAction;
import com.rs2.model.skill.fletching.logs.MapleLogFletchingRecipe;

public final class MapleLogFletchingAction
extends LogFletchingAction {
    private MapleLogFletchingAction(Player player, int n, int n2, int n3, double d, int n4, int n5) {
        super(player, n, n2, n3, d, n4, n5);
    }

    public static MapleLogFletchingAction create(Player player, int n, int n2) {
        MapleLogFletchingRecipe mapleLogFletchingRecipe = MapleLogFletchingRecipe.forButtonId(n);
        if (mapleLogFletchingRecipe == null || mapleLogFletchingRecipe.getMenuQuantity() == 0 && n2 == 0) {
            return null;
        }
        return new MapleLogFletchingAction(player, mapleLogFletchingRecipe.getLogItemId(), mapleLogFletchingRecipe.getProductItemId(), mapleLogFletchingRecipe.getRequiredLevel(), mapleLogFletchingRecipe.getExperience(), mapleLogFletchingRecipe.getMenuQuantity(), n2);
    }
}

