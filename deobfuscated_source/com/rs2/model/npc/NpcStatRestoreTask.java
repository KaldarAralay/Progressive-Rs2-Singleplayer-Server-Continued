/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.npc;

import com.rs2.model.npc.Npc;
import com.rs2.model.task.TickTask;

public final class NpcStatRestoreTask
extends TickTask {
    private /* synthetic */ Npc a;
    private final /* synthetic */ Npc b;
    private final /* synthetic */ int skillId;

    public NpcStatRestoreTask(Npc npc, int n, Npc npc2, int n2) {
        super(100);
        this.a = npc;
        this.b = npc2;
        this.skillId = n2;
    }

    @Override
    public final void execute() {
        if (this.b.isDead()) {
            this.stop();
            return;
        }
        if (this.a.getCurrentLevelForSkill(this.skillId) != this.a.getBaseLevelForSkill(this.skillId)) {
            if (this.a.getCurrentLevelForSkill(this.skillId) > this.a.getBaseLevelForSkill(this.skillId)) {
                this.a.adjustCurrentLevel(this.skillId, -1);
            } else {
                this.a.adjustCurrentLevel(this.skillId, 1);
            }
        }
        if (!this.a.isStatModified(this.skillId)) {
            this.stop();
        }
    }
}

