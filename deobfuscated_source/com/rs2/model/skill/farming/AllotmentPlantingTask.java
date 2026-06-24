/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.Server;
import com.rs2.model.skill.farming.AllotmentCropDefinition;
import com.rs2.model.skill.farming.AllotmentPatch;
import com.rs2.model.skill.farming.AllotmentPatchManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class AllotmentPlantingTask
extends CycleEvent {
    private /* synthetic */ AllotmentPatchManager manager;
    private final /* synthetic */ AllotmentPatch patch;
    private final /* synthetic */ int seedId;
    private final /* synthetic */ AllotmentCropDefinition definition;

    public AllotmentPlantingTask(AllotmentPatchManager allotmentPatchManager, AllotmentPatch allotmentPatch, int n, AllotmentCropDefinition allotmentCropDefinition) {
        this.manager = allotmentPatchManager;
        this.patch = allotmentPatch;
        this.seedId = n;
        this.definition = allotmentCropDefinition;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        this.manager.patchStates[this.patch.getIndex()] = 0;
        this.manager.cropIds[this.patch.getIndex()] = this.seedId;
        this.manager.lastUpdateTicks[this.patch.getIndex()] = Server.getElapsedMinutes();
        AllotmentPatchManager.getPlayer(this.manager).getSkillManager().addExperience(19, this.definition.getPlantingExperience());
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
        this.manager.refreshConfig();
        AllotmentPatchManager.getPlayer(this.manager).setActionLocked(false);
    }
}

