/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.cutscene;

import com.rs2.model.cutscene.Cutscene;
import com.rs2.model.cutscene.CutsceneStep;
import com.rs2.model.task.TickTask;

public final class CutsceneStepTask
extends TickTask {
    private final /* synthetic */ CutsceneStep step;

    public CutsceneStepTask(Cutscene cutscene, int n, CutsceneStep cutsceneStep) {
        super(n);
        this.step = cutsceneStep;
    }

    @Override
    public final void execute() {
        this.step.executeStep();
        this.stop();
    }
}

