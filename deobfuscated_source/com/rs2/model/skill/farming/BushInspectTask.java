/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.model.skill.farming.BushGrowthDefinition;
import com.rs2.model.skill.farming.BushPatch;
import com.rs2.model.skill.farming.BushPatchManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class BushInspectTask
extends CycleEvent {
    private /* synthetic */ BushPatchManager manager;
    private final /* synthetic */ BushPatch patch;
    private final /* synthetic */ BushGrowthDefinition growthDefinition;

    public BushInspectTask(BushPatchManager bushPatchManager, BushPatch bushPatch, BushGrowthDefinition bushGrowthDefinition) {
        this.manager = bushPatchManager;
        this.patch = bushPatch;
        this.growthDefinition = bushGrowthDefinition;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        int n = this.manager.growthStages[this.patch.getIndex()] - 4;
        if (this.growthDefinition.getGrowthMessages().length > n) {
            BushPatchManager.getPlayer(this.manager).getDialogueManager().showStatement(this.growthDefinition.getGrowthMessages()[n]);
        } else {
            BushPatchManager.getPlayer(this.manager).getDialogueManager().showStatement(this.growthDefinition.getGrowthMessages()[this.growthDefinition.getGrowthMessages().length - 1]);
        }
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
        BushPatchManager.getPlayer(this.manager).getUpdateState().setAnimation(1332);
        BushPatchManager.getPlayer(this.manager).setActionLocked(false);
    }
}

