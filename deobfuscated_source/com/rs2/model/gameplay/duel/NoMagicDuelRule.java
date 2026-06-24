/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay.duel;

import com.rs2.model.gameplay.duel.DuelRule;
import com.rs2.model.player.Player;

public final class NoMagicDuelRule
extends DuelRule {
    public NoMagicDuelRule(int n2, int n3, int n4) {
        super(n2, n3, n4);
    }

    @Override
    public final void toggleForPlayer(Player player, boolean bl) {
        boolean bl2;
        boolean bl3 = bl;
        Player player2 = player;
        if (NO_RANGED.isEnabledFor(player2) && NO_MELEE.isEnabledFor(player2)) {
            if (bl3) {
                player2.packetSender.sendGameMessage("You can't have no ranged, no melee and no magic - how would you fight?");
            }
            bl2 = false;
        } else {
            bl2 = true;
        }
        if (bl2) {
            player.getDuelInterfaceManager().toggleRule(this.ruleIndex, "Players cannot use magic");
        }
    }

    @Override
    public final boolean isEnabledFor(Player player) {
        return player.getDuelSession().getEnabledRules()[this.ruleIndex];
    }
}

