/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.combat;

import com.rs2.model.Entity;
import com.rs2.model.task.DelayTimer;

public final class CombatTargetDelayTimer
extends DelayTimer {
    private Entity target;

    public CombatTargetDelayTimer(Entity entity, int n) {
        super(0);
    }

    public final void setTargetDelay(Entity entity, int n) {
        this.target = entity;
        this.setDelayTicks(n);
        this.reset();
    }

    public final Entity getTarget() {
        return this.target;
    }
}

