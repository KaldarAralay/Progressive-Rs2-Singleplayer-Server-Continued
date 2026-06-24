/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.model.skill.farming.AllotmentPatch;
import com.rs2.model.skill.farming.AllotmentPatchManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class AllotmentCompostTask
extends CycleEvent {
    private /* synthetic */ AllotmentPatchManager manager;
    private final /* synthetic */ AllotmentPatch patch;
    private final /* synthetic */ int compostItemId;

    public AllotmentCompostTask(AllotmentPatchManager allotmentPatchManager, AllotmentPatch allotmentPatch, int n) {
        this.manager = allotmentPatchManager;
        this.patch = allotmentPatch;
        this.compostItemId = n;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        int n = this.patch.getIndex();
        this.manager.diseaseChanceMultipliers[n] = this.manager.diseaseChanceMultipliers[n] * (this.compostItemId == 6032 ? 0.35 : 0.1);
        this.manager.patchStates[this.patch.getIndex()] = 5;
        int n2 = this.patch.getIndex();
        this.manager.harvestAmounts[n2] = this.manager.harvestAmounts[n2] + (this.compostItemId == 6032 ? 1 : 2);
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
        AllotmentPatchManager.getPlayer(this.manager).setActionLocked(false);
        AllotmentPatchManager.getPlayer(this.manager).resetAnimation();
    }
}

