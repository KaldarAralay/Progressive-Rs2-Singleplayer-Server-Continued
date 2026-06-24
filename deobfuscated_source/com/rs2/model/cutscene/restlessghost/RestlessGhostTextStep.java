/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.cutscene.restlessghost;

import com.rs2.model.cutscene.Cutscene;
import com.rs2.model.cutscene.CutsceneStep;
import com.rs2.model.cutscene.restlessghost.RestlessGhostCutscene;

public final class RestlessGhostTextStep
extends CutsceneStep {
    private /* synthetic */ RestlessGhostCutscene cutscene;

    public RestlessGhostTextStep(RestlessGhostCutscene restlessGhostCutscene, Cutscene cutscene, int n) {
        super(cutscene, 3);
        this.cutscene = restlessGhostCutscene;
    }

    @Override
    public final void executeStep() {
        this.cutscene.ghost.getUpdateState().setForcedText("stranger..");
    }
}

