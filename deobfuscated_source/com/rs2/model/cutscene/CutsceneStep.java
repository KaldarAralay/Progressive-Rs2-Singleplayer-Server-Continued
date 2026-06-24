/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.cutscene;

import com.rs2.model.cutscene.Cutscene;

public class CutsceneStep {
    private int delayTicks;

    public CutsceneStep(Cutscene cutscene, int n) {
        this.delayTicks = n;
    }

    public final int getDelayTicks() {
        return this.delayTicks;
    }

    public void executeStep() {
    }
}

