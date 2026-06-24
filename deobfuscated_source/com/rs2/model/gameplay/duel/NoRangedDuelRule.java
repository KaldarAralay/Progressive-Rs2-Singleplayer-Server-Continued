/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay.duel;

import com.rs2.model.combat.CombatType;
import com.rs2.model.gameplay.duel.DuelRule;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;

public final class NoRangedDuelRule
extends DuelRule {
    public NoRangedDuelRule(int n2, int n3, int n4) {
        super(n2, n3, n4);
    }

    @Override
    public final void toggleForPlayer(Player player, boolean bl) {
        boolean bl2;
        boolean bl3 = bl;
        Player player2 = player;
        Object object = this;
        if (NO_MELEE.isEnabledFor(player2) && NO_MAGIC.isEnabledFor(player2)) {
            if (bl3) {
                object = player2;
                ((Player)object).packetSender.sendGameMessage("You can't have no ranged, no melee and no magic - how would you fight?");
            }
            bl2 = false;
        } else {
            if (!((DuelRule)object).isEnabledFor(player2)) {
                if (player2.getDuelSession().getCurrentCombatType() == CombatType.RANGED) {
                    player2.getDuelSession().getEquipmentToRemove().add(new ItemStack(player2.getEquipmentManager().getItemIdAtSlot(3)));
                }
            } else if (player2.getDuelSession().getCurrentCombatType() == CombatType.RANGED) {
                player2.getDuelSession().getEquipmentToRemove().remove(new ItemStack(player2.getEquipmentManager().getItemIdAtSlot(3)));
            }
            bl2 = true;
        }
        if (bl2) {
            player.getDuelInterfaceManager().toggleRule(this.ruleIndex, "Players cannot use range");
        }
    }

    @Override
    public final boolean isEnabledFor(Player player) {
        return player.getDuelSession().getEnabledRules()[this.ruleIndex];
    }
}

