/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.model.skill.farming.FlowerGrowthDefinition;
import com.rs2.model.skill.farming.FlowerPatch;
import com.rs2.model.skill.farming.FlowerPatchManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class FlowerInspectTask
extends CycleEvent {
    private /* synthetic */ FlowerPatchManager manager;
    private final /* synthetic */ FlowerPatch patch;
    private final /* synthetic */ FlowerGrowthDefinition growthDefinition;

    public FlowerInspectTask(FlowerPatchManager flowerPatchManager, FlowerPatch flowerPatch, FlowerGrowthDefinition flowerGrowthDefinition) {
        this.manager = flowerPatchManager;
        this.patch = flowerPatch;
        this.growthDefinition = flowerGrowthDefinition;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        int n = this.manager.growthStages[this.patch.getIndex()] - 4;
        FlowerPatchManager.getPlayer(this.manager).getDialogueManager().showStatement(this.growthDefinition.getGrowthMessages()[n]);
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
        FlowerPatchManager.getPlayer(this.manager).getUpdateState().setAnimation(1332);
        FlowerPatchManager.getPlayer(this.manager).setActionLocked(false);
    }
}

