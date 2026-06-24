/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.Server;
import com.rs2.model.skill.farming.FlowerDefinition;
import com.rs2.model.skill.farming.FlowerPatch;
import com.rs2.model.skill.farming.FlowerPatchManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class FlowerPlantingTask
extends CycleEvent {
    private /* synthetic */ FlowerPatchManager manager;
    private final /* synthetic */ FlowerPatch patch;
    private final /* synthetic */ int seedId;
    private final /* synthetic */ FlowerDefinition definition;

    public FlowerPlantingTask(FlowerPatchManager flowerPatchManager, FlowerPatch flowerPatch, int n, FlowerDefinition flowerDefinition) {
        this.manager = flowerPatchManager;
        this.patch = flowerPatch;
        this.seedId = n;
        this.definition = flowerDefinition;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        this.manager.patchStates[this.patch.getIndex()] = 0;
        this.manager.cropIds[this.patch.getIndex()] = this.seedId;
        this.manager.lastUpdateTicks[this.patch.getIndex()] = Server.getElapsedMinutes();
        FlowerPatchManager.getPlayer(this.manager).getSkillManager().addExperience(19, this.definition.getPlantingExperience());
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
        this.manager.refreshConfig();
        FlowerPatchManager.getPlayer(this.manager).setActionLocked(false);
    }
}

