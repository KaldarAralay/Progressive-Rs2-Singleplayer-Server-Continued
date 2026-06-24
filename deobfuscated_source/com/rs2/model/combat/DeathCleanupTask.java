/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.combat;

import com.rs2.model.Entity;
import com.rs2.model.combat.CombatManager;
import com.rs2.model.task.TickTask;

public final class DeathCleanupTask
extends TickTask {
    private final /* synthetic */ Entity defeatedEntity;
    private final /* synthetic */ Entity killer;

    public DeathCleanupTask(int n, Entity entity, Entity entity2) {
        super(n);
        this.defeatedEntity = entity;
        this.killer = entity2;
    }

    @Override
    public final void execute() {
        CombatManager.finishDeath(this.defeatedEntity, this.killer, true);
        this.stop();
    }
}

