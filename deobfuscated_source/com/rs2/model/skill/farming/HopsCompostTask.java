/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.model.skill.farming.HopsPatch;
import com.rs2.model.skill.farming.HopsPatchManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class HopsCompostTask
extends CycleEvent {
    private /* synthetic */ HopsPatchManager manager;
    private final /* synthetic */ HopsPatch patch;
    private final /* synthetic */ int compostItemId;

    public HopsCompostTask(HopsPatchManager hopsPatchManager, HopsPatch hopsPatch, int n) {
        this.manager = hopsPatchManager;
        this.patch = hopsPatch;
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
        HopsPatchManager.getPlayer(this.manager).setActionLocked(false);
        HopsPatchManager.getPlayer(this.manager).resetAnimation();
    }
}

