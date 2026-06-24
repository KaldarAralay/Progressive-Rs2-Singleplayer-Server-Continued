/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.combat.requirement;

import com.rs2.model.Entity;
import com.rs2.model.player.Player;

public abstract class CombatRequirement {
    public abstract String getFailureMessage();

    abstract boolean isSatisfiedBy(Entity var1);

    public final boolean validate(Entity entity) {
        boolean bl = this.isSatisfiedBy(entity);
        String string = this.getFailureMessage();
        if (!bl && entity.isPlayer()) {
            entity = (Player)entity;
            ((Player)entity).packetSender.sendGameMessage(string);
        }
        return bl;
    }
}

