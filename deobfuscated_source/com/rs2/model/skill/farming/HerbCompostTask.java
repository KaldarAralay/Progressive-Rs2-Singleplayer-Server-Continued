/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.model.skill.farming.HerbPatch;
import com.rs2.model.skill.farming.HerbPatchManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class HerbCompostTask
extends CycleEvent {
    private /* synthetic */ HerbPatchManager manager;
    private final /* synthetic */ HerbPatch patch;
    private final /* synthetic */ int compostItemId;

    public HerbCompostTask(HerbPatchManager herbPatchManager, HerbPatch herbPatch, int n) {
        this.manager = herbPatchManager;
        this.patch = herbPatch;
        this.compostItemId = n;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        int n = this.patch.getIndex();
        this.manager.diseaseChanceMultipliers[n] = this.manager.diseaseChanceMultipliers[n] * (this.compostItemId == 6032 ? 0.52 : 0.22);
        this.manager.patchStates[this.patch.getIndex()] = 4;
        int n2 = this.patch.getIndex();
        this.manager.harvestAmounts[n2] = this.manager.harvestAmounts[n2] + (this.compostItemId == 6032 ? 1 : 2);
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
        HerbPatchManager.getPlayer(this.manager).setActionLocked(false);
        HerbPatchManager.getPlayer(this.manager).resetAnimation();
    }
}

