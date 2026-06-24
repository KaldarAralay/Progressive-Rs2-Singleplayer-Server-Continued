/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.Server;
import com.rs2.model.skill.farming.HerbDefinition;
import com.rs2.model.skill.farming.HerbPatch;
import com.rs2.model.skill.farming.HerbPatchManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class HerbPlantingTask
extends CycleEvent {
    private /* synthetic */ HerbPatchManager manager;
    private final /* synthetic */ HerbPatch patch;
    private final /* synthetic */ int seedId;
    private final /* synthetic */ HerbDefinition definition;

    public HerbPlantingTask(HerbPatchManager herbPatchManager, HerbPatch herbPatch, int n, HerbDefinition herbDefinition) {
        this.manager = herbPatchManager;
        this.patch = herbPatch;
        this.seedId = n;
        this.definition = herbDefinition;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        this.manager.patchStates[this.patch.getIndex()] = 0;
        this.manager.growthStages[this.patch.getIndex()] = 4;
        this.manager.cropIds[this.patch.getIndex()] = this.seedId;
        this.manager.lastUpdateTicks[this.patch.getIndex()] = Server.getElapsedMinutes();
        HerbPatchManager.getPlayer(this.manager).getSkillManager().addExperience(19, this.definition.getPlantingExperience());
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
        this.manager.refreshConfig();
        HerbPatchManager.getPlayer(this.manager).setActionLocked(false);
        HerbPatchManager.getPlayer(this.manager).resetAnimation();
    }
}

