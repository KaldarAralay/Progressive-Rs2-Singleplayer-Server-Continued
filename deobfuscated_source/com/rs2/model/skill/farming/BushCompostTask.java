/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.model.skill.farming.BushPatch;
import com.rs2.model.skill.farming.BushPatchManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class BushCompostTask
extends CycleEvent {
    private /* synthetic */ BushPatchManager manager;
    private final /* synthetic */ BushPatch patch;
    private final /* synthetic */ int compostItemId;

    public BushCompostTask(BushPatchManager bushPatchManager, BushPatch bushPatch, int n) {
        this.manager = bushPatchManager;
        this.patch = bushPatch;
        this.compostItemId = n;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        int n = this.patch.getIndex();
        this.manager.diseaseChanceMultipliers[n] = this.manager.diseaseChanceMultipliers[n] * (this.compostItemId == 6032 ? 0.35 : 0.1);
        this.manager.patchStates[this.patch.getIndex()] = 5;
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
        BushPatchManager.getPlayer(this.manager).setActionLocked(false);
        BushPatchManager.getPlayer(this.manager).resetAnimation();
    }
}

