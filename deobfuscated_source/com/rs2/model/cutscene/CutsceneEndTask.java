/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.cutscene;

import com.rs2.model.cutscene.Cutscene;
import com.rs2.model.task.TickTask;

public final class CutsceneEndTask
extends TickTask {
    private /* synthetic */ Cutscene cutscene;

    public CutsceneEndTask(Cutscene cutscene, int n) {
        super(n);
        this.cutscene = cutscene;
    }

    @Override
    public final void execute() {
        this.cutscene.finishCutscene();
        this.stop();
    }
}

