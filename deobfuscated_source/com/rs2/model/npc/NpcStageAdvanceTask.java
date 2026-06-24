/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.npc;

import com.rs2.model.npc.Npc;
import com.rs2.model.task.TickTask;

public final class NpcStageAdvanceTask
extends TickTask {
    private /* synthetic */ Npc npc;

    public NpcStageAdvanceTask(Npc npc, int n) {
        super(10);
        this.npc = npc;
    }

    @Override
    public final void execute() {
        ++this.npc.scriptedPathStage;
        this.npc.queueStageAdvancePath(this.npc.scriptedPathStage);
        this.stop();
    }
}

