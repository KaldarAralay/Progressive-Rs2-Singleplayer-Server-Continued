/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.magic;

import com.rs2.model.Entity;
import com.rs2.model.Position;
import com.rs2.model.combat.hit.HitDefinition;
import com.rs2.model.player.Player;
import com.rs2.model.skill.magic.MagicSpellAction;
import com.rs2.model.task.TickTask;

public final class DelayedSpellImpactTask
extends TickTask {
    private /* synthetic */ MagicSpellAction spellAction;
    private final /* synthetic */ HitDefinition hit;
    private final /* synthetic */ Entity target;
    private final /* synthetic */ Position targetPosition;

    public DelayedSpellImpactTask(MagicSpellAction magicSpellAction, int n, HitDefinition hitDefinition, Entity entity, Position position) {
        super(n);
        this.spellAction = magicSpellAction;
        this.hit = hitDefinition;
        this.target = entity;
        this.targetPosition = position;
    }

    @Override
    public final void execute() {
        MagicSpellAction.finishDelayedImpact(this.spellAction, this, this.hit);
        if (this.hit.getGraphic() != null) {
            if (this.target != null) {
                this.target.getUpdateState().setGraphic(this.hit.getGraphic());
                return;
            }
            Player player = MagicSpellAction.getPlayer(this.spellAction);
            player.packetSender.sendStillGraphic(this.hit.getGraphic().getId(), this.targetPosition, this.hit.getGraphic().getPackedDelay());
        }
    }
}

