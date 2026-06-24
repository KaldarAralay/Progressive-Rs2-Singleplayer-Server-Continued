/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay.duel;

import com.rs2.model.gameplay.duel.DuelRule;
import com.rs2.model.player.Player;

public final class ObstaclesDuelRule
extends DuelRule {
    public ObstaclesDuelRule(int n2, int n3, int n4) {
        super(n2, n3, n4);
    }

    @Override
    public final void toggleForPlayer(Player player, boolean bl) {
        boolean bl2;
        boolean bl3 = bl;
        Player player2 = player;
        if (NO_MOVEMENT.isEnabledFor(player2)) {
            if (bl3) {
                player2.packetSender.sendGameMessage("You can't have no movement and obstacles");
            }
            bl2 = false;
        } else {
            bl2 = true;
        }
        if (bl2) {
            player.getDuelInterfaceManager().toggleRule(this.ruleIndex, "There will be obstacles in the arena.");
        }
    }

    @Override
    public final boolean isEnabledFor(Player player) {
        return player.getDuelSession().getEnabledRules()[this.ruleIndex];
    }
}

