/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.model.skill.farming.HopsGrowthDefinition;
import com.rs2.model.skill.farming.HopsPatch;
import com.rs2.model.skill.farming.HopsPatchManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class HopsInspectTask
extends CycleEvent {
    private /* synthetic */ HopsPatchManager manager;
    private final /* synthetic */ HopsPatch patch;
    private final /* synthetic */ HopsGrowthDefinition growthDefinition;

    public HopsInspectTask(HopsPatchManager hopsPatchManager, HopsPatch hopsPatch, HopsGrowthDefinition hopsGrowthDefinition) {
        this.manager = hopsPatchManager;
        this.patch = hopsPatch;
        this.growthDefinition = hopsGrowthDefinition;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        int n = this.manager.growthStages[this.patch.getIndex()] - 4;
        if (this.growthDefinition.getGrowthMessages().length > n) {
            HopsPatchManager.getPlayer(this.manager).getDialogueManager().showStatement(this.growthDefinition.getGrowthMessages()[n]);
        } else {
            HopsPatchManager.getPlayer(this.manager).getDialogueManager().showStatement(this.growthDefinition.getGrowthMessages()[this.growthDefinition.getGrowthMessages().length - 1]);
        }
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
        HopsPatchManager.getPlayer(this.manager).getUpdateState().setAnimation(1332);
        HopsPatchManager.getPlayer(this.manager).setActionLocked(false);
    }
}

