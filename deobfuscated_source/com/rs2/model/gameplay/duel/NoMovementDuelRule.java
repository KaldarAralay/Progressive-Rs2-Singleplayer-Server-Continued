/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay.duel;

import com.rs2.model.gameplay.duel.DuelRule;
import com.rs2.model.player.Player;

public final class NoMovementDuelRule
extends DuelRule {
    public NoMovementDuelRule(int n2, int n3, int n4) {
        super(n2, n3, n4);
    }

    @Override
    public final void toggleForPlayer(Player player, boolean bl) {
        boolean bl2;
        boolean bl3 = bl;
        Player player2 = player;
        if (OBSTACLES.isEnabledFor(player2)) {
            if (bl3) {
                player2.packetSender.sendGameMessage("You can't have no movement and obstacles");
            }
            bl2 = false;
        } else if (NO_FORFEIT.isEnabledFor(player2)) {
            if (bl3) {
                player2.packetSender.sendGameMessage("You can't have no forfeit and no movement - you could run out of ammo");
            }
            bl2 = false;
        } else {
            bl2 = true;
        }
        if (bl2) {
            player.getDuelInterfaceManager().toggleRule(this.ruleIndex, "Players cannot move.");
        }
    }

    @Override
    public final boolean isEnabledFor(Player player) {
        return player.getDuelSession().getEnabledRules()[this.ruleIndex];
    }
}

