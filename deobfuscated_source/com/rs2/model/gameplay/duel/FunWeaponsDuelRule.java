/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay.duel;

import com.rs2.ServerSettings;
import com.rs2.model.gameplay.duel.DuelRule;
import com.rs2.model.player.Player;

public final class FunWeaponsDuelRule
extends DuelRule {
    public FunWeaponsDuelRule(int n2, int n3, int n4) {
        super(n2, n3, n4);
    }

    @Override
    public final void toggleForPlayer(Player player, boolean bl) {
        boolean bl2 = bl;
        Player player2 = player;
        boolean bl3 = false;
        int[] nArray = ServerSettings.FUN_WEAPON_IDS;
        int n = 0;
        while (n < 13) {
            int n2 = nArray[n];
            if (player2.getInventoryManager().getContainer().containsItem(n2)) {
                bl3 = true;
            }
            ++n;
        }
        if (!bl3 && bl2) {
            player2.packetSender.sendGameMessage("Neither player has a 'fun weapon' for that.");
        }
        if (bl3) {
            player.getDuelInterfaceManager().toggleRule(this.ruleIndex, "'Fun weapons' will be allowed.");
        }
    }

    @Override
    public final boolean isEnabledFor(Player player) {
        return player.getDuelSession().getEnabledRules()[this.ruleIndex];
    }
}

