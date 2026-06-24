/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.prayer;

import com.rs2.model.Entity;
import com.rs2.model.World;
import com.rs2.model.combat.AttackValidationResult;
import com.rs2.model.combat.CombatAction;
import com.rs2.model.combat.CombatCycleEvent;
import com.rs2.model.combat.hit.HitDefinition;
import com.rs2.model.player.Player;
import com.rs2.model.task.TickTask;
import com.rs2.util.GameUtil;

public final class RetributionPrayerTask
extends TickTask {
    private final /* synthetic */ Player player;
    private final /* synthetic */ boolean multiCombat;
    private final /* synthetic */ Entity source;
    private final /* synthetic */ Entity primaryTarget;
    private final /* synthetic */ HitDefinition hitDefinition;

    public RetributionPrayerTask(int n, Player player, boolean bl, Entity entity, Entity entity2, HitDefinition hitDefinition) {
        super(3);
        this.player = player;
        this.multiCombat = bl;
        this.source = entity;
        this.primaryTarget = entity2;
        this.hitDefinition = hitDefinition;
    }

    @Override
    public final void execute() {
        AttackValidationResult attackValidationResult;
        Entity entity;
        this.player.getUpdateState().setGraphic(437, 0);
        if (!this.multiCombat) {
            new CombatAction(this.source, this.primaryTarget, this.hitDefinition).queue();
            this.stop();
            return;
        }
        Entity[] entityArray = World.getPlayers();
        int n = entityArray.length;
        int n2 = 0;
        while (n2 < n) {
            entity = entityArray[n2];
            if (entity != null && entity != this.source) {
                attackValidationResult = CombatCycleEvent.validateAttack(this.source, entity);
                if (GameUtil.getDistance(this.source.getPosition(), entity.getPosition()) <= 1 && attackValidationResult == AttackValidationResult.VALID) {
                    new CombatAction(this.source, entity, this.hitDefinition).queue();
                    if (!this.multiCombat) {
                        this.stop();
                        return;
                    }
                }
            }
            ++n2;
        }
        entityArray = World.getNpcs();
        n = entityArray.length;
        n2 = 0;
        while (n2 < n) {
            entity = entityArray[n2];
            if (entity != null) {
                attackValidationResult = CombatCycleEvent.validateAttack(this.source, entity);
                if (this.source.isWithinReach(entity, 1) && attackValidationResult == AttackValidationResult.VALID) {
                    new CombatAction(this.source, entity, this.hitDefinition).queue();
                    if (!this.multiCombat) {
                        this.stop();
                        return;
                    }
                }
            }
            ++n2;
        }
        this.stop();
    }
}

