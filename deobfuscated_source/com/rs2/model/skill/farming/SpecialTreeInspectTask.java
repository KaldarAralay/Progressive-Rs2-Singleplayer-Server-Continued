/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.model.skill.farming.SpecialTreeGrowthDefinition;
import com.rs2.model.skill.farming.SpecialTreePatch;
import com.rs2.model.skill.farming.SpecialTreePatchManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class SpecialTreeInspectTask
extends CycleEvent {
    private /* synthetic */ SpecialTreePatchManager manager;
    private final /* synthetic */ SpecialTreePatch patch;
    private final /* synthetic */ SpecialTreeGrowthDefinition growthDefinition;

    public SpecialTreeInspectTask(SpecialTreePatchManager specialTreePatchManager, SpecialTreePatch specialTreePatch, SpecialTreeGrowthDefinition specialTreeGrowthDefinition) {
        this.manager = specialTreePatchManager;
        this.patch = specialTreePatch;
        this.growthDefinition = specialTreeGrowthDefinition;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        int n = this.manager.growthStages[this.patch.getIndex()] - 4;
        if (this.growthDefinition.getGrowthMessages().length > n) {
            SpecialTreePatchManager.getPlayer(this.manager).getDialogueManager().showStatement(this.growthDefinition.getGrowthMessages()[n]);
        } else {
            SpecialTreePatchManager.getPlayer(this.manager).getDialogueManager().showStatement(this.growthDefinition.getGrowthMessages()[this.growthDefinition.getGrowthMessages().length - 1]);
        }
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
        SpecialTreePatchManager.getPlayer(this.manager).getUpdateState().setAnimation(1332);
        SpecialTreePatchManager.getPlayer(this.manager).setActionLocked(false);
    }
}

