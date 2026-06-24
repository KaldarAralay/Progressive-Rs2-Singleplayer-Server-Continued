/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay.duel;

import com.rs2.model.gameplay.duel.DuelRule;
import com.rs2.model.player.Player;

public final class NoFoodDuelRule
extends DuelRule {
    public NoFoodDuelRule(int n2, int n3, int n4) {
        super(n2, n3, n4);
    }

    @Override
    public final void toggleForPlayer(Player player, boolean bl) {
        player.getDuelInterfaceManager().toggleRule(this.ruleIndex, "Players cannot eat foods.");
    }

    @Override
    public final boolean isEnabledFor(Player player) {
        return player.getDuelSession().getEnabledRules()[this.ruleIndex];
    }
}

