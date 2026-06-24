/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.combat;

import com.rs2.model.Entity;
import com.rs2.model.npc.Npc;
import com.rs2.model.player.Player;
import com.rs2.model.task.TickTask;

public final class DeathAnimationTask
extends TickTask {
    private final /* synthetic */ int deathAnimationId;
    private final /* synthetic */ Entity defeatedEntity;
    private final /* synthetic */ Entity killer;

    public DeathAnimationTask(int n, int n2, Entity entity, Entity entity2) {
        super(2);
        this.deathAnimationId = n2;
        this.defeatedEntity = entity;
        this.killer = entity2;
    }

    @Override
    public final void execute() {
        Player player;
        int n = this.deathAnimationId;
        if (this.defeatedEntity.isPlayer()) {
            player = (Player)this.defeatedEntity;
            player.setHideHeldItemsInAppearance(true);
            this.defeatedEntity.getUpdateState().setFaceEntity(-1);
            player.setAppearanceUpdateRequired(true);
            if (player.npcTransformationId > 0) {
                n = new Npc(player.npcTransformationId).getDefinition().getDeathAnimationId();
            }
        }
        this.defeatedEntity.getUpdateState().setAnimation(n);
        if (this.defeatedEntity != null && this.killer != null && this.defeatedEntity.isNpc() && this.killer.isPlayer()) {
            player = (Player)this.killer;
            Npc npc = (Npc)this.defeatedEntity;
            this.defeatedEntity.getUpdateState().setFaceEntity(-1);
            player.packetSender.sendSoundEffect(npc.getDeathSoundId(), 1, 20);
        }
        this.stop();
    }
}

